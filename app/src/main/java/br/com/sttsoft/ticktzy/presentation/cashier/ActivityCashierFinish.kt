package br.com.sttsoft.ticktzy.presentation.cashier

import android.content.Intent
import android.os.Bundle
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityCashierBinding
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.presentation.home.HomeActivity

class ActivityCashierFinish: ActivityCashierBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initTexts()
        finishCashier()
    }

    private fun initTexts() {
        binding.tvTitle.text = getString(R.string.text_cashier_title_finish)
        binding.tvStart.text = getString(R.string.text_cashier_finish)
    }

    private fun finishCashier() {
        binding.tvStart.setOnClickListener {
            if (verifyBeforeStart()) {

                val dialog = ConfirmDialog ({ option ->
                    when (option) {
                        "yes" -> {
                            this.savePref("CAIXA_ABERTO", false)
                            val intent = Intent(this, ActivityCashierStart::class.java)
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