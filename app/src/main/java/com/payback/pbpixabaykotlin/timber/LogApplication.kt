package com.payback.pbpixabaykotlin.timber

import android.app.Application
import timber.log.Timber

class LogApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}