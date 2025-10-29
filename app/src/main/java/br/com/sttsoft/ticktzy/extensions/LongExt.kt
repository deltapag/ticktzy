package br.com.sttsoft.ticktzy.extensions

import java.text.NumberFormat
import java.util.Locale

fun Long.toRealFormatado(): String {
    val valor = this / 100.0
    val formato = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return formato.format(valor)
}
