package br.com.sttsoft.ticktzy.domain

import android.content.Context
import android.content.Intent
import androidx.collection.intSetOf
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.isUsingCellular
import br.com.sttsoft.ticktzy.extensions.removeSpecialChars
import br.com.sttsoft.ticktzy.extensions.toSitefFormat
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SitefUseCase(var context: Context) {

    private fun getDefaultIntentParameters(infos: InfoResponse, isTLSEnabled: Boolean = false) : Intent {
        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")

        i.putExtra("operador", "mSiTef")
        i.putExtra("timeoutColeta", "30")

        infos?.apply {

            i.putExtra("enderecoSitef", infos.Pagamento.sitefPublico.ip+":"+infos.Pagamento.sitefPublico.porta)
            /*if (isTLSEnabled) {
                i.putExtra("enderecoSitef", "tls-prod.fiservapp.com:443")
            } else {
                i.putExtra("enderecoSitef", infos.Pagamento.sitefPublico.ip+":"+infos.Pagamento.sitefPublico.porta)
            }*/

            i.putExtra("empresaSitef", infos.Pagamento.lojasSitef.first().codigoLojaSitef)
            i.putExtra("cnpj_automacao", infos.Pagamento.lojasSitef.first().cnpj.removeSpecialChars())
            i.putExtra("CNPJ_CPF", infos.Pagamento.lojasSitef.first().cnpj.removeSpecialChars())
            i.putExtra("dadosSubAdqui", GetDadosSubUseCase().getDadosSub(infos))
        }

        if (context.isUsingCellular()) {
            i.putExtra("comExterna", "1")
        } else {
            i.putExtra("comExterna", "0")
        }

        /*
        if (isTLSEnabled) {
            i.putExtra("comExterna", "1")
        } else {
            i.putExtra("comExterna", "0")
        }*/

        return i
    }


    fun testConnection(infos: InfoResponse): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos)

        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("modalidade", "111")

        return i
    }

    fun tokenConfig(infos: InfoResponse): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)

        var i = Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF")
        i.putExtra("enderecoSitef", "tls-prod.fiservapp.com:443")
        infos?.apply {
            i.putExtra("empresaSitef", infos.Pagamento.lojasSitef[0].codigoLojaSitef)
            i.putExtra("CNPJ_CPF", infos.Pagamento.Subadquirencia[0].cnpj)
        }
        i.putExtra("operador", "mSiTef")
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("modalidade", "699")
        i.putExtra("timeoutColeta", "30")

        return i
    }

    fun payment(infos: InfoResponse, valor: Double, modalidade: String, isPix: Boolean, isTLSEnabled: Boolean = false): Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos, isTLSEnabled)

        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("modalidade", modalidade)
        i.putExtra("valor", valor.toSitefFormat())

        return i
    }

    fun trace(infos: InfoResponse, isTLSEnabled: Boolean = false) : Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos, isTLSEnabled)

        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("modalidade", "121")

        return i
    }

    fun cancelation(infos: InfoResponse, isTLSEnabled: Boolean = false) : Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos, isTLSEnabled)
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("restricoes", "TransacoesAdicionaisHabilitadas=8;3919")
        i.putExtra("modalidade", "200")

        return i
    }

    fun reprint(infos: InfoResponse, isTLSEnabled: Boolean = false) : Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos, isTLSEnabled)
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("modalidade", "112");

        return i
    }

    fun directAccess(infos: InfoResponse, isTLSEnabled: Boolean = false) : Intent {
        val calendar = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault())
        val cupomFormatter = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)
        val numeroCupom = cupomFormatter.format(calendar.time)

        var i = getDefaultIntentParameters(infos, isTLSEnabled)
        i.putExtra("operador", "mSiTef")
        i.putExtra("data", date)
        i.putExtra("hora", time)
        i.putExtra("numeroCupom", numeroCupom)
        i.putExtra("modalidade", "110");

        return i
    }

}