package com.example.skylight

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skylight.databinding.ActivityMainBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

import android.Manifest
import android.annotation.SuppressLint
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.lang.Exception


//3a92cecdb8ba1596c279d469f5ec4045
class MainActivity : AppCompatActivity() {
    //location Latitude,longitude
    private var cityLati=0.0
    private var cityLongi=0.0
    private var searchCity=""

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Missing Permission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)




        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {

                permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                        || permissions.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    false
                )
                -> {
                    Toast.makeText(this, "Location Access Granted", Toast.LENGTH_SHORT).show()

                    if (isLocationEnabled()) {

                        try {
                            val result = fusedLocationClient.getCurrentLocation(
                                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                                CancellationTokenSource().token
                            )
                            result.addOnCompleteListener { task ->
                                if (task.isSuccessful && task.result != null) {
                                    val location ="Latitude: " + task.result.latitude + "\n" + "Longitude: " + task.result.longitude
                                    //update longi and latitude
                                    cityLati=task.result.latitude
                                    cityLongi=task.result.longitude
                                    fetchCityName()

//                                    Toast.makeText(this, location, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: SecurityException) {
                            // Handle the SecurityException here, for example, by requesting permission again
                            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT)
                                .show()
                            // Optionally, request permission again or handle the denial gracefully
                        }
                    } else {

                        Toast.makeText(this, "Please turn ON the location.", Toast.LENGTH_SHORT)
                            .show()
                        createLocationRequest()
                    }

                }

                else -> {

                    Toast.makeText(this, "No Location Access", Toast.LENGTH_SHORT).show()
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

/*        //check api url
        val baseUrl = "https://api.openweathermap.org/data/2.5/"
        val apiKey = "3a92cecdb8ba1596c279d469f5ec4045"
        val apiUrl = "${baseUrl}weather?lat=$cityLati&lon=$cityLongi&appid=$apiKey"
        //Log.d("TAG", "fetchWeatherData: $apiUrl")*/


//        val city = "allahabad"
//        val city=searchCity

//        fetchWeatherData(searchCity)

        searchCity()

    }



    private fun isLocationEnabled(): Boolean {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        try {

//            return locationManager.isLocationEnabled(LocationManager.GPS_PROVIDER)
            return locationManager.isLocationEnabled
        } catch (e: Exception) {

            e.printStackTrace()
        }

        return false
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are satisfied, continue flow
        }

        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, 100)
                } catch (sendEx: Exception) {
                    // Ignore the error
                }
            }
        }
    }

    private fun searchCity() {
        val searchview = binding.search
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchCityName() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getcityName(cityLati,cityLongi ,"3a92cecdb8ba1596c279d469f5ec4045")
        Log.d("TAG", "fetchCityName: $response")

        response.enqueue(object:Callback<fetchCityName>{
            override fun onResponse(call: Call<fetchCityName>, response: Response<fetchCityName>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    searchCity=responseBody.name
                    Log.d("TAG", "onResponse: $searchCity")
                    fetchWeatherData(searchCity)

                }else{
                    Toast.makeText(applicationContext, "Not working", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<fetchCityName>, t: Throwable) {

            }

        })
    }
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "3a92cecdb8ba1596c279d469f5ec4045", "metric")

/*        //check api url
        val baseUrl = "https://api.openweathermap.org/data/2.5/"
        val city = "jaipur"
        val apiKey = "3a92cecdb8ba1596c279d469f5ec4045"
        val units = "metric"
        val apiUrl = "${baseUrl}weather?q=$city&appid=$apiKey&units=$units"
        //Log.d("TAG", "fetchWeatherData: $apiUrl")*/

        response.enqueue(object : Callback<weatherApp> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                //Log.d("TAG", "onResponse: $responseBody")
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val weather = responseBody.weather.firstOrNull()?.main ?: "Unknown"
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max

                    val timezone = responseBody.timezone

                    val humidity = responseBody.main.humidity
                    val sunrise = responseBody.sys.sunrise
                    val sunset = responseBody.sys.sunset
                    val wind = responseBody.wind.speed
                    val sealevel = responseBody.main.pressure

                    // Convert sunrise timestamp
                    val sunriseDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(sunrise.toLong()),
                        ZoneId.systemDefault()
                    )
                    val sunriseTime = sunriseDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                    // Convert sunset timestamp
                    val sunsetDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(sunset.toLong()),
                        ZoneId.systemDefault()
                    )
                    val sunsetTime = sunsetDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                    // Get the current date and time in the specified timezone
                    val currentTime = LocalDateTime.now(ZoneOffset.ofTotalSeconds(timezone.toInt()))
                    // Format the date and day
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val dayOfWeekFormatter = TextStyle.FULL
                    val locale = Locale.ENGLISH // You can change the locale if needed

                    // Format the date and day
                    val date = currentTime.format(dateFormatter)
                    val day = currentTime.dayOfWeek.getDisplayName(dayOfWeekFormatter, locale)


                    binding.city.text = "$cityName"
                    binding.temp.text = "$temperature°ᶜ"
                    binding.weather.text = weather
                    binding.minTemp.text = "Min temp: $minTemp°ᶜ"
                    binding.maxTemp.text = "Max temp: $maxTemp°ᶜ"
                    binding.day.text = "$day"
                    binding.date.text = "$date"
                    binding.humidity.text = "$humidity%"
                    binding.sunrise.text = "$sunriseTime"
                    binding.sunset.text = "$sunsetTime"
                    binding.wind.text = "$wind" + "m/s"
                    binding.condition.text = "$weather"
                    binding.sea.text = "$sealevel" + "hPa"
                    changeImageAcctoWeather(weather)
                } else {
                    //val errorCode=response.code()
                    Toast.makeText(applicationContext, "Not Available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                /*
                Log.d("TAG", "onFailure: NOT WORKING")
                Toast.makeText(applicationContext, "NOT working", Toast.LENGTH_SHORT).show()*/

            }
        })
    }



    private fun changeImageAcctoWeather(condition: String) {

        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sunny)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.animation.setAnimation(R.raw.cloud)
            }

            "Light rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.animation.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.animation.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.animation.setAnimation(R.raw.sunny)
            }
        }
        binding.animation.playAnimation()
    }
}