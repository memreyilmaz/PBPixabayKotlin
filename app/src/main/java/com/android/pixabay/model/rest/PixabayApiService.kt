package com.android.pixabay.model.rest

import com.android.pixabay.BASE_URL
import com.android.pixabay.KEY
import com.android.pixabay.model.ImageResponse
import com.android.pixabay.timber.BaseApplication
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

interface PixabayApiService {

    @GET(KEY)
    fun getSearchedbyPaging (@Query("q") query: String,
                             @Query("page") page: Int,
                             @Query("image_type") photo: String): Single<ImageResponse>

    companion object {
        val cacheSize = (10 * 1024 * 1024).toLong()
        val httpCacheDirectory = File(BaseApplication.appContext?.cacheDir,"http-cache")
        val cache = Cache(
            httpCacheDirectory,
            cacheSize
        )
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)

        val networkCacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())

            val cacheControl = CacheControl.Builder()
                .maxAge(1, TimeUnit.DAYS)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        val httpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(networkCacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        fun getService(): PixabayApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
            return retrofit.create(PixabayApiService::class.java)
        }
    }

}