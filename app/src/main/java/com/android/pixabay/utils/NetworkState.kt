package com.android.pixabay.utils

enum class Status {
    DONE, LOADING, ERROR
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null) {
    companion object {
        val DONE = NetworkState(Status.DONE)
        val LOADING = NetworkState(Status.LOADING)
        fun error(msg: String?) = NetworkState(Status.ERROR, msg)
    }
}