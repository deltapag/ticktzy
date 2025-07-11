package br.com.sttsoft.ticktzy.repository.remote

import br.com.sttsoft.ticktzy.repository.remote.request.Terminal
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InfoRepository {

    @POST("pos/init")
    fun getInfos(@Body body: Terminal) : Call<InfoResponse>
}