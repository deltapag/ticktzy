package br.com.sttsoft.ticktzy.repository.remote.response

data class AtualizacaoRemota(
    val flagAtualizarVersao: String,
    val Ip: String,
    val Porta: String,
    val Recurso: String,
    val Param: String,
    val Valor: String,
)
