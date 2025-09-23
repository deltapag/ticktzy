package br.com.sttsoft.ticktzy.features.sale

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.viewModelScope
import br.com.sttsoft.ticktzy.core.prefs.PrefsGateway
import br.com.sttsoft.ticktzy.core.ui.BaseViewModel
import br.com.sttsoft.ticktzy.domain.ProductCacheUseCase
import br.com.sttsoft.ticktzy.domain.SitefUseCase
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import kotlinx.coroutines.launch

class SaleViewModel(
    private val productCache: ProductCacheUseCase,
    private val sitef: SitefUseCase,
    private val prefs: PrefsGateway
) : BaseViewModel<SaleState, SaleEvent, SaleEffect>(SaleState()) {

    var infos: InfoResponse? = null
        private set

    private var paymentType: String? = null

    override fun onEvent(event: SaleEvent) {
        when (event) {
            SaleEvent.Load -> load()
            is SaleEvent.Search -> search(event.query)
            is SaleEvent.UpdateProducts -> setState {
                copy(filtered = event.list, total = event.list.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) })
            }
            is SaleEvent.CashPay -> cashPay(event.received, event.change)
            is SaleEvent.PixPay -> pixPay()
            is SaleEvent.CardPay -> cardPay(event.type)
            is SaleEvent.PaymentResult -> handlePaymentResult(event.resultCode, event.data)
        }
    }

    fun setInfos(value: InfoResponse?) { infos = value }

    private fun load() = viewModelScope.launch {
        setState { copy(loading = true) }
        val prods = productCache.lerProdutos()
        setState { copy(loading = false, products = prods, filtered = prods,
            total = prods.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) }) }
    }

    private fun search(q: String) {
        val base = state.value.products
        val filtered = if (q.isBlank()) base else base.filter { it.name.contains(q, true) }
        setState { copy(query = q, filtered = filtered,
            total = filtered.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) }) }
    }

    private fun cashPay(received: Double, change: Double) = viewModelScope.launch {
        val total = state.value.total
        if (total == 0.0) return@launch sendEffect { SaleEffect.Toast("Sem produtos adicionados!", true) }
        if (received <= 0.0 || received < total) return@launch sendEffect { SaleEffect.Toast("Valor insuficiente!", true) }

        prefs.putInt("SALES_MADE", prefs.getInt("SALES_MADE", 0) + 1)
        prefs.putInt("MONEY_TYPE", prefs.getInt("MONEY_TYPE", 0) + 1)

        val cents = ((received - change) * 100).toLong()
        prefs.putLong("CAIXA", prefs.getLong("CAIXA", 0L) + cents)

        paymentType = "money"
        sendEffect { SaleEffect.PrintTickets }
        sendEffect { SaleEffect.Close }
    }

    private fun pixPay() = viewModelScope.launch {
        val total = state.value.total
        if (total == 0.0) return@launch sendEffect { SaleEffect.Toast("Sem produtos adicionados!", true) }
        paymentType = "pix"
        launchPayment(mod = "122", isPix = true)
    }

    private fun cardPay(type: String) = viewModelScope.launch {
        val total = state.value.total
        if (total == 0.0) return@launch sendEffect { SaleEffect.Toast("Sem produtos adicionados!", true) }
        paymentType = type
        launchPayment(mod = if (type == "debit") "2" else "3")
    }

    private fun launchPayment(mod: String, isPix: Boolean = false, isTLSEnabled: Boolean = false) = viewModelScope.launch {
        val i = infos ?: return@launch sendEffect { SaleEffect.Toast("Infos do SiTef ausentes.", true) }

        val intent = sitef.payment(i, state.value.total, mod, isPix, isTLSEnabled)
        sendEffect { SaleEffect.LaunchPayment(intent) }
    }

    private fun handlePaymentResult(resultCode: Int, data: Intent?) = viewModelScope.launch {
        val b = data?.extras ?: return@launch
        if (resultCode != Activity.RESULT_OK) return@launch

        prefs.putInt("SALES_MADE", prefs.getInt("SALES_MADE", 0) + 1)

        val estab = b.getString("VIA_ESTABELECIMENTO")
        val client = b.getString("VIA_CLIENTE")

        if (!estab.isNullOrBlank()) {
            // 1) manda imprimir a via do estabelecimento AGORA (na UI)
            sendEffect { SaleEffect.PrintEstablishmentReceipt(estab, client) }
        } else if (!client.isNullOrBlank()) {
            // caso raro: só veio a via do cliente
            sendEffect { SaleEffect.AskToPrintCustomerCopy(client) }
        } else {
            // sem comprovantes — segue pros tickets
            sendEffect { SaleEffect.PrintTickets }
        }
    }

    fun onTicketsPrinted() {
        when (paymentType) {
            "pix" -> prefs.putInt("PIX_TYPE", prefs.getInt("PIX_TYPE", 0) + 1)
            "debit" -> prefs.putInt("DEBIT_TYPE", prefs.getInt("DEBIT_TYPE", 0) + 1)
            "credit" -> prefs.putInt("CREDIT_TYPE", prefs.getInt("CREDIT_TYPE", 0) + 1)
        }
    }
}