package br.com.sttsoft.ticktzy.domain

import android.util.Log
import br.com.sttsoft.ticktzy.repository.remote.InfoRepository
import br.com.sttsoft.ticktzy.repository.remote.MSCall
import br.com.sttsoft.ticktzy.repository.remote.request.Terminal
import br.com.sttsoft.ticktzy.repository.remote.response.InfoResponse
import retrofit2.Call

class GetInfosUseCase(private var infosRequest: Terminal) {

    fun invoke() {
         val playerRepository: InfoRepository = MSCall.createService(InfoRepository::class.java)

         try {

             val call = playerRepository.getInfos(infosRequest)

             call.execute()


         } catch (e: Exception) {
             Log.e("GETINFOSUSECASE", "invoke: ", e)
         }
     }
}