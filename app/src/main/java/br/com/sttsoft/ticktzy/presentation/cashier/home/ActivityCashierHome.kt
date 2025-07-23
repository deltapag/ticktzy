package br.com.sttsoft.ticktzy.presentation.cashier.home

import android.content.Intent
import android.os.Bundle
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityCashierHomeBinding
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.cashier.finish.ActivityCashierFinish
import br.com.sttsoft.ticktzy.presentation.cashier.reinforce.CashierReinforceActivity
import br.com.sttsoft.ticktzy.presentation.cashier.sangria.CashierSangriaActivity
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
            startActivity(Intent(this, ActivityCashierFinish::class.java))
        }

        binding.btnCashierSangria.setOnClickListener {
            startActivity(Intent(this, CashierSangriaActivity::class.java))
        }

        binding.btnCashierReinforce.setOnClickListener {
            startActivity(Intent(this, CashierReinforceActivity::class.java))
        }
    }
}