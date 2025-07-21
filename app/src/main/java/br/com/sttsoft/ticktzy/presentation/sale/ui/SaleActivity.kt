package br.com.sttsoft.ticktzy.presentation.sale.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivitySaleBinding
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.base.PaymentTypeChooseDialog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        infos = this.getFromPrefs("SITEF_INFOS")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

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
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Trate o resultado aqui
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

            }
        }

        binding.paymentBar.setOnPixClick {
            if (verifyTotal()) {
                generatePaymentIntent("", true)
            }
        }

        binding.paymentBar.setOnCardClick {
            if (verifyTotal()) {
                val dialog = PaymentTypeChooseDialog ({ tipo ->
                    when (tipo) {
                        "debit" -> { generatePaymentIntent("") }
                        "credit" -> { generatePaymentIntent("") }
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
}