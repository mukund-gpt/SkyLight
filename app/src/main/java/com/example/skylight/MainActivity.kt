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
        response.enqueue(object  : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful ){
                    val temperature= responseBody?.main?.temp.toString()
//                    binding.temp.text= "$temperature"
                    Log.d("TAG", "onResponse: $temperature")
                    Toast.makeText(applicationContext, "from if", Toast.LENGTH_SHORT).show()
                }
                else{
                    val errorCode=response.code()
                    Log.d("TAG", "onResponse: helloworld")
                    Log.d("TAG", "onResponse: Error code: $errorCode")
                    Toast.makeText(applicationContext, "from else", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                Log.d("TAG", "onFailure: NOT WORKING")
                Toast.makeText(applicationContext, "NOT working", Toast.LENGTH_SHORT).show()

            }

        } )
    }
}