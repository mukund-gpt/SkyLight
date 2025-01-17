package com.example.skylight

import com.example.skylight.forecast.forecast
import com.example.skylight.pollution.airPollution
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

    @GET("air_pollution")
    fun getPollutionData(
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("appid") appid:String,
    ):Call<airPollution>

    @GET("forecast")
    fun getForecastData (
        @Query("lat") lat:Double,
        @Query("lon") lon:Double,
        @Query("appid") appid:String,
        @Query("units") units:String
    ):Call<forecast>


}