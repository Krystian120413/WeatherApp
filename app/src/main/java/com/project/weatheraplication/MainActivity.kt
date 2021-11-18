package com.project.weatheraplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.GradientDrawable
import android.widget.*
import android.content.Intent


class MainActivity : AppCompatActivity() {

    val city: String = "rzeszow,pl"
    val api: String = "79aaa968a0069d0b93d757ed27fea018" // Use your own API key
    val aqiApi = "https://api.waqi.info/feed/geo:50.033611;22.004722/?token=4201969e380e8f0422ceb9ef1c3b5bb500d8ffa3"
    var aqi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageButton>(R.id.excerciseBtn).setOnClickListener{
            val intentEx = Intent(this, ExcerciseActivity::class.java)
            startActivity(intentEx)
        }

        findViewById<Button>(R.id.checkWeatherBtn).setOnClickListener{
            val intentWF = Intent(this, WeatherForecastActivity::class.java)
            startActivity(intentWF)
        }

        WeatherTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val response:String? = try {
                URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api").readText(Charsets.UTF_8)

            } catch (e: Exception) {
                null
            }
            aqi = URL(aqiApi).readText(Charsets.UTF_8)
            return response
        }

        @SuppressLint("SimpleDateFormat")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                var aqiLevel = "0"
                if (aqi != "") {
                    aqiLevel = JSONObject(aqi).getJSONObject("data").getString("aqi")
                }
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy kk:mm").format(Date(updatedAt*1000))
                val temp = main.getInt("temp").toString()+"°C"
                val tempMin = "Min Temp: " + main.getInt("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getInt("temp_max")+"°C"
                val pressure = main.getString("pressure") + " hPa"
                val humidity = main.getString("humidity") + "%"

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = (wind.getDouble("speed")*3.6).toInt().toString() + " km/h"
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("kk:mm").format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("kk:mm").format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                airQuality(aqiLevel.toInt())

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        private fun airQuality(level: Int) {
            val aqi = findViewById<TextView>(R.id.aqi)
            aqi.text = level.toString()
            when (level) {
                in 0..50 -> aqi.setBackgroundColor(resources.getColor(R.color.green))
                in 51..100 -> aqi.setBackgroundColor(resources.getColor(R.color.yellow))
                in 101..150 -> aqi.setBackgroundColor(resources.getColor(R.color.orange))
                in 151..200 -> aqi.setBackgroundColor(resources.getColor(R.color.red))
                in 201..300 -> aqi.setBackgroundColor(resources.getColor(R.color.bad))
                else -> aqi.setBackgroundColor(resources.getColor(R.color.black))
            }
        }
    }
}
