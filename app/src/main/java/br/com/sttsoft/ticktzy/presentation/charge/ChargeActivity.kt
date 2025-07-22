package br.com.sttsoft.ticktzy.presentation.charge

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.com.execucao.posmp_api.SmartPosHelper
import br.com.execucao.posmp_api.printer.PrinterService
import br.com.execucao.posmp_api.store.AppStatus
import br.com.execucao.smartPOSService.printer.IOnPrintFinished
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityChargeBinding
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.dialogs.PaymentTypeChooseDialog
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import java.text.DecimalFormat


class ChargeActivity: BaseActivity() {

    private val binding: ActivityChargeBinding by lazy {
        ActivityChargeBinding.inflate(layoutInflater)
    }

    private var currentValue: Long = 0L

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var infos: InfoResponse? = null

    private lateinit var printerService: PrinterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeSmartPosHelper()
        initializePrinterService()

        infos = this.getFromPrefs("SITEF_INFOS")

        initActivityResultLaucher()

        setNumberClicks()
        setButtonsClicks()
    }

    private fun initActivityResultLaucher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val bundle = data?.extras
            if (bundle != null) {
                if (result.resultCode == RESULT_OK) {
                    val comprovanteEstab = bundle.getString("VIA_ESTABELECIMENTO")
                    if (comprovanteEstab != null && comprovanteEstab.trim { it <= ' ' }.isNotEmpty()) {
                        printReceipt(comprovanteEstab)
                    }

                    val dialog = ConfirmDialog ({ option ->
                        when (option) {
                            "yes" -> {
                                val comprovanteCliente = bundle.getString("VIA_CLIENTE")
                                if (comprovanteCliente != null && comprovanteCliente.trim { it <= ' ' }.isNotEmpty()) {
                                    printReceipt(comprovanteCliente)
                                }
                            }
                            "no" -> {}
                        }
                    },getString(R.string.dialog_print_question_title), getString(R.string.dialog_print_question_body))
                    dialog.show(supportFragmentManager, "PrintQuestionDialog")
                }
            } else {
                Toast.makeText(this, "Nenhum dado retornado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setNumberClicks() {
        binding.tvN1.setOnClickListener { handleNumberClick("1") }
        binding.tvN2.setOnClickListener { handleNumberClick("2") }
        binding.tvN3.setOnClickListener { handleNumberClick("3") }
        binding.tvN4.setOnClickListener { handleNumberClick("4") }
        binding.tvN5.setOnClickListener { handleNumberClick("5") }
        binding.tvN6.setOnClickListener { handleNumberClick("6") }
        binding.tvN7.setOnClickListener { handleNumberClick("7") }
        binding.tvN8.setOnClickListener { handleNumberClick("8") }
        binding.tvN9.setOnClickListener { handleNumberClick("9") }
        binding.tvN0.setOnClickListener { handleNumberClick("0") }
        binding.tvN00.setOnClickListener { handleNumberClick("00") }
    }

    fun handleNumberClick(number: String) {
        if (number == "00") {
            currentValue = currentValue * 100 // Adiciona "00" no final
        } else {
            currentValue = currentValue * 10 + number.toInt() // Adiciona o número no final
        }

        updateScreen()
    }

    fun setButtonsClicks() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivDelete.setOnClickListener {
            currentValue = currentValue / 10;
            updateScreen()
        }

        binding.ivDelete.setOnLongClickListener {
            currentValue = 0L
            updateScreen()
            false
        }

        binding.llPay.setOnClickListener {
            if (verifyBeforePay()) {
                val dialog = PaymentTypeChooseDialog ({ tipo ->
                    when (tipo) {
                        "debit" -> { generatePaymentIntent("2", true) }
                        "credit" -> { generatePaymentIntent("3", true) }
                        "money" -> { finish() }
                    }
                }, true)
                dialog.show(supportFragmentManager, "CardTypeDialog")
            }
        }

        binding.ivPix.setOnClickListener {
            generatePaymentIntent("122", true)
        }
    }

    fun verifyBeforePay(): Boolean {

        if (currentValue == 0L) {
            binding.tvWarning.text = getString(R.string.error_value_zero)
            binding.tvWarning.visibility = View.VISIBLE
            binding.tvValor.setTextColor(Color.RED)
            return false
        }

        return true
    }

    fun updateScreen() {
        binding.tvValor.setTextColor(Color.BLACK)
        binding.tvWarning.visibility = View.GONE
        val decimalFormat = DecimalFormat("R$ #,##0.00")
        val formattedValue = decimalFormat.format(currentValue / 100.0)
        binding.tvValor.text = formattedValue
    }

    private fun generatePaymentIntent(modalidade: String, isPix: Boolean = false) {
        activityResultLauncher.launch(infos?.let { SitefUseCase().payment(it, currentValue.toDouble(), modalidade, isPix) })
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
                            this@ChargeActivity,
                            "Impresso com sucesso",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onFailed(error: Int, msg: String) {
                        Toast.makeText(
                            this@ChargeActivity,
                            "Erro na Impressora: $msg",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "Impressora indisponível", Toast.LENGTH_SHORT).show()
        }
    }

}