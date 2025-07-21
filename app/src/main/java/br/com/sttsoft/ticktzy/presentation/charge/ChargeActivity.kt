package br.com.sttsoft.ticktzy.presentation.charge

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityChargeBinding
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.base.PaymentTypeChooseDialog
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChargeActivity: BaseActivity() {

    private val binding: ActivityChargeBinding by lazy {
        ActivityChargeBinding.inflate(layoutInflater)
    }

    private var currentValue: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

        setNumberClicks()
        setButtonsClicks()
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
                        "debit" -> { /* ação de débito */ }
                        "credit" -> { /* ação de crédito */ }
                        "money" -> {}
                    }
                }, true)
                dialog.show(supportFragmentManager, "CardTypeDialog")
            }
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

}