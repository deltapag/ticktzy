package br.com.sttsoft.ticktzy.presentation.sitef

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.com.execucao.posmp_api.SmartPosHelper
import br.com.execucao.posmp_api.printer.PrinterService
import br.com.execucao.posmp_api.store.AppStatus
import br.com.execucao.smartPOSService.printer.IOnPrintFinished
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivitySitefHomeBinding
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class ActivitySitefHome: BaseActivity() {

    private val binding: ActivitySitefHomeBinding by lazy {
        ActivitySitefHomeBinding.inflate(layoutInflater)
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var printerService: PrinterService

    private var infos: InfoResponse? = null

    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initActivityResultLaucher()

        infos = this.getFromPrefs("SITEF_INFOS")

        initializeSmartPosHelper()
        initializePrinterService()

        initFuncions()
    }

    fun initFuncions() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnReprint.setOnClickListener {
            type = "reprint"
            infos?.let { SitefUseCase().reprint(it, this.getPref("TLS_ENABLED", false)) }
                ?.let { it1 -> activityResultLauncher.launch(it1) }
        }

        binding.btnCancel.setOnClickListener {
            type = "cancel"

            infos?.let { SitefUseCase().cancelation(it, this.getPref("TLS_ENABLED", false)) }
                ?.let { it1 -> activityResultLauncher.launch(it1) }
        }

        binding.btnSendLogs.setOnClickListener {
            type = "logs"
            infos?.let { SitefUseCase().trace(it, this.getPref("TLS_ENABLED", false)) }
                ?.let { it1 -> activityResultLauncher.launch(it1) }
        }

        if (!BuildConfig.DEBUG) {
            binding.btnSitefDirectAccess.visibility = View.GONE
        }

        binding.btnSitefDirectAccess.setOnClickListener {
            type = "sitef"
            val infos: InfoResponse? = this.getFromPrefs("SITEF_INFOS")

            infos?.let {
                var i = SitefUseCase().directAccess(it, this.getPref("TLS_ENABLED", false))
                activityResultLauncher.launch(i)
            }
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

    private fun initActivityResultLaucher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val bundle = data?.extras
            if (bundle != null) {
                if (result.resultCode == RESULT_OK) {
                    handleType()

                    val comprovanteEstab = bundle.getString("VIA_ESTABELECIMENTO")
                    val comprovanteCli = bundle.getString("VIA_CLIENTE")
                    if (comprovanteEstab != null && comprovanteEstab.trim { it <= ' ' }.isNotEmpty()) {
                        printReceipt(comprovanteEstab)
                    } else if (comprovanteCli != null && comprovanteCli.trim { it <= ' ' }.isNotEmpty()) {
                        printReceipt(comprovanteCli)
                    }

                    if (!type.equals("reprint")) {
                        val dialog = ConfirmDialog ({ option ->
                            when (option) {
                                "yes" -> {
                                    if (comprovanteCli != null && comprovanteCli.trim { it <= ' ' }.isNotEmpty()) {
                                        printReceipt(comprovanteCli)

                                        finish()
                                    }
                                }
                                "no" -> {
                                    finish()
                                }
                            }
                        },getString(R.string.dialog_print_question_title), getString(R.string.dialog_print_question_body))
                        dialog.show(supportFragmentManager, "PrintQuestionDialog")
                    } else {
                        finish()
                    }
                }
            } else {
                finish()
            }
        }
    }

    private fun isPrinterServiceAvailable(): Boolean {
        return printerService != null
    }

    private fun printReceipt(viaEstab: String) {
        if (isPrinterServiceAvailable()) {
            val formattedViaEstab = viaEstab.replace(": ", ":")
                .replace(" T", "T")
                .replace(" R", "R")
                .replace(" F", "F")


            printerService.printText(formattedViaEstab,
                object : IOnPrintFinished.Stub() {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@ActivitySitefHome,
                            "Impresso com sucesso",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailed(error: Int, msg: String) {
                        Toast.makeText(
                            this@ActivitySitefHome,
                            "Erro na Impressora: $msg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "Impressora indisponível", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleType() {
        when (type) {
            "cancel" -> {
                this.savePref("CANCELS_MADE", this.getPref("CANCELS_MADE", 0) + 1)
            }
        }
    }
}