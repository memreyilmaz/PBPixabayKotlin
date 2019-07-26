package com.android.pixabay.viewmodel

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.android.pixabay.model.Hit
import com.android.pixabay.model.repository.PixabayRepository
import com.android.pixabay.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SharedViewModel @Inject
constructor(private val repository: PixabayRepository): ViewModel(){

    val selectedImage = MutableLiveData<Hit>()
    private val searchQuery = MutableLiveData<String>()
    var downloadResult: SingleLiveEvent<String> = SingleLiveEvent()


    private val searchResult = map(searchQuery) {
        repository.search(it)
    }
    val imageslist = switchMap(searchResult) { it.pagedList }!!
    val networkState = switchMap(searchResult) { it.networkState }!!

    fun showSearchResults(query: String): Boolean {
        if (searchQuery.value == query) {
            return false
        }
        searchQuery.value = query
        return true
    }

    fun lastSearchQueryValue(): String? = searchQuery.value

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

    fun retry() {
        val listing = searchResult?.value
        listing?.retry?.invoke()
    }
}