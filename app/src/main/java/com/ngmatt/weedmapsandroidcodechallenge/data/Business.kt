package com.ngmatt.weedmapsandroidcodechallenge.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Business(
    val id: String,
    @Json(name = "image_url")
    val imageUrl: String,
    val name: String,
    val rating: Double,
    var reviews: MutableList<Review> = mutableListOf()
)