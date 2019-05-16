package com.android.pixabay.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.pixabay.PixabayRepository

class ViewModelFactory(private val repository: PixabayRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}