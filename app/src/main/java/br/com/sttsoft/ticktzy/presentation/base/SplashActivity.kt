package br.com.sttsoft.ticktzy.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.databinding.ActivitySplashBinding
import br.com.sttsoft.ticktzy.domain.GetInfosUseCase
import br.com.sttsoft.ticktzy.domain.TerminalnfosUseCase
import br.com.sttsoft.ticktzy.extensions.saveToPrefs
import br.com.sttsoft.ticktzy.presentation.home.HomeActivity
import br.com.sttsoft.ticktzy.repository.remote.request.TerminalWrapper

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
            val terminal = TerminalnfosUseCase().invoke()
            val useCase = GetInfosUseCase(TerminalWrapper(terminal))

            Thread {
                useCase.invoke(
                    onSuccess = {
                        runOnUiThread {
                            it.saveToPrefs(this, "SITEF_INFOS")
                            binding.tvStatus.text = "Sucesso! Iniciando..."
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    },
                    onError = { error ->
                        runOnUiThread {
                            binding.tvStatus.text = "Erro ao obter dados: ${error.message}"
                        }
                    }
                )
            }.start()
        }
    }

}