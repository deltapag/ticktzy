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
    private val btnCard: LinearLayout
    private val btnPix: LinearLayout

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.component_payment_bar, this, true)

        tvTotal = findViewById(R.id.tv_total)
        btnCard = findViewById(R.id.ll_pay)
        btnPix = findViewById(R.id.iv_pix)
    }

    fun setTotalText(value: String) {
        tvTotal.text = "Total: $value"
    }

    fun setOnCardClick(listener: () -> Unit) {
        btnCard.setOnClickListener { listener() }
    }

    fun setOnPixClick(listener: () -> Unit) {
        btnPix.setOnClickListener { listener() }
    }
}
