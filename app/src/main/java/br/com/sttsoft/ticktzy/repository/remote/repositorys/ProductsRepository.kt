package br.com.sttsoft.ticktzy.repository.remote.repositorys

import br.com.sttsoft.ticktzy.repository.local.product
import br.com.sttsoft.ticktzy.repository.remote.response.ProductsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductsRepository {
    @GET("classes/products")
    fun getProductsByCnpj(
        @Query("where") where: String,
    ): Call<ProductsResponse>

    @POST("classes/products")
    fun setProduct(
        @Body product: product,
    ): Call<ResponseBody>
}
