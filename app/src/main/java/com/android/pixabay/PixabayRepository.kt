package com.android.pixabay

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.android.pixabay.model.Hit
import com.android.pixabay.model.paging.ImageDataSource
import com.android.pixabay.model.paging.ImageDataSourceFactory
import com.android.pixabay.model.rest.PixabayApiService
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PixabayRepository(private val networkService: PixabayApiService) {
    private val subscriptions = CompositeDisposable()
    lateinit var imagesDataSourceFactory: ImageDataSourceFactory
    lateinit var imagesList: LiveData<PagedList<Hit>>

    fun search(query: String): LiveData<PagedList<Hit>> {
        imagesDataSourceFactory = ImageDataSourceFactory(query,subscriptions, networkService)

       val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
            .build()

       ////val factory = imageDataSourceFactory(query)
       /////val config = pagedListConfig()
        //imagesList = LivePagedListBuilder<Int,Hit>(imagesDataSourceFactory, config).build()

      //////// val imagesList = LivePagedListBuilder(factory, config)
      ////////      .build()
       imagesList = LivePagedListBuilder<Int,Hit>(ImageDataSourceFactory(query,
                subscriptions, networkService), config).build()
        //return LivePagedListBuilder<Int,Hit>(imagesDataSourceFactory, config).build()
       return imagesList
    }

    fun retry(){
        imagesDataSourceFactory.sourceLiveData.value?.retry()
    }

    fun listIsEmpty(): Boolean {
        return imagesList.value?.isEmpty() ?: true
    }

    fun getState(): LiveData<State> = Transformations.switchMap< ImageDataSource,
            State>(imagesDataSourceFactory.sourceLiveData, ImageDataSource::state)

    companion object {
        private const val PAGE_SIZE = 20
        private const val ENABLE_PLACEHOLDERS = false
    }

    fun downloadImage(imageView: ImageView, imageName: String): Completable {
        return Completable.create { observer ->
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + APP_NAME
            try {
                val file = File(path, imageName + ".jpg")
                file.parentFile.mkdir()
                var stream = FileOutputStream(file)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.close()
                observer.onComplete()
                Timber.i("file created")
            }catch (e: IOException) {
                observer.onError(e)
                Timber.e("couldnt create file")
            }
        }
    }

    private fun imageDataSourceFactory(query: String): ImageDataSourceFactory {
        return ImageDataSourceFactory(query, subscriptions, networkService)
    }

    private fun pagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
            .build()
    }
}