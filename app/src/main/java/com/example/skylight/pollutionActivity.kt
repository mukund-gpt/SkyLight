package com.example.skylight

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skylight.databinding.ActivityPollutionBinding
import com.example.skylight.pollution.airPollution
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class pollutionActivity : AppCompatActivity() {
    var lati=0.0
    var longi=0.0

    private val binding:ActivityPollutionBinding by lazy {
        ActivityPollutionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        lati = intent.getDoubleExtra("lat", 0.0).toDouble()
        longi = intent.getDoubleExtra("lon", 0.0).toDouble()


        Log.d("TAG", "onCreate: $lati")
        Log.d("TAG", "onCreate: $longi")

        fetchPollutionData()

    }

    private fun fetchPollutionData() {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getPollutionData(lati,longi ,"3a92cecdb8ba1596c279d469f5ec4045")
        //Log.d("TAG", "AP: $response")

        response.enqueue(object:Callback<airPollution>{
            override fun onResponse(call: Call<airPollution>, response: Response<airPollution>) {
                val responseBody=response.body()
                //Log.d("TAG", "Response: $response")//url

                if(response.isSuccessful && responseBody!=null){
                    val aqi= responseBody.list.firstOrNull()?.main?.aqi
                    val co=responseBody.list.firstOrNull()?.components?.co
                    val no=responseBody.list.firstOrNull()?.components?.no
                    val no2=responseBody.list.firstOrNull()?.components?.no2
                    val o3=responseBody.list.firstOrNull()?.components?.o3
                    val so2=responseBody.list.firstOrNull()?.components?.so2
                    val pm2_5=responseBody.list.firstOrNull()?.components?.pm2_5
                    val pm10=responseBody.list.firstOrNull()?.components?.pm10
                    val nh3=responseBody.list.firstOrNull()?.components?.nh3


                    binding.aqi.text="$aqi"
                    binding.co.text="$co"+"µg/m3"
                    binding.no.text="$no"+"µg/m3"
                    binding.no2.text="$no2"+"µg/m3"
                    binding.o3.text="$o3"+"µg/m3"
                    binding.so2.text="$so2"+"µg/m3"
                    binding.pm25.text="$pm2_5"+"µg/m3"
                    binding.pm10.text="$pm10"+"µg/m3"
                    binding.nh3.text="$nh3"+"µg/m3"


                }else{
                    Log.d("TAG", "onResponse: From else")
                    Toast.makeText(applicationContext, "Not working", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<airPollution>, t: Throwable) {
                Log.e("TAG", "Failed to fetch pollution data: ${t.message}")
                Toast.makeText(applicationContext, "Failed to fetch pollution data", Toast.LENGTH_SHORT).show()
            }

        })
    }


}