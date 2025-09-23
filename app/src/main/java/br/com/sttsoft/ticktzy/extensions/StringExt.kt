package br.com.sttsoft.ticktzy.extensions

fun String.removeSpecialChars(): String {
    return this.replace(Regex("[^A-Za-z0-9]"), "")
}