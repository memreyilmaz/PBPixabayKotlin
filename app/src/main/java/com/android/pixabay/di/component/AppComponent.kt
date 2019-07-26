package com.android.pixabay.di.component

import android.app.Application
import com.android.pixabay.di.BaseApplication
import com.android.pixabay.di.module.FragmentModule
import com.android.pixabay.di.module.ViewModelModule
import com.android.pixabay.di.module.ActivityModule
import com.android.pixabay.di.module.ApiModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    ActivityModule::class, ApiModule::class, FragmentModule::class, ViewModelModule::class])
interface AppComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}