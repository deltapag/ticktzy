package br.com.sttsoft.ticktzy.repository.local

data class product(
    val cnpj: String,
    val name: String,
    val photo: String,
    val price: Double,
    var isSelected: Boolean = false,
    var quantity: Int = 0,
    var habilitado: Boolean = false
)