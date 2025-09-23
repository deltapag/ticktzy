package br.com.sttsoft.ticktzy.core.prefs

interface PrefsGateway {
    fun getInt(key: String, default: Int = 0): Int
    fun putInt(key: String, value: Int)
    fun getLong(key: String, default: Long = 0L): Long
    fun putLong(key: String, value: Long)
}