package com.android.pixabay

import android.content.Context
import android.net.ConnectivityManager

object ConnectionController {

    fun isInternetAvailable(context: Context): Boolean? {
        var isConnected: Boolean? = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}