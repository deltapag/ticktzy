package br.com.sttsoft.ticktzy.domain

import android.util.Log
import br.com.sttsoft.ticktzy.extensions.toReal
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import com.sunmi.peripheral.printer.SunmiPrinterService
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
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


    fun moneyReceiptPrint(
        infos: InfoResponse,
        valueReceived: Double,
        valueChanged: Double,
        valueCharged: Double
    ) {
        printerService?.apply {
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            val now = Date()
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val formattedDate = formatter.format(now)

            // Cabeçalho
            setAlignment(1, null) // Centralizado
            sendRAWData(boldOn, null)
            setFontSize(20f, null)
            printText(infos.Pagamento.lojasSitef.firstOrNull()!!.nomeLoja+"\n", null)
            setFontSize(26f, null)
            printText("PAGAMENTO EM DINHEIRO\n", null)

            setFontSize(20f, null)
            printText("$formattedDate\n", null)

            lineWrap(1, null)

            // Valores
            setAlignment(0, null) // Esquerda
            sendRAWData(boldOff, null)
            setFontSize(22f, null)
            printText("VALOR COBRADO:   ${valueCharged.toReal()}\n", null)
            printText("VALOR RECEBIDO:  ${valueReceived.toReal()}\n", null)

            // Separador
            printText("------------------------------\n", null)

            // Troco em destaque
            sendRAWData(boldOn, null)
            printText("TROCO:           ${valueChanged.toReal()}\n", null)
            sendRAWData(boldOff, null)

            lineWrap(2, null)

            // Rodapé
            setAlignment(1, null)
            setFontSize(20f, null)
            printText("Sem validade fiscal.\nApenas para registro!\n", null)

            lineWrap(4, null)
        }
    }

    fun ticketPrint(infos: InfoResponse, productName: String, productPrice: Double) {
        printerService?.apply {
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            val now = Date()
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val formattedDate = formatter.format(now)

            // Inicial
            setAlignment(1, null) // Centralizado
            sendRAWData(boldOff, null)
            setFontSize(20f, null)
            printText(infos.Pagamento.lojasSitef.firstOrNull()!!.nomeLoja+"\n", null)
            printText("$formattedDate\n", null)

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

    fun printInfo(info:String, value: String) {
        printerService?.apply {
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            // Inicial
            setAlignment(1, null) // Centralizado
            sendRAWData(boldOn, null)
            setFontSize(22f, null)
            printText("COMPROVANTE DE " + info + " \n", null)

            lineWrap(1, null)

            // Item comprado
            setFontSize(26f, null)
            printText("Valor: " + value + " \n", null)
            setFontSize(22f, null)
            printText(getDataHoraFormatada(), null)
            sendRAWData(boldOff, null)

            lineWrap(2, null)

            setFontSize(22f, null)
            printText("Sem validade fiscal.\n Apenas para registro!\n", null)

            lineWrap(4, null)
        }
    }

    fun getDataHoraFormatada(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale("pt", "BR"))
        return formato.format(Date())
    }

    fun printFinish() {
        printerService?.apply {
            val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
            val boldOff = byteArrayOf(0x1B, 0x45, 0x00)

            // Inicial
            setAlignment(1, null) // Centralizado
            sendRAWData(boldOn, null)
            setFontSize(22f, null)
            printText("COMPROVANTE DE FECHAMENTO\n", null)

            lineWrap(1, null)

            // Item comprado
            setFontSize(26f, null)
            printText("CAIXA FECHADO\n", null)
            setFontSize(22f, null)
            printText(getDataHoraFormatada(), null)
            sendRAWData(boldOff, null)

            lineWrap(2, null)

            setFontSize(22f, null)
            printText("Sem validade fiscal.\n Apenas para registro!\n", null)

            lineWrap(4, null)
        }
    }

}