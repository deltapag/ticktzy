package br.com.sttsoft.ticktzy.repository.remote.repositorys

import br.com.sttsoft.ticktzy.repository.remote.request.TerminalWrapper
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InfoRepository {

    @POST("pos/init")
    fun getInfos(@Body body: TerminalWrapper) : Call<InfoResponse>
}