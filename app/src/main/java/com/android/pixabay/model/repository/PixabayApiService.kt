package com.android.pixabay.model.repository

import com.android.pixabay.model.ImageResponse
import com.android.pixabay.utils.KEY
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {

    @GET(KEY)
    fun getSearch (@Query("q") query: String,
                   @Query("page") page: Int): Single<ImageResponse>
}