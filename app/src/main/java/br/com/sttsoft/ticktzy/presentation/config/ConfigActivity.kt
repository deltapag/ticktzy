package br.com.sttsoft.ticktzy.presentation.config

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
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

        binding.btnLayoutNewSale.setOnClickListener {
            Toast.makeText(this, "Configurar Nova Venda", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutCharge.setOnClickListener {
            Toast.makeText(this, "Configurar Cobrança", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutReports.setOnClickListener {
            Toast.makeText(this, "Configurar Relatórios", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutSearchBar.setOnClickListener {
            Toast.makeText(this, "Configurar SearchBar", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutPaymentBar.setOnClickListener {
            Toast.makeText(this, "Configurar PaymentBar", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutProductList.setOnClickListener {
            Toast.makeText(this, "Configurar Lista de Produtos", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutProductAdapter.setOnClickListener {
            Toast.makeText(this, "Configurar ProductAdapter", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutProductView.setOnClickListener {
            Toast.makeText(this, "Configurar ProductView", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutTerminal.setOnClickListener {
            Toast.makeText(this, "Configurar Terminal", Toast.LENGTH_SHORT).show()
        }

        binding.btnLayoutSitef.setOnClickListener {
            Toast.makeText(this, "Configurar SITEF", Toast.LENGTH_SHORT).show()
        }
    }

}
