package br.com.sttsoft.ticktzy.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import java.net.Inet4Address
import java.net.InetAddress

class ResolveSitefEndpointUseCase(private val context: Context) {

    data class Endpoint(
        val host: String,
        val port: Int,
        val comExterna: Boolean = true // para APN privada
    )

    fun resolve(): Endpoint? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return null
        val caps = cm.getNetworkCapabilities(network) ?: return null

        // Só interessa quando estiver em celular (chip)
        if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return null

        val lp = cm.getLinkProperties(network) ?: return null
        val ipv4 = lp.linkAddresses
            .firstOrNull { it.address is Inet4Address }
            ?.address as? Inet4Address
            ?: return null

        val carrierName = carrierNameOrEmpty().uppercase()

        // ====== Regras por sub-rede (mais preciso) ======
        // LYRA - SP1: sub-rede 172.25.250.0/24   -> destino 172.25.250.34:4096
        // LYRA - SP2: sub-rede 192.168.70.0/24   -> destino 192.168.70.34:4096
        when {
            inCidr(ipv4, "172.25.250.0/24") -> {
                return Endpoint(host = "172.25.250.34", port = 4096)
            }
            inCidr(ipv4, "192.168.70.0/24") -> {
                return Endpoint(host = "192.168.70.34", port = 4096)
            }
        }

        // ====== Regras por operadora (fallback) ======
        // Se a linha acima não pegou por sub-rede, usa nome do chip
        return when {
            "LYRA" in carrierName -> Endpoint("172.25.250.34", 4096) // default SP1
            "ALGAR" in carrierName -> Endpoint("172.25.42.232", /* confira! */ 17024)
            else -> null // sem mapeamento -> usar público/TLS
        }
    }

    // ===== Helpers =====

    private fun carrierNameOrEmpty(): String {
        val subId = SubscriptionManager.getDefaultDataSubscriptionId()
        val tm = context.getSystemService(TelephonyManager::class.java)
        val tms = try { tm?.createForSubscriptionId(subId) } catch (_: Exception) { tm }
        // Em muitas versões não precisa de READ_PHONE_STATE para o nome, mas protegemos com try/catch
        return try { tms?.simOperatorName ?: "" } catch (_: SecurityException) { "" }
    }

    private fun inCidr(ip: Inet4Address, cidr: String): Boolean {
        val (net, prefixStr) = cidr.split("/")
        val prefix = prefixStr.toInt()
        val netBytes = InetAddress.getByName(net).address
        val ipBytes = ip.address
        val mask = (-1 shl (32 - prefix))
        val netInt = toInt(netBytes) and mask
        val ipInt = toInt(ipBytes) and mask
        return netInt == ipInt
    }

    private fun toInt(bytes: ByteArray): Int {
        return (bytes[0].toInt() and 0xFF shl 24) or
                (bytes[1].toInt() and 0xFF shl 16) or
                (bytes[2].toInt() and 0xFF shl 8) or
                (bytes[3].toInt() and 0xFF)
    }
}