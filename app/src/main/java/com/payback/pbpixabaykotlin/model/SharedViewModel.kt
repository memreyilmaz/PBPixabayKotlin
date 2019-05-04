package com.payback.pbpixabaykotlin.model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.payback.pbpixabaykotlin.APP_NAME
import com.payback.pbpixabaykotlin.rest.PixabayApiClient
import com.payback.pbpixabaykotlin.rest.PixabayApiInterface
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class SharedViewModel(application: Application) : AndroidViewModel(application) {
        var downloadstatus = MutableLiveData<Boolean?>()
        var images: MutableLiveData<List<Hit>> = MutableLiveData()
        //var selectedImage: MutableLiveData<Hit> = MutableLiveData()
        //lateinit var selectedImage: Hit

    val selectedImage = MutableLiveData<Hit>()
    fun setSelectedImage(hit: Hit) {
        //selectedImage.value = hit
        selectedImage.value = hit
    }

    private val subscriptions = CompositeDisposable()

    private val photo: String = "photo"

    fun loadImages(searchQuery : String) {
        val apiService: PixabayApiInterface = PixabayApiClient.getClient()

        subscriptions.add(apiService.getSearched(searchQuery,photo).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ImageResponse>() {
                override fun onSuccess(t: ImageResponse) {
                    images.value = t.hits
                }

                override fun onError(e: Throwable) {
                    Timber.e(e.toString())
                }
            }))


        /*val call = apiService.getSearched(searchQuery, photo)
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
        })*/
    }

    fun getImages(): LiveData<List<Hit>> {
        return images
    }

    fun downloadImage(imageView: ImageView, imageName: String): Single<String> {
        return Single.create { observer ->
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + APP_NAME
            try {
             val file = File(path, imageName + ".jpg")
             file.parentFile.mkdir()
             var stream = FileOutputStream(file)
             val bitmap = (imageView.drawable as BitmapDrawable).bitmap
             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
             stream.close()
             observer.onSuccess("down")
            }catch (e: IOException) {
             observer.onError(e)
            }
        }
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