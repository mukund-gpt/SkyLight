package com.example.skylight

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ):Call<weatherApp>


    @GET("weather")
    fun getcityName(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("appid") appid:String,
    ):Call<fetchCityName>
}