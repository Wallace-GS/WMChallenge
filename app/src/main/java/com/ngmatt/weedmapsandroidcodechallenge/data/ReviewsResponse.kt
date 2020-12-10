package com.ngmatt.weedmapsandroidcodechallenge.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewsResponse(
    val reviews: List<Review>
)

@JsonClass(generateAdapter = true)
data class Review(
    val rating: Int,
    val text: String,
)