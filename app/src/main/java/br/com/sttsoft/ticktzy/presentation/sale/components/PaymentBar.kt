package br.com.sttsoft.ticktzy.presentation.sale.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import br.com.sttsoft.ticktzy.R

class PaymentBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvTotal: TextView
    private val btnCash: Button
    private val btnCard: Button
    private val btnPix: Button

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.component_payment_bar, this, true)

        tvTotal = findViewById(R.id.tv_total)
        btnCash = findViewById(R.id.btn_cash)
        btnCard = findViewById(R.id.btn_card)
        btnPix = findViewById(R.id.btn_pix)
    }

    fun setTotalText(value: String) {
        tvTotal.text = "Total: $value"
    }

    fun setOnCashClick(listener: () -> Unit) {
        btnCash.setOnClickListener { listener() }
    }

    fun setOnCardClick(listener: () -> Unit) {
        btnCard.setOnClickListener { listener() }
    }

    fun setOnPixClick(listener: () -> Unit) {
        btnPix.setOnClickListener { listener() }
    }
}
