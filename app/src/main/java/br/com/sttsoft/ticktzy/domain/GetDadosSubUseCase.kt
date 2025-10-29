package br.com.sttsoft.ticktzy.domain

import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse

class GetDadosSubUseCase {
    fun getDadosSub(infos: InfoResponse): String {
        val s = infos.Pagamento.Subadquirencia[0]

        return buildString {
            append("00").append(count(useValidInformation(s.nomeFantasia))).append(useValidInformation(s.nomeFantasia))
            append("01").append(count(useValidInformation(s.endereco))).append(useValidInformation(s.endereco))
            append("02").append(count(useValidInformation(s.cidade))).append(useValidInformation(s.cidade))
            append("03").append(count(useValidInformation(s.UF))).append(useValidInformation(s.UF))
            append("04").append(count(useValidInformation(s.pais))).append(useValidInformation(s.pais))
            append("05").append(count(useValidInformation(s.cep))).append(useValidInformation(s.cep))
            append("06").append(count(useValidInformation(s.mcc))).append(useValidInformation(s.mcc))
            append("07").append(count(useValidInformation(s.cnpj))).append(useValidInformation(s.cnpj))
            append("08").append(count(useValidInformation(s.telefoneNro))).append(useValidInformation(s.telefoneNro))
            append(
                "09",
            ).append(count(useValidInformation(s.idEstabelecimento))).append(useValidInformation(s.idEstabelecimento))
            append("10").append(count(useValidInformation(s.email))).append(useValidInformation(s.email))
            append("11").append(count(useValidInformation(s.razaoSocial))).append(useValidInformation(s.razaoSocial))
            append("12").append(count(useValidInformation(s.tipoPessoa))).append(useValidInformation(s.tipoPessoa))
        }
    }

    private fun useValidInformation(
        data: String?,
        defaultValue: String = "",
    ): String {
        if (data == null) {
            return defaultValue
        } else {
            return data
        }
    }

    private fun count(data: String): String {
        if (data.length < 10) {
            return "0" + data.length
        } else {
            return data.length.toString()
        }
    }
}
