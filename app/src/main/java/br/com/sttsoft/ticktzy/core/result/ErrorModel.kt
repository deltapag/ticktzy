package br.com.sttsoft.ticktzy.core.result

data class ErrorModel(
    val code: String = "UNKNOWN",
    val message: String = "Ocorreu um erro",
    val cause: Throwable? = null,
)
