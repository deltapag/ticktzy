package br.com.sttsoft.ticktzy.presentation.cashier.finish.components


sealed class tableInfos {
    data class tableRow(val chave: String, val valor: String): tableInfos()
    data class tableSection(val titulo: String): tableInfos()
}