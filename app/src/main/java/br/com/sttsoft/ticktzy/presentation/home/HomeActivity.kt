package br.com.sttsoft.ticktzy.presentation.home

import android.content.Intent
import android.os.Bundle
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityHomeBinding
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.cashier.home.ActivityCashierHome
import br.com.sttsoft.ticktzy.presentation.charge.ChargeActivity
import br.com.sttsoft.ticktzy.presentation.config.ConfigActivity
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog
import br.com.sttsoft.ticktzy.presentation.dialogs.InputDialog
import br.com.sttsoft.ticktzy.presentation.sale.ui.SaleActivity
import br.com.sttsoft.ticktzy.presentation.sitef.ActivitySitefHome
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class HomeActivity : BaseActivity() {
    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private var infos: InfoResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        infos = this.getFromPrefs("SITEF_INFOS")

        setVersionCode()
        setListeners()
    }

    fun setVersionCode() {
        binding.tvVersion.text = String.format(getString(R.string.text_home_version), BuildConfig.VERSION_NAME)
    }

    fun setListeners() {
        binding.btnNewSale.setOnClickListener {
            startActivity(Intent(this, SaleActivity::class.java))
        }

        binding.btnCharge.setOnClickListener {
            startActivity(Intent(this, ChargeActivity::class.java))
        }

        binding.btnReports.setOnClickListener {
        }

        binding.btnConfig.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }

        binding.btnCashierFunctions.setOnClickListener {
            InputDialog { password, dialog ->
                infos?.let {
                    if (it.App.senhaDoApp.equals(password)) {
                        dialog.dismiss()
                        startActivity(Intent(this, ActivityCashierHome::class.java))
                    } else {
                        ConfirmDialog(
                            { option ->
                                when (option) {
                                    "ok" -> {
                                        dialog.dismiss()
                                    }
                                }
                            },
                            getString(
                                R.string.dialog_warning_title,
                            ),
                            getString(
                                R.string.text_dialog_message_password_incorrect,
                            ),
                            true,
                        ).show(supportFragmentManager, "ConfirmDialog")
                    }
                }
            }.show(supportFragmentManager, "InputDialog")
        }

        binding.btnAdministrativesFunctions.setOnClickListener {
            InputDialog { password, dialog ->
                infos?.let {
                    if (it.App.senhaDoApp.equals(password)) {
                        dialog.dismiss()
                        startActivity(Intent(this, ActivitySitefHome::class.java))
                    } else {
                        ConfirmDialog(
                            { option ->
                                when (option) {
                                    "ok" -> {
                                        dialog.dismiss()
                                    }
                                }
                            },
                            getString(
                                R.string.dialog_warning_title,
                            ),
                            getString(
                                R.string.text_dialog_message_password_incorrect,
                            ),
                            true,
                        ).show(supportFragmentManager, "ConfirmDialog")
                    }
                }
            }.show(supportFragmentManager, "InputDialog")
        }
    }
}
