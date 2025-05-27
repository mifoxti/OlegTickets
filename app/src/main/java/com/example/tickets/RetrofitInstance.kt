package com.example.tickets

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://api.geonames.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GeoNamesApi by lazy {
        retrofit.create(GeoNamesApi::class.java)
    }
}
