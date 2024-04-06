package com.example.skylight

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skylight.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//3a92cecdb8ba1596c279d469f5ec4045
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData()

    }

    private fun fetchWeatherData() {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData("jaipur","3a92cecdb8ba1596c279d469f5ec4045","metric")

        //check api url
        val baseUrl = "https://api.openweathermap.org/data/2.5/"
        val city = "jaipur"
        val apiKey = "3a92cecdb8ba1596c279d469f5ec4045"
        val units = "metric"
        val apiUrl = "${baseUrl}weather?q=$city&appid=$apiKey&units=$units"
        //Log.d("TAG", "fetchWeatherData: $apiUrl")
        //

        response.enqueue(object  : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody=response.body()
                //Log.d("TAG", "onResponse: $responseBody")
                if(response.isSuccessful && responseBody!=null ){
                    val temperature= responseBody.main.temp.toString()
                    val weather=responseBody.weather.firstOrNull()?.main?:"Unknown"
                    val minTemp=responseBody.main.temp_min
                    val maxTemp=responseBody.main.temp_max
                    val humidity=responseBody.main.humidity
                    val sunrise=responseBody.sys.sunrise
                    val sunset=responseBody.sys.sunset
                    val wind=responseBody.wind.speed
                    val sealevel=responseBody.main.pressure



                    binding.temp.text="$temperature°ᶜ"
                    binding.weather.text=weather
                    binding.minTemp.text="Min temp: $minTemp°ᶜ"
                    binding.maxTemp.text="Max temp: $maxTemp°ᶜ"
                    binding.humidity.text="$humidity%"
                    binding.sunrise.text="$sunrise"
                    binding.sunset.text="$sunset"
                    binding.wind.text="$wind"+"m/s"
                    binding.condition.text="$weather"
                    binding.sea.text="$sealevel" + "hPa"
                }
                else{
                    //val errorCode=response.code()
                   }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                Log.d("TAG", "onFailure: NOT WORKING")
                Toast.makeText(applicationContext, "NOT working", Toast.LENGTH_SHORT).show()

            }

        } )
    }
}