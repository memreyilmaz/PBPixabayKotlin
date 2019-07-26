package com.android.pixabay.model.repository

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.android.pixabay.utils.Listing
import com.android.pixabay.model.Hit
import com.android.pixabay.model.paging.ImageDataSourceFactory
import com.android.pixabay.utils.APP_NAME
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PixabayRepository @Inject
constructor (private val networkService: PixabayApiService) {
    private val subscriptions = CompositeDisposable()
    lateinit var imagesDataSourceFactory: ImageDataSourceFactory
    lateinit var imagesList: LiveData<PagedList<Hit>>

    fun search(query: String): Listing<Hit> {
        imagesDataSourceFactory = ImageDataSourceFactory(query,subscriptions, networkService)

       val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
            .build()

       imagesList = LivePagedListBuilder<Int, Hit>(ImageDataSourceFactory(query,
                subscriptions, networkService), config).build()

        return Listing(
            pagedList = imagesList,
            networkState = switchMap(imagesDataSourceFactory.sourceLiveData) { it.network },
            retry = { imagesDataSourceFactory.sourceLiveData.value?.retry() })
    }

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
}