package com.project.weatheraplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.widget.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.loader.content.AsyncTaskLoader
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.io.*
import java.net.HttpURLConnection


class MainActivity : AppCompatActivity() {


    private var latitude: Double = 22.00
    private var longitude: Double = 57.00
    val api: String = "79aaa968a0069d0b93d757ed27fea018" // Use your own API key
    val aqiToken = "4201969e380e8f0422ceb9ef1c3b5bb500d8ffa3"
    var aqi: String = ""

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        if(getPermissions()){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getLocation()
        }
        else {
            readLastLocation()
        }

        findViewById<ImageButton>(R.id.excerciseBtn).setOnClickListener{
            val intentEx = Intent(this, ExcerciseActivity::class.java)
            startActivity(intentEx)
        }

        findViewById<Button>(R.id.checkWeatherBtn).setOnClickListener{
            val intentWF = Intent(this, WeatherForecastActivity::class.java)
            startActivity(intentWF)
        }

        findViewById<ImageButton>(R.id.settingBtn).setOnClickListener{
            val intentSetting = Intent(this, SettingsActivity::class.java)
            startActivity(intentSetting)
            //SendNotification(this).createNotificationChannel()
        }
    }

    private fun getPermissions():Boolean {
        if ((ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                44
            )
            return false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun readLastLocation() {
        try {
            val fIn: FileInputStream = openFileInput("location.txt")
            val isr = InputStreamReader(fIn)

            val read = isr.readText().split("\n")
            latitude = (read[0].toDouble())
            longitude = (read[1].toDouble())
            isr.close()
            fIn.close()

            WeatherTask().execute()
        } catch (e: FileNotFoundException) {
            findViewById<TextView>(R.id.address).text = "File missing"
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnCompleteListener{
                    val location = it.result
                    if (location != null) {
                        val geo = Geocoder(this)
                        val address = geo.getFromLocation(location.latitude, location.longitude, 1)
                        latitude = address[0].latitude
                        longitude = address[0].longitude

                        val fOut: FileOutputStream = openFileOutput(
                            "location.txt",
                            MODE_PRIVATE
                        )
                        val osw = OutputStreamWriter(fOut)
                        osw.write("$latitude\n$longitude")
                        osw.flush()
                        osw.close()
                        fOut.close()

                        WeatherTask().execute()
                    }
                }
        }
        else {
            readLastLocation()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val response:String? = try {
                URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$api&units=metric").readText(Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
            aqi = URL("https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$aqiToken").readText(Charsets.UTF_8)
            return response
        }

        @SuppressLint("SimpleDateFormat")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
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

                val timezone = jsonObj.getLong("timezone")
                val sunrise: Long = (sys.getLong("sunrise")+timezone)*1000
                val sunset: Long = (sys.getLong("sunset")+timezone)*1000
                val windSpeed = (wind.getDouble("speed")*3.6).toInt().toString() + " km/h"
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                val simple: SimpleDateFormat = SimpleDateFormat("kk:mm")
                simple.timeZone = TimeZone.getTimeZone("UTC")
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = simple.format(Date(sunrise))
                findViewById<TextView>(R.id.sunset).text = simple.format(Date(sunset))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

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

    fun isInternetWorking(): Boolean {
        var success = false
        try {
            val url = URL("https://google.com")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.connect()
            success = connection.responseCode == 200
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return success
    }
}
