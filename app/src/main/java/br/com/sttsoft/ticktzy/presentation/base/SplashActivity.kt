package br.com.sttsoft.ticktzy.presentation.base

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivitySplashBinding
import br.com.sttsoft.ticktzy.domain.GetInfosUseCase
import br.com.sttsoft.ticktzy.domain.GetProductsUseCase
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.ProductSyncUseCase
import br.com.sttsoft.ticktzy.domain.TerminalnfosUseCase
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.extensions.saveToPrefs
import br.com.sttsoft.ticktzy.presentation.cashier.start.ActivityCashierStart
import br.com.sttsoft.ticktzy.presentation.home.HomeActivity
import br.com.sttsoft.ticktzy.repository.remote.request.TerminalWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SplashActivity: BaseActivity() {

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        defineHomeApp()

        val infos = TerminalnfosUseCase().invoke()

        binding.tvSerial.text = infos.NumeroSerie

        binding.tvStatus.text = "Coletando informações do servidor..."

        if (!BuildConfig.useAPI) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            lifecycleScope.launch {
                try {
                    val useCaseInfos = GetInfosUseCase(TerminalWrapper(infos))

                    val infosResponse = withContext(Dispatchers.IO) {
                        useCaseInfos.invoke()
                    }

                    infosResponse?.saveToPrefs(this@SplashActivity, "SITEF_INFOS")

                    if (!BuildConfig.DEBUG) {
                        this@SplashActivity.savePref("TLS_ENABLED", true)
                    }

                    binding.tvStatus.text = "Coletando produtos..."

                    val cnpj = infosResponse
                        ?.Pagamento
                        ?.Subadquirencia
                        ?.firstOrNull()
                        ?.cnpj

                    // Agora chama a coleta de produtos
                    if (cnpj != null) {
                        val produtos = withContext(Dispatchers.IO) {
                            ProductSyncUseCase(
                                ProductCacheUseCase(this@SplashActivity),
                                GetProductsUseCase()
                            ).sync(cnpj)
                        }
                    }

                    binding.tvStatus.text = "Sucesso! Iniciando..."

                    if (this@SplashActivity.getPref("CAIXA_ABERTO", false)) {
                        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, ActivityCashierStart::class.java))
                        finish()
                    }



                } catch (e: Exception) {
                    binding.tvStatus.text = "Erro ao obter dados: ${e.message}"
                }
            }
        }
    }

    private fun defineHomeApp() {
        val packageName = BuildConfig.APPLICATION_ID
        val appLabel = resources.getString(R.string.app_name)

        val intent = Intent()
        intent.setComponent(getHomeAppComponentName())
        intent.setAction("br.com.bin.service.DefineHomeAppService.action.DEFINE_HOME_APP")
        intent.putExtra("br.com.bin.service.DefineHomeAppService.extra.PACKAGE_NAME", packageName)
        intent.putExtra("br.com.bin.service.DefineHomeAppService.extra.APP_LABEL", appLabel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    /**
     * Obtém o ComponentName de acordo com o modelo de terminal
     */
    private fun getHomeAppComponentName(): ComponentName {
        val pkg = when (Build.MODEL) {
            "GPOS780" -> getString(R.string.gertec_gpos780)
            "GPOS760" -> getString(R.string.gertec_gpos760)
            "GPOS720" -> getString(R.string.gertec_gpos720)
            "P2-B" -> getString(R.string.sunmi_P2)
            "P2-A11" -> getString(R.string.sunmi_P2A11)
            "L400" -> getString(R.string.positivo_L400)
            "T19" -> getString(R.string.tectoy_T19)
            "T8" -> getString(R.string.tectoy_T8)
            "T4" -> getString(R.string.tectoy_T4)
            "DX4000" -> getString(R.string.ingenico_dx4000)
            "EX6000" -> getString(R.string.ingenico_ex6000)
            "EX4000" -> getString(R.string.ingenico_ex4000)
            else -> getString(R.string.ingenico_dx8000)
        }
        return ComponentName(pkg, "br.com.bin.service.DefineHomeAppService")
    }
}