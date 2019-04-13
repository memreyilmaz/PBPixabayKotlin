package com.payback.pbpixabaykotlin.model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.payback.pbpixabaykotlin.APP_NAME
import com.payback.pbpixabaykotlin.rest.PixabayApiClient
import com.payback.pbpixabaykotlin.rest.PixabayApiInterface
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageViewModel(application: Application) : AndroidViewModel(application) {
    var downloadstatus = MutableLiveData<Boolean?>()
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

    fun saveImage(url: String, imageName: String)  {
        Picasso.with(getApplication())
            .load(url)
            .into(getTarget(imageName))
    }

    private fun getTarget(imageName: String): Target {
        return object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                Thread(Runnable {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + APP_NAME
                    try {
                        val file = File(path, imageName + ".jpg")
                        file.parentFile.mkdir()
                        var stream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        stream.close()
                        downloadstatus.postValue(true)
                        Timber.i("file created")
                    }catch (e: IOException){
                        downloadstatus.postValue(false)
                        Timber.e("couldnt create file")

                    }
                }).start()
            }
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(errorDrawable: Drawable?) {

            }
        }
    }
}