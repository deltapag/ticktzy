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
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.presentation.dialogs.PaymentTypeChooseDialog
import br.com.sttsoft.ticktzy.presentation.sale.components.ProductAdapter
import br.com.sttsoft.ticktzy.repository.local.product
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class SaleActivity : BaseActivity() {
    private val binding: ActivitySaleBinding by lazy {
        ActivitySaleBinding.inflate(layoutInflater)
    }

    override val enablePrinterBinding = true

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

        initActivityResultLaucher()

        setSearchBarListener()
        setOnClickListeners()

        getProducts()
    }

    private fun initActivityResultLaucher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                val bundle = data?.extras
                if (bundle != null) {
                    if (result.resultCode == RESULT_OK) {
                        this.savePref("SALES_MADE", this.getPref("SALES_MADE", 0) + 1)
                        val comprovanteEstab = bundle.getString("VIA_ESTABELECIMENTO")
                        if (comprovanteEstab != null && comprovanteEstab.trim { it <= ' ' }.isNotEmpty()) {
                            printReceipt(comprovanteEstab)
                        }

                        val dialog =
                            ConfirmDialog(
                                { option ->
                                    when (option) {
                                        "yes" -> {
                                            val comprovanteCliente = bundle.getString("VIA_CLIENTE")
                                            if (comprovanteCliente != null &&
                                                comprovanteCliente.trim { it <= ' ' }.isNotEmpty()
                                            ) {
                                                printReceipt(comprovanteCliente)
                                            }
                                            printTickets()
                                        }
                                        "no" -> {
                                            printTickets()
                                        }
                                    }
                                },
                                getString(R.string.dialog_print_question_title),
                                getString(R.string.dialog_print_question_body),
                            )
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
        } else {
            ConfirmDialog(
                { option ->
                    when (option) {
                        "ok" -> {
                            finish()
                        }
                    }
                },
                getString(
                    R.string.dialog_warning_title,
                ),
                getString(R.string.text_dialog_message_no_products),
            ).show(supportFragmentManager, "ConfirmDialog")
        }
    }

    private fun setAdapter() {
        binding.rclProducts.layoutManager = GridLayoutManager(this, 2)

        adapter =
            ProductAdapter(productList) { total ->
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

        binding.paymentBar.setOnPixClick {
            if (verifyTotal()) {
                type = "pix"
                generatePaymentIntent("122", true)
            }
        }

        binding.paymentBar.setOnCardClick {
            if (verifyTotal()) {
                val dialog =
                    PaymentTypeChooseDialog({ tipo ->
                        when (tipo) {
                            "sitef" -> {
                                type = "sitef"
                                generatePaymentIntent("0")
                            }
                            "debit" -> {
                                type = "debit"
                                generatePaymentIntent("2")
                            }
                            "credit" -> {
                                type = "credit"
                                generatePaymentIntent("3")
                            }
                            "money" -> {
                                if (verifyTotal()) {
                                    ChangeDialog(this, adapter.getTotal(), { valorRecebido, troco, dialog ->

                                        if (valorRecebido > 0.0 && valorRecebido >= adapter.getTotal()) {
                                            this.savePref("SALES_MADE", this.getPref("SALES_MADE", 0) + 1)
                                            this.savePref("MONEY_TYPE", this.getPref("MONEY_TYPE", 0) + 1)

                                            var valor = (valorRecebido - troco)

                                            this.savePref("MONEY_VALUE", this.getPref("MONEY_VALUE", 0.0) + valor)

                                            var caixa = this.getPref("CAIXA", 0L)

                                            caixa += ((valorRecebido - troco) * 100).toLong()

                                            this.savePref("CAIXA", caixa)

                                            type = "money"

                                            infos?.let { info ->
                                                PrinterUseCase(
                                                    sunmiPrinterService,
                                                ).moneyReceiptPrint(info, valorRecebido, troco, adapter.getTotal())
                                            }

                                            printTickets()

                                            dialog.dismiss()

                                            finish()
                                        } else {
                                            showToast("Valor insuficiente!")
                                        }
                                    }, { dialog -> dialog.dismiss() }).show()
                                }
                            }
                        }
                    }, true)
                dialog.show(supportFragmentManager, "CardTypeDialog")
            }
        }
    }

    private fun generatePaymentIntent(
        modalidade: String,
        isPix: Boolean = false,
    ) {
        infos?.let {
            SitefUseCase(
                this,
            ).payment(it, adapter.getTotal(), modalidade, isPix, this.getPref("TLS_ENABLED", false))
        }
            ?.let { activityResultLauncher.launch(it) }
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
            val formattedViaEstab =
                viaEstab.replace(": ", ":")
                    .replace(" T", "T")
                    .replace(" R", "R")
                    .replace(" F", "F")

            printerService.printText(
                formattedViaEstab,
                object : IOnPrintFinished.Stub() {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@SaleActivity,
                            "Impresso com sucesso",
                            Toast.LENGTH_LONG,
                        ).show()
                    }

                    override fun onFailed(
                        error: Int,
                        msg: String,
                    ) {
                        Toast.makeText(
                            this@SaleActivity,
                            "Erro na Impressora: $msg",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                },
            )
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
                this.savePref("PIX_VALUE", this.getPref("PIX_VALUE", 0.0) + adapter.getTotal())
            }
            "debit" -> {
                this.savePref("DEBIT_TYPE", this.getPref("DEBIT_TYPE", 0) + 1)
                this.savePref("DEBIT_VALUE", this.getPref("DEBIT_VALUE", 0.0) + adapter.getTotal())
            }
            "credit" -> {
                this.savePref("CREDIT_TYPE", this.getPref("CREDIT_TYPE", 0) + 1)
                this.savePref("CREDIT_VALUE", this.getPref("CREDIT_VALUE", 0.0) + adapter.getTotal())
            }
        }
    }
}
