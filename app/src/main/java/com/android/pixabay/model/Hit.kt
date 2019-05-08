package com.android.pixabay.model

import com.google.gson.annotations.SerializedName

data class Hit (
    @SerializedName("largeImageURL")
    val largeImageURL: String,
    @SerializedName("likes")
    val likes: Int,
    val id: Int,
    @SerializedName("comments")
    val comments: Int,
    @SerializedName("pageURL")
    val pageURL: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("previewURL")
    val previewURL: String
)