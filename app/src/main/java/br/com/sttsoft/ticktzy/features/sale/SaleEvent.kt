package br.com.sttsoft.ticktzy.features.sale

import android.content.Intent
import br.com.sttsoft.ticktzy.core.ui.UiEvent
import br.com.sttsoft.ticktzy.repository.local.product

sealed class SaleEvent : UiEvent {
    data object Load : SaleEvent()

    data class Search(val query: String) : SaleEvent()

    data class UpdateProducts(val list: List<product>) : SaleEvent()

    data class CashPay(val received: Double, val change: Double) : SaleEvent()

    data object PixPay : SaleEvent()

    data class CardPay(val type: String) : SaleEvent() // "debit"/"credit"

    data class PaymentResult(val resultCode: Int, val data: Intent?) : SaleEvent()
}
