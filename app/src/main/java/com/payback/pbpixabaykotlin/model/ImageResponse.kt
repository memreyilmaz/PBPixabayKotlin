package com.payback.pbpixabaykotlin.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageResponse (
        @SerializedName("totalHits")
        val totalHits: Int,
        @SerializedName("hits")
        val hits: List<Hit>? = null,
        @SerializedName("total")
        val total: Int
) : Parcelable
