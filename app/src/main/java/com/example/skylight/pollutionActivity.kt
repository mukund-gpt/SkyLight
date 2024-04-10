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
                    Log.d("TAG", "onResponse: $aqi")

                }else{
                    Log.d("TAG", "onResponse: From else")
                    Toast.makeText(applicationContext, "Not working", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<airPollution>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }


}