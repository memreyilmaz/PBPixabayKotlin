package com.android.pixabay.timber

import android.app.Application
import android.content.Context
import timber.log.Timber

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appContext = applicationContext
    }

    companion object {

        var appContext: Context? = null
            private set
    }
}