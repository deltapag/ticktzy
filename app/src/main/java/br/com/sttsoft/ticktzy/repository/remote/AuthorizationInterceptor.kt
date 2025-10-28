package br.com.sttsoft.ticktzy.repository.remote

import br.com.sttsoft.ticktzy.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds Authorization Bearer token to Delta Pag API requests
 */
class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.bearerToken}")
            .build()
        return chain.proceed(newRequest)
    }
}

/**
 * Interceptor that adds Parse API headers to Back4App requests
 */
class ParseApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("X-Parse-Application-Id", BuildConfig.parseAppId)
            .addHeader("X-Parse-REST-API-Key", BuildConfig.parseApiKey)
            .build()
        return chain.proceed(newRequest)
    }
}

