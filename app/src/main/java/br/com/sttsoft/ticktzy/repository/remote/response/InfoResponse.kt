package br.com.sttsoft.ticktzy.repository.remote.response

data class InfoResponse(
    val retCod: String,
    val retDsc: String,
    val headerCupom: Boolean,
    val headerEstabelecimento: Boolean,
    val headerValor: Boolean,
    val maxDiasRelatorio: Int,
    val AtualizacaoRemota: AtualizacaoRemota,
    val Pagamento: Pagamento,
    val App: App
)