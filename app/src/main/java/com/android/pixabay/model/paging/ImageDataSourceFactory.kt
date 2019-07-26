package com.android.pixabay.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.android.pixabay.model.Hit
import com.android.pixabay.model.repository.PixabayApiService
import io.reactivex.disposables.CompositeDisposable

class ImageDataSourceFactory(val searchQuery: String, private val subscriptions: CompositeDisposable, private val apiService: PixabayApiService)
                    : DataSource.Factory<Int, Hit>() {

    val sourceLiveData = MutableLiveData<ImageDataSource>()

    override fun create(): DataSource<Int, Hit> {
        val source = ImageDataSource(searchQuery, subscriptions, apiService)
        sourceLiveData.postValue(source)
        return source
    }
}