package br.com.sttsoft.ticktzy.core.device

import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

interface PrinterController {
    fun printTicket(infos: InfoResponse, name: String, price: Double, onDone: () -> Unit = {})
    fun printMoneyReceipt(
        infos: InfoResponse,
        valueReceived: Double,
        change: Double,
        total: Double,
        onDone: () -> Unit = {}
    )
}