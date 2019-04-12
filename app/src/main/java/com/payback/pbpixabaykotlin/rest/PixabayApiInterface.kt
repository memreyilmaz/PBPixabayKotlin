package com.payback.pbpixabaykotlin.rest

import com.payback.pbpixabaykotlin.KEY
import com.payback.pbpixabaykotlin.model.ImageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiInterface {

    @GET(KEY)
    fun getSearched (@Query("q") query: String,
               //      @Query("page") page: Int,
                     @Query("image_type") photo: String): Call<ImageResponse>
}