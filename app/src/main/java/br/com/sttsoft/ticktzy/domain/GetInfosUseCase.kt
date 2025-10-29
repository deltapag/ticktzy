package br.com.sttsoft.ticktzy.domain

import br.com.sttsoft.ticktzy.repository.remote.MSCall
import br.com.sttsoft.ticktzy.repository.remote.repositorys.InfoRepository
import br.com.sttsoft.ticktzy.repository.remote.request.TerminalWrapper
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GetInfosUseCase(private val infosRequest: TerminalWrapper) {
    suspend fun invoke(): InfoResponse? {
        val playerRepository: InfoRepository = MSCall.createService(InfoRepository::class.java)
        val call: Call<InfoResponse> = playerRepository.getInfos(infosRequest)

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(
                object : Callback<InfoResponse> {
                    override fun onResponse(
                        call: Call<InfoResponse>,
                        response: Response<InfoResponse>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body())
                        } else {
                            val errorBody = response.errorBody()?.string()
                            continuation.resumeWithException(
                                Exception("Erro ${response.code()} - $errorBody"),
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<InfoResponse>,
                        t: Throwable,
                    ) {
                        continuation.resumeWithException(t)
                    }
                },
            )

            // cancela a chamada se a coroutine for cancelada
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}
