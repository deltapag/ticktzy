package br.com.sttsoft.ticktzy.core.prefs

import android.content.Context
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref

class PrefsGatewayImpl(private val context: Context) : PrefsGateway {
    override fun getInt(
        key: String,
        default: Int,
    ) = context.getPref(key, default)

    override fun putInt(
        key: String,
        value: Int,
    ) = context.savePref(key, value)

    override fun getLong(
        key: String,
        default: Long,
    ) = context.getPref(key, default)

    override fun putLong(
        key: String,
        value: Long,
    ) = context.savePref(key, value)
}
