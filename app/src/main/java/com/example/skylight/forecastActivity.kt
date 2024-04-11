package com.example.skylight

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.skylight.databinding.ActivityForecastBinding
import com.example.skylight.databinding.ActivityPollutionBinding
import com.example.skylight.forecast.forecast
import com.example.skylight.pollution.airPollution
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.math.round

class forecastActivity : AppCompatActivity() {
    var lati=0.0
    var longi=0.0

    private val binding: ActivityForecastBinding by lazy {
        ActivityForecastBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lati = intent.getDoubleExtra("lat", 0.0)
        longi = intent.getDoubleExtra("lon", 0.0)

        Log.d("TAG", "onCreate: $lati")
        Log.d("TAG", "onCreate: $longi")
        fetchForeCastData()
    }

    private fun fetchForeCastData() {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=retrofit.getForecastData(lati,longi ,"3a92cecdb8ba1596c279d469f5ec4045","metric")

        response.enqueue(object: Callback<forecast> {
            override fun onResponse(call: Call<forecast>, response: Response<forecast>) {
                val responseBody=response.body()
                //Log.d("TAG", "Response: $response")//url

                if(response.isSuccessful && responseBody!=null){

                    for (i in 1..8){
                        val date= responseBody.list[i].dt_txt
                        val desc= responseBody.list[i].weather.firstOrNull()?.description
                        val temp=responseBody.list[i].main.temp
                        val humidity=responseBody.list[i].main.humidity
                        val windSpeed=responseBody.list[i].wind.speed
                        val icon=responseBody.list[i].weather.firstOrNull()?.icon

                        val dateViewId = resources.getIdentifier("date$i", "id", packageName)
                        findViewById<TextView>(dateViewId).text=date

                        val descriptionid = resources.getIdentifier("description$i", "id", packageName)
                        findViewById<TextView>(descriptionid).text=desc

                        val temperatureid = resources.getIdentifier("temp$i", "id", packageName)
                        findViewById<TextView>(temperatureid).text=temp.toString()+"°ᶜ"

                        val humidityid = resources.getIdentifier("humidity$i", "id", packageName)
                        findViewById<TextView>(humidityid).text="Humidity: "+humidity.toString()+"%"

                        val windspeedid = resources.getIdentifier("windspeed$i", "id", packageName)
                        findViewById<TextView>(windspeedid).text="Wind Speed: "+windSpeed.toString()+"m/s"


                        val iconId = resources.getIdentifier("icon$i", "id", packageName)
                        val imageView = findViewById<ImageView>(iconId)
                        Glide.with(this@forecastActivity)
                            .load("https://openweathermap.org/img/wn/$icon@2x.png")
                            .into(imageView)

                    }

                }else{
                    Log.d("TAG", "onResponse: From else")
                    Toast.makeText(applicationContext, "Not working", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<forecast>, t: Throwable) {
                Log.e("TAG", "Failed to fetch forecast data: ${t.message}")
                Toast.makeText(applicationContext, "Failed to fetch forecast data", Toast.LENGTH_SHORT).show()
            }

        })

    }
}