package br.com.sttsoft.ticktzy.domain

import android.util.Log
import br.com.sttsoft.ticktzy.repository.remote.InfoRepository
import br.com.sttsoft.ticktzy.repository.remote.MSCall
import br.com.sttsoft.ticktzy.repository.remote.request.Terminal
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetInfosUseCase(private var infosRequest: Terminal) {

    fun invoke(
        onSuccess: (InfoResponse?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val playerRepository: InfoRepository = MSCall.createService(InfoRepository::class.java)

        val call: Call<InfoResponse> = playerRepository.getInfos(infosRequest)

        call.enqueue(object : Callback<InfoResponse> {
            override fun onResponse(
                call: Call<InfoResponse>,
                response: Response<InfoResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError(Throwable("Erro ${response.code()} - ${response.errorBody()?.string()}"))
                }
            }

            override fun onFailure(call: Call<InfoResponse>, t: Throwable) {
                Log.e("GETINFOSUSECASE", "Erro na chamada Retrofit", t)
                onError(t)
            }
        })
    }
}