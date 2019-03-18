package com.payback.pbpixabaykotlin.rest

import com.payback.pbpixabaykotlin.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PixabayApiClient {

    fun getClient(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}