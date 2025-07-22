package br.com.sttsoft.ticktzy.domain

import br.com.sttsoft.ticktzy.extensions.toReal
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import com.sunmi.peripheral.printer.SunmiPrinterService
import java.util.Locale

class PrinterUseCase(val printerService: SunmiPrinterService?) {

    val boldOff = byteArrayOf(0x1B, 0x45, 0x00) // Negrito OFF
    val boldOn = byteArrayOf(0x1B, 0x45, 0x01)  // Negrito ON


    fun invoke() {
        printerService?.apply {
            sendRAWData(boldOff, null)
            lineWrap(1, null)

            setAlignment(1, null)
            setFontSize(24f, null)
            printText("Informações\n", null)
            printText("NOME / OUTRAS INFOS\n\n", null)

            sendRAWData(boldOn, null)
            setFontSize(36f, null)
            printText("TICKET 5\n", null)

            lineWrap(5, null)
            sendRAWData(boldOff, null)
        }
    }

    fun testPrinter() {
        printerService?.apply {
            sendRAWData(boldOff, null)
            lineWrap(1, null)

            setAlignment(1, null)
            setFontSize(24f, null)
            printText("TESTE DE IMPRESSÃO\n", null)
            printText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\n\n", null)

            sendRAWData(boldOn, null)
            printText("NEGRITO\n", null)

            lineWrap(5, null)
            sendRAWData(boldOff, null)
        }
    }

    fun ticketPrint(infos: InfoResponse, productName: String, productPrice: Double) {
        printerService?.apply {
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            // Inicial
            setAlignment(1, null) // Centralizado
            sendRAWData(boldOff, null)
            setFontSize(20f, null)
            printText(infos.Pagamento.lojasSitef.firstOrNull()!!.nomeLoja+"\n", null)

            lineWrap(1, null)

            // Item comprado
            sendRAWData(boldOn, null)
            setFontSize(32f, null)
            printText(productName.uppercase()+"\n", null)

            sendRAWData(boldOff, null)
            setFontSize(28f, null)
            printText(productPrice.toReal(), null)

            lineWrap(2, null)

            // Rodapé
            sendRAWData(boldOn, null)
            setFontSize(20f, null)
            printText("Cuide bem destas fichas!\n", null)
            printText("São os comprovantes do seu consumo\n", null)
            sendRAWData(boldOff, null)

            lineWrap(4, null)
        }
    }


}