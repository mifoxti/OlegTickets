package com.example.tickets

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesApi {
    @GET("searchJSON")
    fun searchCities(
        @Query("q") city: String,
        @Query("lang") lang: String,
        @Query("username") username: String
    ): Call<GeoNamesResponse>
}

