package com.payback.pbpixabaykotlin

import android.content.Context
import android.net.ConnectivityManager

object ConnectionController {

    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetwork = cm?.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }
}