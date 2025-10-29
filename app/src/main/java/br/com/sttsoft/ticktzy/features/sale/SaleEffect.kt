package br.com.sttsoft.ticktzy.features.sale

import android.content.Intent
import br.com.sttsoft.ticktzy.core.ui.UiEffect

sealed class SaleEffect : UiEffect {
    data class PrintEstablishmentReceipt(val estab: String, val client: String?) : SaleEffect()

    data class AskToPrintCustomerCopy(val client: String) : SaleEffect()

    data class LaunchPayment(val intent: Intent) : SaleEffect()

    data class Toast(val message: String, val long: Boolean = false) : SaleEffect()

    data object PrintTickets : SaleEffect()

    data object Close : SaleEffect()
}
