package br.com.sttsoft.ticktzy.presentation.sale.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivitySaleBinding
import br.com.sttsoft.ticktzy.presentation.sale.components.ProductAdapter
import br.com.sttsoft.ticktzy.repository.local.product
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService

class SaleActivity: AppCompatActivity() {

    private val binding: ActivitySaleBinding by lazy {
        ActivitySaleBinding.inflate(layoutInflater)
    }

    private var sunmiPrinterService: SunmiPrinterService? = null

    private lateinit var adapter: ProductAdapter

    private val productList: List<product> = listOf(
        product(name = "Frango", photo = "asd", price = 1.0, quantity = 0, isSelected = false),
        product(name = "Lata", photo = "", price = 1.0, quantity = 0, isSelected = false),
        product(name = "Batata", photo = "", price = 1.0, quantity = 0, isSelected = false),
        product(name = "Teste", photo = "", price = 1.0, quantity = 0, isSelected = false),
        product(name = "Bebida", photo = "", price = 1.0, quantity = 0, isSelected = false),
        product(name = "Comida", photo = "", price = 1.0, quantity = 0, isSelected = false)
    )

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

        initPrinter()

        initActivityResultLaucher()

        setAdapter()
        setSearchBarListener()
        setOnClickListeners()
    }

    fun initPrinter() {
        InnerPrinterManager.getInstance().bindService(this, object : InnerPrinterCallback() {
            override fun onConnected(service: SunmiPrinterService) {
                sunmiPrinterService = service
            }

            override fun onDisconnected() {
                sunmiPrinterService = null
            }
        })
    }

    fun initActivityResultLaucher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Trate o resultado aqui
            }
        }
    }

    fun setAdapter() {
        binding.rclProducts.layoutManager = GridLayoutManager(this, 3)

        adapter = ProductAdapter(productList) { total ->
            binding.paymentBar.setTotalText(getString(R.string.text_sale_price).format(total))
        }

        binding.rclProducts.adapter = adapter
    }

    fun setSearchBarListener() {
        binding.searchBar.addOnTextChangedListener { query -> adapter.filter(query) }
    }

    fun setOnClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.paymentBar.setOnCashClick {
            try {
                activityResultLauncher.launch(advanceToPayment(adapter.getTotal(), 0))
            } catch (e: Exception) {
                Log.e("SALEC", "setOnClickListeners: ", )
            }
        }

        binding.paymentBar.setOnPixClick {
            sunmiPrinterService?.apply {
                val boldOff = byteArrayOf(0x1B, 0x45, 0x00) // ESC E 0 → negrito OFF

                sendRAWData(boldOff, null)

                // Início da impressão
                lineWrap(1, null) // pula linha

                // Informações (centralizado, normal)
                setAlignment(1, null) // 0=left, 1=center, 2=right
                setFontSize(24f, null)
                printText("Informações\n", null)
                printText("NOME / OUTRAS INFOS\n\n", null)

                // Texto principal (negrito e maior)
                val boldOn = byteArrayOf(0x1B, 0x45, 0x01) // ESC E 1 → negrito ON

                sendRAWData(boldOn, null)
                setFontSize(36f, null)
                printText("TICKET 5\n", null)

                // Avança papel
                lineWrap(5, null)
                sendRAWData(boldOff, null)
            }
        }
    }

    fun advanceToPayment(total: Double, modality: Int) : Intent{
        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")


        return i
    }
}