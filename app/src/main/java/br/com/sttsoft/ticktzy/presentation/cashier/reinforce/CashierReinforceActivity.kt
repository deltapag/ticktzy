package br.com.sttsoft.ticktzy.presentation.cashier.reinforce

import android.os.Bundle
import android.view.View
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.extensions.toRealFormatado
import br.com.sttsoft.ticktzy.presentation.cashier.start.ActivityCashierBase
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog

class CashierReinforceActivity: ActivityCashierBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initComponents()
    }

    fun initComponents() {
        binding.ivClose.visibility = View.VISIBLE

        binding.tvTitle.text = getString(R.string.button_text_cashier_reinforce)
        binding.tvStart.text = getString(R.string.confirm)

        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.clTitle.setOnClickListener {
            finish()
        }

        binding.tvStart.setOnClickListener {
            doReinforce()
        }

        binding.llStart.setOnClickListener {
            doReinforce()
        }
    }

    fun doReinforce() {
        if (verifyBeforeStart()) {
            val dialog = ConfirmDialog ({ option ->
                when (option) {
                    "yes" -> {
                        this.savePref("REINFORCE_MADE", this.getPref("REINFORCE_MADE", 0) + 1)

                        this.savePref("CAIXA_REINFORCE", this.getPref("CAIXA_REINFORCE", 0L) + currentValue)

                        var valor = this.getPref("CAIXA", 0L)
                        valor += currentValue

                        this.savePref("CAIXA",  valor)

                        PrinterUseCase(sunmiPrinterService).printInfo("REFORÇO", currentValue.toRealFormatado())

                        finish()
                    }
                    "no" -> {}
                }
            },getString(R.string.confirm), getString(R.string.text_cashier_reinforce_confirmation))
            dialog.show(supportFragmentManager, "ConfirmDialog")
        }
    }
}