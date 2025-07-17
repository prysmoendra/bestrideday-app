package edu.dakode.bestbikeday

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Data models for Open-Meteo API

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_sum: List<Double>,
    val weathercode: List<Int>,
    val wind_speed_10m_max: List<Double>
)

data class WeatherResponse(
    val daily: DailyWeather
)

// Retrofit API interface
interface OpenMeteoApi {
    @GET("v1/forecast")
    fun get7DayForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode,wind_speed_10m_max",
        @Query("timezone") timezone: String = "Asia/Jakarta"
    ): Call<WeatherResponse>
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(OpenMeteoApi::class.java)

        // Bandung coordinates
        val latitude = -6.9175
        val longitude = 107.6191

        api.get7DayForecast(latitude, longitude).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()?.daily
                    if (weather != null) {
                        val days = mutableListOf<WeatherDay>()
                        for (i in weather.time.indices) {
                            val maxTemp = weather.temperature_2m_max[i]
                            val minTemp = weather.temperature_2m_min[i]
                            val rain = weather.precipitation_sum[i]
                            val wind = weather.wind_speed_10m_max[i]

                            // Scoring algorithm
                            val tempScore = when {
                                maxTemp in 22.0..28.0 -> 1.0 // ideal
                                maxTemp in 18.0..31.0 -> 0.7 // acceptable
                                else -> 0.3 // too cold or too hot
                            }
                            val rainScore = when {
                                rain == 0.0 -> 1.0 // no rain
                                rain < 2.0 -> 0.7 // light rain
                                else -> 0.2 // heavy rain
                            }
                            val windScore = when {
                                wind < 15.0 -> 1.0 // calm
                                wind < 25.0 -> 0.7 // breezy
                                else -> 0.3 // too windy
                            }
                            val score = (tempScore * 0.4 + rainScore * 0.4 + windScore * 0.2) * 100

                            days.add(
                                WeatherDay(
                                    date = weather.time[i],
                                    maxTemp = maxTemp,
                                    minTemp = minTemp,
                                    precipitation = rain,
                                    wind = wind,
                                    score = score.toInt()
                                )
                            )
                        }
                        recyclerView.adapter = WeatherAdapter(days)
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // Optionally show an error message
            }
        })
    }
}