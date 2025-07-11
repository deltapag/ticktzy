package br.com.sttsoft.ticktzy.repository.remote

import br.com.sttsoft.ticktzy.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MSCall {

    companion object {

        private val baseUrl = BuildConfig.urlDelta

        private val client = OkHttpClient()
            .newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

        private val builder: Retrofit.Builder = Retrofit.Builder().baseUrl(baseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create())

        private val retrofit = builder.build()

        fun <S> createService(serviceClass: Class<S>?): S {
            return retrofit.create<S>(serviceClass)
        }
    }

}