package br.com.sttsoft.ticktzy.core.device

import br.com.execucao.posmp_api.printer.PrinterService
import br.com.execucao.smartPOSService.printer.IOnPrintFinished

class PosmpReceiptControllerImpl(
    private val serviceProvider: () -> PrinterService?,
) : PosmpReceiptController {
    override fun isAvailable(): Boolean = serviceProvider() != null

    override fun printReceipt(
        rawText: String,
        preFormat: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val svc = serviceProvider() ?: return onError("Impressora indisponível")

        val text = if (preFormat) format(rawText) else rawText

        svc.printText(
            text,
            object : IOnPrintFinished.Stub() {
                override fun onSuccess() = onSuccess()

                override fun onFailed(
                    error: Int,
                    msg: String,
                ) = onError("Erro $error: $msg")
            },
        )
    }

    private fun format(s: String): String =
        s.replace(": ", ":")
            .replace(" T", "T")
            .replace(" R", "R")
            .replace(" F", "F")
}
