package br.com.sttsoft.ticktzy.presentation.base

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.databinding.ActivitySplashBinding
import br.com.sttsoft.ticktzy.domain.GetInfosUseCase
import br.com.sttsoft.ticktzy.domain.GetProductsUseCase
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.ProductSyncUseCase
import br.com.sttsoft.ticktzy.domain.TerminalnfosUseCase
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.saveToPrefs
import br.com.sttsoft.ticktzy.presentation.cashier.ActivityCashierStart
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

        binding.tvStatus.text = "Coletando informações do servidor..."

        if (!BuildConfig.useAPI) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            lifecycleScope.launch {
                try {
                    val infos = TerminalnfosUseCase().invoke()
                    val useCaseInfos = GetInfosUseCase(TerminalWrapper(infos))

                    val infosResponse = withContext(Dispatchers.IO) {
                        useCaseInfos.invoke()
                    }

                    infosResponse?.saveToPrefs(this@SplashActivity, "SITEF_INFOS")
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

}