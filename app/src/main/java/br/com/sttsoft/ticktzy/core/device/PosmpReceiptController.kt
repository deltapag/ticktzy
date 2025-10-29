package br.com.sttsoft.ticktzy.core.device

interface PosmpReceiptController {
    fun isAvailable(): Boolean

    fun printReceipt(
        rawText: String,
        preFormat: Boolean = true,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
    )
}
