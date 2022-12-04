package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("https://api.nasa.gov/")
    .build()

enum class ApiKey(val key: String) { KEY("OWwINA3RO1hEMmaT4x3oleoLPx6T4FOrHTjxNXOr") }

interface ApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getProperties(
        @Query("api_key") apiKey: String
    ): String

    @GET("planetary/apod")
    suspend fun getImage( @Query("api_key") type: String): PictureOfDay

}

object AsteroidApi {

    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
