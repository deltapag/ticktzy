package br.com.sttsoft.ticktzy.presentation.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.databinding.ActivityConfigBinding
import br.com.sttsoft.ticktzy.domain.GetProductsUseCase
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.ProductSyncUseCase
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfigActivity: BaseActivity() {

    override val enablePrinterBinding = true

    private val binding: ActivityConfigBinding by lazy {
        ActivityConfigBinding.inflate(layoutInflater)
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initActivityResultLaucher()

        setListeners()

        if (!BuildConfig.DEBUG) {
            binding.btnSitefConfig.visibility = View.GONE
        }
    }

    fun initActivityResultLaucher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Trate o resultado aqui
            }
        }
    }

    fun setListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSitefTestConection.setOnClickListener {
            val infos: InfoResponse? = this.getFromPrefs("SITEF_INFOS")

            infos?.let {
                var i = SitefUseCase().testConnection(it)
                activityResultLauncher.launch(i)
            }
        }

        binding.btnSitefConfig.setOnClickListener {
            val infos: InfoResponse? = this.getFromPrefs("SITEF_INFOS")

            infos?.let {
                var i = SitefUseCase().tokenConfig(it)
                activityResultLauncher.launch(i)
            }
        }

        binding.btnSitefDirectAccess.setOnClickListener {
            val infos: InfoResponse? = this.getFromPrefs("SITEF_INFOS")

            infos?.let {
                var i = SitefUseCase().directAccess(it, this.getPref("TLS_ENABLED", false))
                activityResultLauncher.launch(i)
            }
        }

        binding.btnTestPrinter.setOnClickListener {
            PrinterUseCase(sunmiPrinterService).testPrinter()
        }

        binding.btnProductRefresh.setOnClickListener {
            val infos: InfoResponse? = this.getFromPrefs("SITEF_INFOS")

            showLoading()

            lifecycleScope.launch {
                try {
                    val cnpj = infos
                        ?.Pagamento
                        ?.Subadquirencia
                        ?.firstOrNull()
                        ?.cnpj

                    cnpj?.let {
                        val produtos = withContext(Dispatchers.IO) {
                            ProductSyncUseCase(
                                ProductCacheUseCase(this@ConfigActivity),
                                GetProductsUseCase()
                            ).sync(it)
                        }

                        hideLoading()

                        produtos.also {
                            showToast("Atualizado com ${it.size} produtos", true)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CONFIG", "Sync Products: ", e)
                    showToast("Erro ao buscar produtos!", true)
                    hideLoading()
                }
            }
        }

        binding.btnSitefConfigInfos.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, ConfigSitefInfosActivity::class.java))
        }

        binding.btnCashierTest.setOnClickListener {

        }
    }

}