package br.com.sttsoft.ticktzy.extensions

import android.util.Patterns

fun String.removeSpecialChars(): String {
    return this.replace(Regex("[^A-Za-z0-9]"), "")
}

fun String.isLink(): Boolean {
    return Patterns.WEB_URL.matcher(this.trim()).matches()
}