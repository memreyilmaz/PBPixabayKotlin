package com.android.pixabay.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.pixabay.di.util.ViewModelKey
import com.android.pixabay.viewmodel.SharedViewModel
import com.android.pixabay.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    protected abstract fun sharedViewModel(sharedViewModel: SharedViewModel): ViewModel
}