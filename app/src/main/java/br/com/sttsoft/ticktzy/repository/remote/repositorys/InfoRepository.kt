package br.com.sttsoft.ticktzy.repository.remote.repositorys

import br.com.sttsoft.ticktzy.repository.remote.request.TerminalWrapper
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface InfoRepository {

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 9712f0e92fb7a94b55bc3c23875e8951218635fee146b3932393e782991e5b46"
    )
    @POST("pos/init")
    fun getInfos(@Body body: TerminalWrapper) : Call<InfoResponse>
}