package br.com.sttsoft.ticktzy.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import br.com.sttsoft.ticktzy.BuildConfig
import br.com.sttsoft.ticktzy.repository.remote.request.Terminal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalnfosUseCase {

    fun invoke(context: Context): Terminal {
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
            TipoConexao = if (context.isOnCellular()) "CHIP" else "WIFI",
            MacAddress = "00:11:22:33:44:55",
            OperadoraTelecom = carrierNameOrDefault(context)
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

    private fun carrierNameOrDefault(context: Context): String {
        val subId = SubscriptionManager.getDefaultDataSubscriptionId()
        val tm = context.getSystemService(TelephonyManager::class.java)
        val tms = try { tm?.createForSubscriptionId(subId) } catch (_: Exception) { tm }
        // Em muitas versões não precisa de READ_PHONE_STATE para o nome, mas protegemos com try/catch
        return try { tms?.simOperatorName ?: "Claro" } catch (_: SecurityException) { "Claro" }
    }

    fun Context.isOnCellular(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false

        return caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }


}