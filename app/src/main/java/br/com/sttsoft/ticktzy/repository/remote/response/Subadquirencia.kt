package br.com.sttsoft.ticktzy.repository.remote.response

data class Subadquirencia(
    var nomeFantasia: String,
    val endereco: String,
    val cidade: String,
    val UF: String,
    val pais: String,
    val cep: String,
    val mcc: String,
    var cnpj: String,
    val telefoneNro: String,
    val idEstabelecimento: String,
    val email: String,
    val razaoSocial: String,
    val tipoPessoa: String
)
