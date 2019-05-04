package com.payback.pbpixabaykotlin.model

import com.google.gson.annotations.SerializedName

data class ImageResponse (
        @SerializedName("totalHits")
        val totalHits: Int,
        @SerializedName("hits")
        val hits: List<Hit>? = null,
        @SerializedName("total")
        val total: Int
)