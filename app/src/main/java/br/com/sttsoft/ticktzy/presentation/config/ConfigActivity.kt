package br.com.sttsoft.ticktzy.presentation.config

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import br.com.sttsoft.ticktzy.databinding.ActivityConfigBinding

class ConfigActivity: AppCompatActivity() {

    private val binding: ActivityConfigBinding by lazy {
        ActivityConfigBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {override fun handleOnBackPressed() {} })

        setListeners()
    }

    fun setListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

}