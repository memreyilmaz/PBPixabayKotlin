package com.payback.pbpixabaykotlin.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payback.pbpixabaykotlin.rest.PixabayApiClient
import com.payback.pbpixabaykotlin.rest.PixabayApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import timber.log.Timber

class ImageViewModel : ViewModel() {
    private var images: MutableLiveData<List<Hit>> = MutableLiveData()
    val photo: String = "photo"
    fun getImages(): LiveData<List<Hit>> {
        return images
    }

    fun loadImages(searchQuery : String) {
        var apiService: PixabayApiInterface

        apiService = PixabayApiClient.getClient().create()

        val call = apiService.getSearched(searchQuery, photo)
        call.enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                Timber.i("Request Url: %s", call.request().url().toString());
                Timber.i("Response code: %s", response.code());

                images.value = response.body()?.hits
            }
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Timber.e(t.toString())
            }
        })
    }
}