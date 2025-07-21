package br.com.sttsoft.ticktzy.domain

import android.content.Intent
import br.com.sttsoft.ticktzy.extensions.getFromPrefs
import br.com.sttsoft.ticktzy.extensions.toSitefFormat
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SitefUseCase {

    fun testConnection(infos: InfoResponse): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)

        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        i.putExtra("empresaSitef", "00000000")
        i.putExtra("enderecoSitef", "tls-prod.fiservapp.com:443")
        infos?.apply {
            i.putExtra("CNPJ_CPF", infos.Pagamento.Subadquirencia[0].cnpj)
        }
        i.putExtra("operador", "mSiTef")
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("modalidade", "111")
        i.putExtra("timeoutColeta", "30")

        return i
    }

    fun tokenConfig(infos: InfoResponse): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)

        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        i.putExtra("empresaSitef", "00000000")
        i.putExtra("enderecoSitef", "tls-prod.fiservapp.com:443")
        infos?.apply {
            i.putExtra("CNPJ_CPF", infos.Pagamento.Subadquirencia[0].cnpj)
        }
        i.putExtra("operador", "mSiTef")
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("modalidade", "699")
        i.putExtra("timeoutColeta", "30")

        return i
    }

    fun payment(infos: InfoResponse, valor: Double, modalidade: String, isPix: Boolean): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        i.putExtra("empresaSitef", "00000000")
        i.putExtra("enderecoSitef", "tls-prod.fiservapp.com:443")
        infos?.apply {

            i.putExtra("cnpj_automacao", infos.Pagamento.lojasSitef[0].cnpj)
            i.putExtra("CNPJ_CPF", infos.Pagamento.Subadquirencia[0].cnpj)
            i.putExtra("dadosSubAdqui", infos.Pagamento.Subadquirencia[0].nomeFantasia)
        }
        i.putExtra("operador", "mSiTef")
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("modalidade", modalidade)
        i.putExtra("valor", valor.toSitefFormat())
        i.putExtra("timeoutColeta", "30")
        i.putExtra("comExterna", "4")

        return i
    }

}