package br.com.sttsoft.ticktzy.presentation.config

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.databinding.ActivityChargeBinding
import br.com.sttsoft.ticktzy.databinding.ActivityConfigSitefInfosBinding
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.saveToPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class ConfigSitefInfosActivity: BaseActivity() {

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
            binding.edtCNPJCPF.setText(it.Pagamento.Subadquirencia.first().cnpj)
            binding.edtDadosSubAdqui.setText(it.Pagamento.Subadquirencia.first().nomeFantasia)
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
                it.Pagamento.Subadquirencia.first().cnpj = binding.edtCNPJCPF.text.toString()
                it.Pagamento.Subadquirencia.first().nomeFantasia = binding.edtDadosSubAdqui.text.toString()
            }

            infos?.saveToPrefs(this@ConfigSitefInfosActivity, "SITEF_INFOS")

            showToast("Informações salvas...")

            finish()
        }
    }
}