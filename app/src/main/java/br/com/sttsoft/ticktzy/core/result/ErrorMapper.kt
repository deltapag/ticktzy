package br.com.sttsoft.ticktzy.core.result

fun Throwable.toErrorModel(defaultMsg: String = "Falha inesperada"): ErrorModel =
    ErrorModel(
        message = message ?: defaultMsg,
        cause = this,
    )
