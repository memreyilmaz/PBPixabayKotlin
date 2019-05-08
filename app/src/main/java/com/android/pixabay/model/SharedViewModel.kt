package com.android.pixabay.model

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.android.pixabay.APP_NAME
import com.android.pixabay.State
import com.android.pixabay.model.paging.ImageDataSource
import com.android.pixabay.model.paging.ImageDataSourceFactory
import com.android.pixabay.model.rest.PixabayApiService
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SharedViewModel : ViewModel(){

    private val networkService = PixabayApiService.getService()
    var imagesList: LiveData<PagedList<Hit>>
    val selectedImage = MutableLiveData<Hit>()
    private val subscriptions = CompositeDisposable()
    private val pageSize = 20
    private val imagesDataSourceFactory: ImageDataSourceFactory

    init {
        imagesDataSourceFactory = ImageDataSourceFactory(subscriptions, networkService)
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize)
            .setEnablePlaceholders(false)
            .build()
        imagesList = LivePagedListBuilder<Int, Hit>(imagesDataSourceFactory, config).build()
    }
    fun setSelectedImage(hit: Hit) {
        selectedImage.value = hit
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
                Timber.i("file created")
            }catch (e: IOException) {
                observer.onError(e)
                Timber.e("couldnt create file")
            }
        }
    }

    fun getState(): LiveData<State> = Transformations.switchMap<ImageDataSource,
            State>(imagesDataSourceFactory.sourceLiveData, ImageDataSource::state)

    fun retry() {
        imagesDataSourceFactory.sourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return imagesList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.dispose()
    }


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