package com.android.pixabay.model.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.android.pixabay.State
import com.android.pixabay.model.Hit
import com.android.pixabay.model.rest.PixabayApiService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

//class ImageDataSource(val searchQuery: String): PageKeyedDataSource<Int,Hit>(){
class ImageDataSource(private val subscriptions: CompositeDisposable, private val apiService: PixabayApiService)
                : PageKeyedDataSource<Int, Hit>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Hit>) {
       /* val call = apiService.getSearchedbyPaging("fruits", 1, "photo")

        call.enqueue(object : Callback<ImageResponse>{
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Timber.e(t.toString())
            }

            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                callback.onResult(response.body()?.hits!!,null,2)
                /*if(response.raw().cacheResponse() != null){
                    Timber.i("Response come from cache")
                }

                if(response.raw().networkResponse() != null){
                    Timber.i("Response come from network")
                }*/
            }

        })*/
        updateState(State.LOADING)
        subscriptions.add(
            apiService.getSearchedbyPaging("fruits",1, "photo")
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(
                            response.hits,
                            null,
                            2
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {
        /*val call = apiService.getSearchedbyPaging("fruits", 1, "photo")

        call.enqueue(object : Callback<ImageResponse>{
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Timber.e(t.toString())
            }

            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                callback.onResult(response.body()?.hits!!,params.key+1)
            }

        })*/

        updateState(State.LOADING)
        subscriptions.add(
            apiService.getSearchedbyPaging("fruits", params.key+1, "photo")
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(response.hits,
                            params.key + 1
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadAfter(params, callback) })
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Hit>) {
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

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