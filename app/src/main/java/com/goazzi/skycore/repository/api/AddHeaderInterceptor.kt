package com.goazzi.skycore.repository.api

import com.goazzi.skycore.BuildConfig
import com.goazzi.skycore.misc.Constants
import okhttp3.Interceptor
import okhttp3.Response

class AddHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder =
            chain.request()
                .newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
        return chain.proceed(requestBuilder)
    }
}