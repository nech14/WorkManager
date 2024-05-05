package com.example.workmanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL


data class WeatherData(
    @SerializedName("sys") val sys: Sys,
    @SerializedName("name") val name: String,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("wind") val wind: Wind
)

data class Sys(
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
)

data class Weather(
    @SerializedName("description") val description: String
)

data class Wind(
    @SerializedName("speed") val speed: Double
)


class MainActivity : AppCompatActivity() {

    lateinit var button: Button
    lateinit var name1: EditText
    lateinit var name2: EditText
    lateinit var data1: TextView
    lateinit var data2: TextView

    private val API_KEY = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        button = findViewById(R.id.button)
        name1 = findViewById(R.id.editTextText2)
        name2 = findViewById(R.id.editTextText3)
        data1 = findViewById(R.id.textView3)
        data2 = findViewById(R.id.textView4)

        button.setOnClickListener{
            update(it);
        }


    }

    @SuppressLint("SetTextI18n")
    fun update(view: View){
        val city1 = name1.text.toString()
        val city2 = name2.text.toString()

        if (city1.isNotEmpty()){
            fetchWeatherData(city1){weatherData ->
                data1.text = """
                    city: $city1
                    weather: ${weatherData.weather[0].description}
                    wind: ${weatherData.wind.speed}
                """.trimIndent()
            }
        }else{
            data1.text = "You dont' enter name cities 1"
        }
        if (city2.isNotEmpty()){
            fetchWeatherData(city2){weatherData ->
                data2.text = """
                    city: $city2
                    weather: ${weatherData.weather[0].description}
                    wind: ${weatherData.wind.speed}
                """.trimIndent()
            }
        }else{
            data2.text = "You dont' enter name cities 2"
        }

    }

    fun fetchWeatherData(city:String, callback: (WeatherData) -> Unit){
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val stream = URL(weatherURL).openStream()
                val data = stream.bufferedReader().use { it.readText() }
                val weatherData = Gson().fromJson(data, WeatherData::class.java)
                launch(Dispatchers.Main) {
                    callback(weatherData)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "There is no such city!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}