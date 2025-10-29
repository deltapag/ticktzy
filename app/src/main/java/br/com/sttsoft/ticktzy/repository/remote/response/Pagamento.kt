package br.com.sttsoft.ticktzy.repository.remote.response

data class Pagamento(
    val sitefPublico: Sitef,
    val sitefPrivado: Sitef,
    val lojasSitef: List<LojaSitef>,
    val Subadquirencia: List<Subadquirencia>,
)
