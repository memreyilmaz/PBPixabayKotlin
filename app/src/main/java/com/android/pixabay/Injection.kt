package com.android.pixabay

import androidx.lifecycle.ViewModelProvider
import com.android.pixabay.model.ViewModelFactory
import com.android.pixabay.model.rest.PixabayApiService

object Injection {

    private fun providePixabayRepository() : PixabayRepository {
        return PixabayRepository(PixabayApiService.getService())
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(providePixabayRepository())
    }
}
