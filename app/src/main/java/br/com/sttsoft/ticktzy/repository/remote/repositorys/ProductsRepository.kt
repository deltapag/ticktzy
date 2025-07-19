package br.com.sttsoft.ticktzy.repository.remote.repositorys

import br.com.sttsoft.ticktzy.repository.local.product
import br.com.sttsoft.ticktzy.repository.remote.response.ProductsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductsRepository {

    @Headers(
        "Content-Type: application/json",
        "X-Parse-Application-Id: 44Ipo5EE0DDLvducULikdSG6tVbnlyNhTWcQlabp",
        "X-Parse-REST-API-Key: 9U3oQCXujFZPGZQzIV8d36WiAL4VcRGxlfdx8wPp"
    )
    @GET("classes/products")
    fun getProductsByCnpj(@Query("where") where: String): Call<ProductsResponse>



    @Headers(
        "Content-Type: application/json",
        "X-Parse-Application-Id: 44Ipo5EE0DDLvducULikdSG6tVbnlyNhTWcQlabp",
        "X-Parse-REST-API-Key: 9U3oQCXujFZPGZQzIV8d36WiAL4VcRGxlfdx8wPp"
    )
    @POST("classes/products")
    fun setProduct(@Body product: product): Call<ResponseBody>
}