package com.android.pixabay.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.pixabay.model.Hit
import com.android.pixabay.model.repository.PixabayApiService
import com.android.pixabay.utils.NetworkState
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class ImageDataSource(val searchQuery: String, private val subscriptions: CompositeDisposable, private val apiService: PixabayApiService)
                : PageKeyedDataSource<Int, Hit>() {

    private var retryCompletable: Completable? = null
    val network = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Hit>) {
        network.postValue(NetworkState.LOADING)
        subscriptions.add(
            apiService.getSearch(searchQuery,1)
                .subscribe(
                    { response ->
                        //if (response.totalHits == 0){
                          //  network.postValue(NetworkState.error("no result"))
                        //}else{
                            network.postValue(NetworkState.DONE)
                            callback.onResult(response.hits, null, 2)
                       // }
                    },
                    {
                        network.postValue(NetworkState.error(it.localizedMessage))
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {
        network.postValue(NetworkState.LOADING)
        subscriptions.add(
            apiService.getSearch(searchQuery,params.key+1)
                .subscribe(
                    { response ->
                        network.postValue(NetworkState.DONE)
                        callback.onResult(response.hits, params.key + 1)
                    },
                    {
                        network.postValue(NetworkState.error(it.localizedMessage))
                        setRetry(Action { loadAfter(params, callback) })
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {}

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    fun retry() {
        if (retryCompletable != null) {
            subscriptions.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
        }
    }
}