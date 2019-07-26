package com.android.pixabay.di.module

import com.android.pixabay.view.ui.DetailFragment
import com.android.pixabay.view.ui.ListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): DetailFragment
}