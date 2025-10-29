package br.com.sttsoft.ticktzy.presentation.config

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityConfigSitefInfosBinding
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.extensions.saveToPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class ConfigSitefInfosActivity : BaseActivity() {
    private val binding: ActivityConfigSitefInfosBinding by lazy {
        ActivityConfigSitefInfosBinding.inflate(layoutInflater)
    }

    private var infos: InfoResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        infos = this.getFromPrefs("SITEF_INFOS")

        initFields()
        setListeners()
    }

    private fun initFields() {
        infos?.let {
            binding.edtIp.setText(it.Pagamento.sitefPublico.ip)
            binding.edtPorta.setText(it.Pagamento.sitefPublico.porta)
            binding.edtCnpjAutomacao.setText(it.Pagamento.lojasSitef.first().cnpj)
            binding.edtEmpresa.setText(it.Pagamento.lojasSitef.first().codigoLojaSitef)
            binding.edtCNPJCPF.setText(it.Pagamento.Subadquirencia.first().cnpj)
            binding.edtDadosSubAdqui.setText(it.Pagamento.Subadquirencia.first().nomeFantasia)
        }

        if (this.getPref("TLS_ENABLED", false)) {
            binding.btnEnableTLS.text = getString(R.string.button_text_config_enable_tls)

            ViewCompat.setBackgroundTintList(
                binding.btnEnableTLS,
                ContextCompat.getColorStateList(this, R.color.plus_green),
            )
        }
    }

    private fun setListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            infos?.let {
                it.Pagamento.sitefPublico.ip = binding.edtIp.text.toString()
                it.Pagamento.sitefPublico.porta = binding.edtPorta.text.toString()
                it.Pagamento.lojasSitef.first().cnpj = binding.edtCnpjAutomacao.text.toString()
                it.Pagamento.lojasSitef.first().codigoLojaSitef = binding.edtEmpresa.text.toString()
                it.Pagamento.Subadquirencia.first().cnpj = binding.edtCNPJCPF.text.toString()
                it.Pagamento.Subadquirencia.first().nomeFantasia = binding.edtDadosSubAdqui.text.toString()
            }

            infos?.saveToPrefs(this@ConfigSitefInfosActivity, "SITEF_INFOS")

            showToast("Informações salvas...")

            finish()
        }

        binding.btnEnableTLS.setOnClickListener {
            if (!this.getPref("TLS_ENABLED", false)) {
                binding.btnEnableTLS.text = getString(R.string.button_text_config_enable_tls)

                ViewCompat.setBackgroundTintList(
                    binding.btnEnableTLS,
                    ContextCompat.getColorStateList(this, R.color.plus_green),
                )
                this.savePref("TLS_ENABLED", true)
            } else {
                binding.btnEnableTLS.text = getString(R.string.button_text_config_disable_tls)

                ViewCompat.setBackgroundTintList(
                    binding.btnEnableTLS,
                    ContextCompat.getColorStateList(this, R.color.minus_red),
                )
                this.savePref("TLS_ENABLED", false)
            }
        }
    }
}
