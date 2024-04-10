package com.example.skylight.forecast

data class forecast(
    val list: ArrayList<ListElements>
) {

    data class ListElements(
        val dt: Int,
        val dt_txt: String,
        val main: Main,
        val pop: Double,
        val sys: Sys,
        val visibility: Int,
        val weather: List<Weather>,
        val wind: Wind
    ) {
        data class Main(
            val humidity: Int,
            val temp: Double,
        )

        data class Sys(
            val pod: String
        )

        data class Weather(
            val description: String,
            val icon: String,
            val id: Int,
            val main: String
        )

        data class Wind(
            val speed: Double
        )
    }
}