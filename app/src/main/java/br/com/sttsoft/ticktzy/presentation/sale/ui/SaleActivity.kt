package br.com.sttsoft.ticktzy.presentation.sale.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import br.com.execucao.posmp_api.SmartPosHelper
import br.com.execucao.posmp_api.printer.PrinterService
import br.com.execucao.posmp_api.store.AppStatus
import br.com.execucao.smartPOSService.printer.IOnPrintFinished
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivitySaleBinding
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.dialogs.ChangeDialog
import br.com.sttsoft.ticktzy.presentation.dialogs.PaymentTypeChooseDialog
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.presentation.sale.components.ProductAdapter
import br.com.sttsoft.ticktzy.repository.local.product
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService

class SaleActivity: BaseActivity() {

    private val binding: ActivitySaleBinding by lazy {
        ActivitySaleBinding.inflate(layoutInflater)
    }

    private var sunmiPrinterService: SunmiPrinterService? = null

    private lateinit var adapter: ProductAdapter

    private lateinit var productList: List<product>

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var infos: InfoResponse? = null

    private lateinit var printerService: PrinterService

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeSmartPosHelper()
        initializePrinterService()

        infos = this.getFromPrefs("SITEF_INFOS")

        initPrinter()

        initActivityResultLaucher()

        setSearchBarListener()
        setOnClickListeners()

        getProducts()
    }

    private fun initPrinter() {
        InnerPrinterManager.getInstance().bindService(this, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService) {
                sunmiPrinterService = service
            }

            override fun onDisconnected() {
                sunmiPrinterService = null
            }
        })
    }

    private fun initActivityResultLaucher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val bundle = data?.extras
            if (bundle != null) {
                if (result.resultCode == RESULT_OK) {
                    this.savePref("SALES_MADE", this.getPref("SALES_MADE", 0) + 1)
                    val comprovanteEstab = bundle.getString("VIA_ESTABELECIMENTO")
                    if (comprovanteEstab != null && comprovanteEstab.trim { it <= ' ' }.isNotEmpty()) {
                        printReceipt(comprovanteEstab)
                    }

                    val dialog = ConfirmDialog ({ option ->
                        when (option) {
                            "yes" -> {
                                val comprovanteCliente = bundle.getString("VIA_CLIENTE")
                                if (comprovanteCliente != null && comprovanteCliente.trim { it <= ' ' }.isNotEmpty()) {
                                    printReceipt(comprovanteCliente)
                                }
                                printTickets()
                            }
                            "no" -> {
                                printTickets()
                            }
                        }
                    }, getString(R.string.dialog_print_question_title), getString(R.string.dialog_print_question_body))
                    dialog.show(supportFragmentManager, "PrintQuestionDialog")
                }
            } else {
                Toast.makeText(this, "Nenhum dado retornado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getProducts() {
        showLoading()
        val produtos = ProductCacheUseCase(this).lerProdutos()

        if (produtos.isNotEmpty()) {
            productList = produtos
            setAdapter()
            hideLoading()
        }
    }

    private fun setAdapter() {
        binding.rclProducts.layoutManager = GridLayoutManager(this, 3)

        adapter = ProductAdapter(productList) { total ->
            binding.paymentBar.setTotalText(getString(R.string.text_sale_price).format(total))
        }

        binding.rclProducts.adapter = adapter
    }

    private fun setSearchBarListener() {
        binding.searchBar.addOnTextChangedListener { query -> adapter.filter(query) }
    }

    private fun setOnClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.paymentBar.setOnCashClick {
            if (verifyTotal()) {
                ChangeDialog(this, adapter.getTotal()) { valorRecebido, troco, dialog ->

                    if (valorRecebido > 0.0 && valorRecebido >= adapter.getTotal()) {

                        this.savePref("SALES_MADE", this.getPref("SALES_MADE", 0) + 1)
                        this.savePref("MONEY_TYPE", this.getPref("MONEY_TYPE", 0) + 1)

                        var caixa = this.getPref("CAIXA", 0L)

                        caixa += ((valorRecebido - troco) * 100).toLong()

                        this.savePref("CAIXA", caixa)

                        type = "money"

                        infos?.let { info ->
                            PrinterUseCase(sunmiPrinterService).moneyReceiptPrint(info, valorRecebido, troco, adapter.getTotal())
                        }

                        printTickets()

                        dialog.dismiss()

                        finish()

                    } else {
                        showToast("Valor insuficiente!")
                    }

                }.show()
            }
        }

        binding.paymentBar.setOnPixClick {
            if (verifyTotal()) {
                type = "pix"
                generatePaymentIntent("122", true)
            }
        }

        binding.paymentBar.setOnCardClick {
            if (verifyTotal()) {
                val dialog = PaymentTypeChooseDialog ({ tipo ->
                    when (tipo) {
                        "debit" -> {
                            type = "debit"
                            generatePaymentIntent("2")
                        }
                        "credit" -> {
                            type = "credit"
                            generatePaymentIntent("3")
                        }
                    }
                }, false)
                dialog.show(supportFragmentManager, "CardTypeDialog")
            }
        }
    }

    private fun generatePaymentIntent(modalidade: String, isPix: Boolean = false) {
        activityResultLauncher.launch(infos?.let { SitefUseCase().payment(it, adapter.getTotal(), modalidade, isPix) })
    }

    private fun verifyTotal(): Boolean {
        if (adapter.getTotal() == 0.0) {
            showToast("Sem produtos adicionados!", true)
            return false
        } else {
            return true
        }
    }

    private fun initializeSmartPosHelper() {
        if (SmartPosHelper.getInstance() == null) {
            SmartPosHelper.init(applicationContext, AppStatus.ACTIVE)
        }
    }

    private fun initializePrinterService() {
        printerService = SmartPosHelper.getInstance().printer
        printerService.open()
    }

    private fun isPrinterServiceAvailable(): Boolean {
        return printerService != null
    }

    private fun printReceipt(viaEstab: String) {
        if (isPrinterServiceAvailable()) {
            val formattedViaEstab = viaEstab.replace(": ", ":")
                .replace(" T", "T")
                .replace(" R", "R")
                .replace(" F", "F")


            printerService.printText(formattedViaEstab,
                object : IOnPrintFinished.Stub() {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@SaleActivity,
                            "Impresso com sucesso",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailed(error: Int, msg: String) {
                        Toast.makeText(
                            this@SaleActivity,
                            "Erro na Impressora: $msg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "Impressora indisponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun printTickets() {

        handlePaymentType()

        val produtosSelecionados = adapter.getSelectedProducts()
        produtosSelecionados.forEach { product ->
            infos?.let { info ->
                repeat(product.quantity) {
                    PrinterUseCase(sunmiPrinterService).ticketPrint(info, product.name, product.price)
                }
            }
        }

        finish()
    }

    private fun handlePaymentType() {
        when (type) {
            "pix" -> {
                this.savePref("PIX_TYPE", this.getPref("PIX_TYPE", 0) + 1)
            }
            "debit" -> {
                this.savePref("DEBIT_TYPE", this.getPref("DEBIT_TYPE", 0) + 1)
            }
            "credit" -> {
                this.savePref("CREDIT_TYPE", this.getPref("CREDIT_TYPE", 0) + 1)
            }
        }
    }
}