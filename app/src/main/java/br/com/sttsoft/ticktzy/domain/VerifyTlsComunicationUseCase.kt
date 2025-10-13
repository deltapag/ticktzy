package br.com.sttsoft.ticktzy.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class VerifyTlsComunicationUseCase(var context: Context) {
    fun comunicacaoPermiteTls(): Boolean {
        var result = true
        // Aqui estamos assumindo que a comunicação por Celular sempre usa uma
        // APN Privada. Se isso não for verdade, a aplicação deverá implementar um
        // mecanismo adicional para determinal se APN é privada ou não.
        // Essa verificação provavelmente dependerá do modelo do Android.
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = false
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        result = true
                    }
                }
            }
        } else {
            if (cm != null) {
                val activeNetworkInfo = cm.activeNetworkInfo
                if (activeNetworkInfo != null) {
                    if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                        result = false
                    } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET) {
                        result = true
                    }
                }
            }
        }
        return result
    }
}