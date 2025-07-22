package br.com.sttsoft.ticktzy.presentation.cashier.home

import android.content.Intent
import android.os.Bundle
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityCashierHomeBinding
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.cashier.ActivityCashierFinish
import br.com.sttsoft.ticktzy.presentation.cashier.ActivityCashierStart
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog

class ActivityCashierHome: BaseActivity() {

    private val binding: ActivityCashierHomeBinding by lazy {
        ActivityCashierHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setButtonsClicks()
    }

    private fun setButtonsClicks() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFinishCashier.setOnClickListener {
            val dialog = ConfirmDialog ({ option ->
                when (option) {
                    "yes" -> {
                        startActivity(Intent(this, ActivityCashierFinish::class.java))
                    }
                    "no" -> {}
                }
            },getString(R.string.confirmation), getString(R.string.text_cashier_finish_confirmation))
            dialog.show(supportFragmentManager, "PrintQuestionDialog")
        }

        binding.btnCashierSangria.setOnClickListener {

        }
    }
}