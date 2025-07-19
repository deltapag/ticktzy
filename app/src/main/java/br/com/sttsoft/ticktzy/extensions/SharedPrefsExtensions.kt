package br.com.sttsoft.ticktzy.extensions

import android.content.Context
import com.google.gson.Gson

inline fun <reified T> T.saveToPrefs(context: Context, key: String) {
    val prefs = context.getSharedPreferences("APP_INFOS_SITEF", Context.MODE_PRIVATE)
    val json = Gson().toJson(this)
    prefs.edit().putString(key, json).apply()
}

inline fun <reified T> Context.getFromPrefs(key: String): T? {
    val prefs = getSharedPreferences("APP_INFOS_SITEF", Context.MODE_PRIVATE)
    val json = prefs.getString(key, null)
    return json?.let { Gson().fromJson(it, T::class.java) }
}