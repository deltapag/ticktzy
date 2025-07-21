package br.com.sttsoft.ticktzy.domain

import android.os.Build
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.repository.remote.request.Terminal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalnfosUseCase {

    fun invoke(): Terminal {
        val sdfData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Date()

        return Terminal(
            Data = sdfData.format(now),
            Hora = sdfHora.format(now),
            Fabricante = Build.MANUFACTURER ?: "Desconhecido",
            Modelo = Build.MODEL ?: "Desconhecido",
            NumeroSerie = getSerialFromSystem(),
            VrsApp = "v"+ BuildConfig.VERSION_NAME,
            VrsCliSitef = "2.0.0",
            TipoConexao = "WIFI",
            MacAddress = "00:11:22:33:44:55",
            OperadoraTelecom = "Claro"
        )
    }

    fun getSerialFromSystem(): String {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val method = clazz.getMethod("get", String::class.java, String::class.java)
            method.invoke(null, "ro.serialno", "Serial Desconhecido") as String
        } catch (e: Exception) {
            "Serial Erro"
        }
    }

}