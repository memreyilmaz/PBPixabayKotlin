package com.android.pixabay.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.android.pixabay.model.Hit
import com.android.pixabay.model.rest.PixabayApiService
import io.reactivex.disposables.CompositeDisposable

//    class ImageDataSourceFactory(val searchQuery : String): DataSource.Factory<Int,Hit>(){
class ImageDataSourceFactory(private val subscriptions: CompositeDisposable, private val apiService: PixabayApiService)
                    : DataSource.Factory<Int, Hit>() {

    val sourceLiveData = MutableLiveData<ImageDataSource>()

    override fun create(): DataSource<Int, Hit> {
        val source = ImageDataSource(subscriptions, apiService)
        sourceLiveData.postValue(source)
        return source
    }
}