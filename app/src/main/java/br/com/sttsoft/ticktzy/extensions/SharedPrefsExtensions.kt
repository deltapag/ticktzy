package br.com.sttsoft.ticktzy.extensions

import android.content.Context
import com.google.gson.Gson

inline fun <reified T> T.saveToPrefs(
    context: Context,
    key: String,
) {
    val prefs = context.getSharedPreferences("APP_INFOS_SITEF", Context.MODE_PRIVATE)
    val json = Gson().toJson(this)
    prefs.edit().putString(key, json).apply()
}

inline fun <reified T> Context.getFromPrefs(key: String): T? {
    val prefs = getSharedPreferences("APP_INFOS_SITEF", Context.MODE_PRIVATE)
    val json = prefs.getString(key, null)
    return json?.let { Gson().fromJson(it, T::class.java) }
}

fun Context.savePref(
    key: String,
    value: Any,
) {
    val prefs = getSharedPreferences("APP_INFOS_TICKETZY", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        when (value) {
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Double -> putString(key, value.toString()) // salvar como string
            else -> throw IllegalArgumentException("Tipo não suportado: ${value::class}")
        }
        apply()
    }
}

inline fun <reified T> Context.getPref(
    key: String,
    default: T,
): T {
    val prefs = getSharedPreferences("APP_INFOS_TICKETZY", Context.MODE_PRIVATE)

    return when (T::class) {
        String::class -> prefs.getString(key, default as? String) as T
        Boolean::class -> prefs.getBoolean(key, default as Boolean) as T
        Int::class -> prefs.getInt(key, default as Int) as T
        Float::class -> prefs.getFloat(key, default as Float) as T
        Long::class -> prefs.getLong(key, default as Long) as T
        Double::class -> prefs.getString(key, default.toString())!!.toDouble() as T
        else -> throw IllegalArgumentException("Tipo não suportado: ${T::class}")
    }
}
