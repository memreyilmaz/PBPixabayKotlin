package com.android.pixabay.di.util

import android.content.Context
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val cacheControl = CacheControl.Builder()
            .maxAge(1, TimeUnit.DAYS)
            .build()

        val request = chain.request()
            request.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        return chain.proceed(request)
    }
}