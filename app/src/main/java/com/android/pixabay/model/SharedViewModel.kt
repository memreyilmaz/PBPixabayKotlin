package com.android.pixabay.model

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.android.pixabay.PixabayRepository
import com.android.pixabay.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class SharedViewModel(private val repository: PixabayRepository): ViewModel(){

    val selectedImage = MutableLiveData<Hit>()
    val downloadResult = MutableLiveData<String>()
    private val queryLiveData = MutableLiveData<String>()

    val imageslist = Transformations.switchMap(queryLiveData){
        repository.search(it)
    }

    fun setQuery(query: String) {
        queryLiveData.postValue(query)
    }
    fun lastQueryValue(): String? = queryLiveData.value

    fun setSelectedImage(hit: Hit) {
        selectedImage.value = hit
    }

    fun downloadImage(imageView: ImageView, imageName: String) {
        repository.downloadImage(imageView,imageName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onComplete = {
                    downloadResult.value = "File Saved"
                },
                onError = { e ->
                    downloadResult.value = "Error saving file :${e.localizedMessage}"
                }
            )
    }

    fun getState(): LiveData<State> {
        return repository.getState()
    }
    fun retry() {
        repository.retry()
    }
    fun listIsEmpty(): Boolean {
        return repository.listIsEmpty()
    }
}