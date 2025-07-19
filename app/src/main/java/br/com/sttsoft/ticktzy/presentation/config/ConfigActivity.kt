package br.com.sttsoft.ticktzy.presentation.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.databinding.ActivityConfigBinding
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class ConfigActivity: BaseActivity() {

    private val binding: ActivityConfigBinding by lazy {
        ActivityConfigBinding.inflate(layoutInflater)
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

        initActivityResultLaucher()

        setListeners()
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
    }

}