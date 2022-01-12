package com.project.weatheraplication

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.reflect.Array
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherForecastActivity : AppCompatActivity() {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val api: String = "79aaa968a0069d0b93d757ed27fea018"
    lateinit var weatherApi: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_forecast)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)
        weatherApi = "https://api.openweathermap.org/data/2.5/onecall?lat=$latitude&lon=$longitude&appid=$api&units=metric"
        WeatherTask().execute()
        println("$latitude, $longitude asd")

    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            val response: String? = try {
                URL(weatherApi).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
            return response
        }

        @SuppressLint("SimpleDateFormat")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONArray("daily")

                val main1 = main.getJSONObject(1)
                val main2 = main.getJSONObject(2)
                val main3 = main.getJSONObject(3)
                val main4 = main.getJSONObject(4)

                val temp1 = main1.getJSONObject("temp")
                val temp2 = main2.getJSONObject("temp")
                val temp3 = main3.getJSONObject("temp")
                val temp4 = main4.getJSONObject("temp")

                val date1 = SimpleDateFormat("EEE dd/MM").format(Date(main1.getLong("dt")*1000))
                val date2 = SimpleDateFormat("EEE dd/MM").format(Date(main2.getLong("dt")*1000))
                val date3 = SimpleDateFormat("EEE dd/MM").format(Date(main3.getLong("dt")*1000))
                val date4 = SimpleDateFormat("EEE dd/MM").format(Date(main4.getLong("dt")*1000))

                val desc1 = main1.getJSONArray("weather").getJSONObject(0).getString("description")
                val desc2 = main2.getJSONArray("weather").getJSONObject(0).getString("description")
                val desc3 = main3.getJSONArray("weather").getJSONObject(0).getString("description")
                val desc4 = main4.getJSONArray("weather").getJSONObject(0).getString("description")

                val tempday1 = temp1.getInt("day").toString() + "°C"
                val wind1 = (main1.getDouble("wind_speed")*3.6).toInt().toString() + " km/h"
                val humidity1 = main1.getString("humidity") + "%"
                val tempday2 = temp2.getInt("day").toString() + "°C"
                val wind2 = (main2.getDouble("wind_speed")*3.6).toInt().toString() + " km/h"
                val humidity2 = main2.getString("humidity") + "%"
                val tempday3 = temp3.getInt("day").toString() + "°C"
                val wind3 = (main3.getDouble("wind_speed")*3.6).toInt().toString() + " km/h"
                val humidity3 = main3.getString("humidity") + "%"
                val tempday4 = temp4.getInt("day").toString() + "°C"
                val wind4 = (main4.getDouble("wind_speed")*3.6).toInt().toString() + " km/h"
                val humidity4 = main4.getString("humidity") + "%"


                findViewById<TextView>(R.id.text1).text = date1
                findViewById<TextView>(R.id.temp1).text = tempday1
                findViewById<TextView>(R.id.desc1).text = desc1
                findViewById<TextView>(R.id.wind1).text = wind1
                findViewById<TextView>(R.id.humidity1).text = humidity1
                findViewById<TextView>(R.id.text2).text = date2
                findViewById<TextView>(R.id.temp2).text = tempday2
                findViewById<TextView>(R.id.desc2).text = desc2
                findViewById<TextView>(R.id.wind2).text = wind2
                findViewById<TextView>(R.id.humidity2).text = humidity2
                findViewById<TextView>(R.id.text3).text = date3
                findViewById<TextView>(R.id.temp3).text = tempday3
                findViewById<TextView>(R.id.desc3).text = desc3
                findViewById<TextView>(R.id.wind3).text = wind3
                findViewById<TextView>(R.id.humidity3).text = humidity3
                findViewById<TextView>(R.id.text4).text = date4
                findViewById<TextView>(R.id.temp4).text = tempday4
                findViewById<TextView>(R.id.desc4).text = desc4
                findViewById<TextView>(R.id.wind4).text = wind4
                findViewById<TextView>(R.id.humidity4).text = humidity4

            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
                    .show()
                finish()
                startActivity(intent)
            }
        }
    }
}