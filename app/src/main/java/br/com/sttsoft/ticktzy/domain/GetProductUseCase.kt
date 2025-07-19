package br.com.sttsoft.ticktzy.domain

import android.util.Log
import br.com.sttsoft.ticktzy.repository.local.product
import br.com.sttsoft.ticktzy.repository.remote.MSCallProducts
import br.com.sttsoft.ticktzy.repository.remote.repositorys.ProductsRepository
import br.com.sttsoft.ticktzy.repository.remote.response.ProductsResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetProductUseCase {

    fun invoke(
        cnpj: String,
        onSuccess: (ProductsResponse?) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val pRepo: ProductsRepository = MSCallProducts.createService(ProductsRepository::class.java)

        val where = JSONObject().put("cnpj", cnpj).toString()

        val call: Call<ProductsResponse> = pRepo.getProductsByCnpj(where)

        call.enqueue(object : Callback<ProductsResponse> {
            override fun onResponse(
                call: Call<ProductsResponse>,
                response: Response<ProductsResponse>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    onError(Throwable("Erro ${response.code()} - ${response.errorBody()?.string()}"))
                }
            }

            override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                Log.e("GETINFOSUSECASE", "Erro na chamada Retrofit", t)
                onError(t)
            }
        })
    }
}