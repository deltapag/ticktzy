package br.com.sttsoft.ticktzy.domain

import com.sunmi.peripheral.printer.SunmiPrinterService

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


}