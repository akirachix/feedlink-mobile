package com.feedlink.feedlink.api
import com.feedlink.feedlink.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        TokenManager.getAuthToken()?.let { token ->
            request.header("Authorization", "Token $token")
        }
        return chain.proceed(request.build())
    }
}