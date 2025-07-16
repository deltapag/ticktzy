package br.com.sttsoft.ticktzy.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.R

import br.com.sttsoft.ticktzy.databinding.ActivityHomeBinding
import br.com.sttsoft.ticktzy.presentation.charge.ChargeActivity
import br.com.sttsoft.ticktzy.presentation.config.ConfigActivity
import br.com.sttsoft.ticktzy.presentation.sale.ui.SaleActivity

class HomeActivity: AppCompatActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
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
    }
}