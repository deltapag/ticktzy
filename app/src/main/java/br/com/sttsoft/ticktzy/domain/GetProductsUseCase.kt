package br.com.sttsoft.ticktzy.domain

import android.util.Log
import br.com.sttsoft.ticktzy.repository.remote.MSCallProducts
import br.com.sttsoft.ticktzy.repository.remote.repositorys.ProductsRepository
import br.com.sttsoft.ticktzy.repository.remote.response.ProductsResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GetProductsUseCase {

    suspend fun invoke(cnpj: String): ProductsResponse? {
        val pRepo: ProductsRepository = MSCallProducts.createService(ProductsRepository::class.java)
        val where = JSONObject().put("cnpj", cnpj).toString()
        val call: Call<ProductsResponse> = pRepo.getProductsByCnpj(where)

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<ProductsResponse> {
                override fun onResponse(call: Call<ProductsResponse>, response: Response<ProductsResponse>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    } else {
                        val error = response.errorBody()?.string()
                        continuation.resumeWithException(Exception("Erro ${response.code()} - $error"))
                    }
                }

                override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                    Log.e("GetProductsUseCase", "Erro na chamada Retrofit", t)
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}