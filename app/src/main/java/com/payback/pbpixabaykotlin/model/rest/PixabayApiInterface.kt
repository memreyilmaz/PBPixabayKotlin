package com.payback.pbpixabaykotlin.model.rest

import com.payback.pbpixabaykotlin.KEY
import com.payback.pbpixabaykotlin.model.ImageResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiInterface {

    @GET(KEY)
    fun getSearched (@Query("q") query: String,
               //      @Query("page") page: Int,
                     @Query("image_type") photo: String): Single<ImageResponse>
}