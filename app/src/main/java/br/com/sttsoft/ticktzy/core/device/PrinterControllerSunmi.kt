package br.com.sttsoft.ticktzy.core.device

import com.sunmi.peripheral.printer.SunmiPrinterService
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class PrinterControllerSunmi(
    private val serviceProvider: () -> SunmiPrinterService?
) : PrinterController {

    override fun printTicket(
        infos: InfoResponse,
        name: String,
        price: Double,
        onDone: () -> Unit
    ) {
        val service = serviceProvider() ?: return
        try {
            PrinterUseCase(service).ticketPrint(
                infos = infos, // se não precisa das infos
                productName = name,
                productPrice = price
            )
            onDone()
        } catch (_: Exception) {
            // ignora erro individual de ticket
        }
    }

    override fun printMoneyReceipt(
        infos: InfoResponse,
        valueReceived: Double,
        change: Double,
        total: Double,
        onDone: () -> Unit
    ) {
        val service = serviceProvider() ?: return
        try {
            PrinterUseCase(service).moneyReceiptPrint(
                infos = infos,
                valueReceived = valueReceived,
                valueChanged = change,
                valueCharged = total
            )
            onDone()
        } catch (_: Exception) {
            // idem: você pode logar o erro se quiser
        }
    }
}