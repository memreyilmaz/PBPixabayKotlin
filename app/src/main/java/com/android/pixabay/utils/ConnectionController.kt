package com.android.pixabay.utils


/*object ConnectionController {

    fun isInternetAvailable(context: Context): Boolean? {
        var isConnected: Boolean? = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}*/

/*class LiveNetworkMonitor(context: Context) : NetworkMonitor {

    private val applicationContext: Context

    override val isConnected: Boolean
        get() {
            val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

    init {
        applicationContext = context.applicationContext
    }
}*/
