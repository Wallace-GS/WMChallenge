package com.ngmatt.weedmapsandroidcodechallenge.data

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.yelp.com/v3/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface YelpApiService {

    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String
    ) : Response<BusinessesResponse>

    @GET("businesses/{id}/reviews")
    suspend fun getReviews(
        @Header("Authorization") authHeader: String,
        @Path("id") reviewId: String
    ) : Response<ReviewsResponse>
}

object YelpApi {
    val retrofitService : YelpApiService by lazy { retrofit.create(YelpApiService::class.java) }
}