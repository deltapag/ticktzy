package br.com.sttsoft.ticktzy.extensions

import java.text.NumberFormat
import java.util.Locale

fun Double.toReal(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(this)
}

fun Double.toSitefFormat(): String {
    val valorEmCentavos = (this * 100).toLong()
    return valorEmCentavos.toString().padStart(12, '0')
}