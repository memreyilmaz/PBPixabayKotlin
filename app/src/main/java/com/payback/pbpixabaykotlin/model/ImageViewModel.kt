package com.payback.pbpixabaykotlin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payback.pbpixabaykotlin.rest.PixabayApiClient
import com.payback.pbpixabaykotlin.rest.PixabayApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ImageViewModel : ViewModel() {
    var images: MutableLiveData<List<Hit>> = MutableLiveData()
    private val photo: String = "photo"

    fun loadImages(searchQuery : String) {
        val apiService: PixabayApiInterface = PixabayApiClient.getClient()

        val call = apiService.getSearched(searchQuery, photo)
        call.enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                Timber.i("Request Url: %s", call.request().url().toString())
                Timber.i("Response code: %s", response.code())
                if(response.raw().cacheResponse() != null){
                    Timber.i("Response come from cache")
                }

                if(response.raw().networkResponse() != null){
                    Timber.i("Response come from network")
                }
                images.value = response.body()?.hits
            }
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Timber.e(t.toString())
            }
        })
    }

    fun getImages(): LiveData<List<Hit>> {
        return images
    }
}