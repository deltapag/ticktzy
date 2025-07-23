package br.com.sttsoft.ticktzy.presentation.cashier.start

import android.content.Intent
import android.os.Bundle
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.extensions.toRealFormatado
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.presentation.home.HomeActivity

class ActivityCashierStart: ActivityCashierBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startCashier()
    }

    private fun startCashier() {
        binding.tvStart.setOnClickListener {
            if (verifyBeforeStart()) {
                val dialog = ConfirmDialog ({ option ->
                    when (option) {
                        "yes" -> {
                            this.savePref("CAIXA_INICIAL", currentValue)
                            this.savePref("CAIXA", currentValue)
                            this.savePref("CAIXA_ABERTO", true)

                            PrinterUseCase(sunmiPrinterService).printInfo("ABERTURA", currentValue.toRealFormatado())

                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        "no" -> {}
                    }
                },getString(R.string.dialog_cashier_open_title), getString(R.string.dialog_cashier_open_body))
                dialog.show(supportFragmentManager, "PrintQuestionDialog")
            }
        }
    }
}