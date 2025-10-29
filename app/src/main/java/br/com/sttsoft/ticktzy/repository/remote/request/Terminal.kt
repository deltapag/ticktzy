package br.com.sttsoft.ticktzy.repository.remote.request

data class Terminal(
    val Data: String,
    val Hora: String,
    val Fabricante: String = "Sumni",
    val Modelo: String = "P2",
    val NumeroSerie: String,
    val VrsApp: String,
    val VrsCliSitef: String,
    val TipoConexao: String,
    val MacAddress: String,
    val OperadoraTelecom: String,
)
