package com.project.weatheraplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit
import com.project.weatheraplication.LocationDialog as dialog
import android.net.ConnectivityManager

class MainActivity : AppCompatActivity(),
    com.project.weatheraplication.LocationDialog.LocationDialogListener {

    private var latitude: Double = 22.00
    private var longitude: Double = 57.00
    private val api: String = "79aaa968a0069d0b93d757ed27fea018"
    private val aqiToken = "4201969e380e8f0422ceb9ef1c3b5bb500d8ffa3"
    var aqi: String = ""
    var city: String = ""
    var weatherApi: String = ""
    var aqiApi: String = ""
    var changed = false
    var mMediaPlayer: MediaPlayer? = null
    var temp = 0

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.getDoubleExtra("Longitude", -9999.0) != -9999.0) {
            changed = true
            longitude = intent.getDoubleExtra("Longitude", 0.0)
            latitude = intent.getDoubleExtra("Latitude", 0.0)
            weatherApi = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$api&units=metric"
            aqiApi = "https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$aqiToken"
            WeatherTask().execute()
        }
        else {
            changed = false
            if (getPermissions() && isInternetAvailable()) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                getLocation()
            } else if (isInternetAvailable()) {
                openDialog()
            } else {
                Toast.makeText(this, "Connect to internet and try again!", Toast.LENGTH_LONG).show()
                this.finishAffinity()
            }
        }

        findViewById<ImageButton>(R.id.excerciseBtn).setOnClickListener{
            val intentEx = Intent(this, ExcerciseActivity::class.java)
            startActivity(intentEx)
        }

        findViewById<Button>(R.id.checkWeatherBtn).setOnClickListener{
            val intentWF = Intent(this, WeatherForecastActivity::class.java)
            intentWF.putExtra("latitude", latitude)
            intentWF.putExtra("longitude", longitude)
            startActivity(intentWF)
        }

        findViewById<ImageButton>(R.id.settingBtn).setOnClickListener{
            stopMusic()
            if (changed) finish()
            val intentSetting = Intent(this, SettingsActivity::class.java)
            intentSetting.putExtra("Address", findViewById<TextView>(R.id.address).text)
            intentSetting.putExtra("Longitude", longitude)
            intentSetting.putExtra("Latitude", latitude)
            startActivity(intentSetting)
        }

        UpdateDelayed(this).myHandler.postDelayed(UpdateDelayed(this), 1000)
    }

    override fun onStop() {
        stopMusic()
        UpdateDelayed(this).myHandler.postDelayed(UpdateDelayed(this), TimeUnit.HOURS.toMillis(2))
        super.onStop()
    }

    override fun onDestroy() {
        stopMusic()
        super.onDestroy()
    }

    private fun openDialog() {
        val locationDialog = dialog()
        locationDialog.show(supportFragmentManager, "example dialog")
    }

    override fun applyText(city: String) {
        this.city = city
        getLocation()
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
    private fun readLastLocation(): Boolean {
        return try {
            val fIn: FileInputStream = openFileInput("location.txt")
            val isr = InputStreamReader(fIn)

            val read = isr.readText().split("\n")
            latitude = (read[0].toDouble())
            longitude = (read[1].toDouble())
            isr.close()
            fIn.close()

            WeatherTask().execute()
            true
        } catch (e: FileNotFoundException) {
            false
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            try {
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

                        weatherApi = "https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$api&units=metric"
                        aqiApi = "https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$aqiToken"

                        WeatherTask().execute()
                    }
                }
            } catch (e: UninitializedPropertyAccessException) {
                if(!city.contains(',')) {
                    weatherApi = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$api&units=metric"
                    aqiApi = "https://api.waqi.info/feed/$city/?token=$aqiToken"
                } else {
                    val zip = city.replace(Regex("[, ]+"), ",").split(',')
                    weatherApi = "https://api.openweathermap.org/data/2.5/weather?zip=${zip[0]},${zip[1]}&appid=$api&units=metric"

                    val client = OkHttpClient()
                    getCity(weatherApi, client)

                    do {
                        aqiApi = "https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$aqiToken"
                        Thread.sleep(150)
                    } while (city.contains(','))
                }

                WeatherTask().execute()
            }
        }
    }

    private fun getCity(url : String, client : OkHttpClient) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val jobj = JSONObject(response.body()!!.string())
                longitude = jobj.getJSONObject("coord").getDouble("lon")
                latitude = jobj.getJSONObject("coord").getDouble("lat")
                aqiApi = "https://api.waqi.info/feed/geo:$latitude;$longitude/?token=$aqiToken"
                city = jobj.getString("name")
            }
        })
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
            val response: String? = try {
                URL(weatherApi).readText(Charsets.UTF_8)
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
                    val jsonObj = JSONObject(result)
                    var aqiLevel = "0"
                    if (aqi != "") {
                        aqiLevel = JSONObject(aqi).getJSONObject("data").getString("aqi")
                    }
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt: Long = jsonObj.getLong("dt")
                    val updatedAtText =
                        "Updated at: " + SimpleDateFormat("dd/MM/yyyy kk:mm").format(Date(updatedAt * 1000))
                    temp = main.getInt("temp")
                    val tempS = "$temp°C"
                    val tempMin = "Min Temp: " + main.getInt("temp_min") + "°C"
                    val tempMax = "Max Temp: " + main.getInt("temp_max") + "°C"
                    val pressure = main.getString("pressure") + " hPa"
                    val humidity = main.getString("humidity") + "%"

                    val timezone = jsonObj.getLong("timezone")
                    val sunrise: Long = (sys.getLong("sunrise") + timezone) * 1000
                    val sunset: Long = (sys.getLong("sunset") + timezone) * 1000
                    val windSpeed = (wind.getDouble("speed") * 3.6).toInt().toString() + " km/h"
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name") + ", " + sys.getString("country")

                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.updated_at).text = updatedAtText
                    findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                    val simple: SimpleDateFormat = SimpleDateFormat("kk:mm")
                    simple.timeZone = TimeZone.getTimeZone("UTC")
                    findViewById<TextView>(R.id.temp).text = tempS
                    findViewById<TextView>(R.id.temp_min).text = tempMin
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.sunrise).text = simple.format(Date(sunrise))
                    findViewById<TextView>(R.id.sunset).text = simple.format(Date(sunset))
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.pressure).text = pressure
                    findViewById<TextView>(R.id.humidity).text = humidity

                    playMusic(weatherDescription)

                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                    airQuality(aqiLevel.toInt())
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    startActivity(intent)
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

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }


    fun playMusic(weather : String){
        if(weather == "clear sky"){
            mMediaPlayer = MediaPlayer.create(this, R.raw.sunny_day)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        }
        else if (weather.contains("rain") || weather.contains("drizzle")){
            mMediaPlayer = MediaPlayer.create(this, R.raw.rainy_day)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        }
        else if (weather.contains("snow")){
            mMediaPlayer = MediaPlayer.create(this, R.raw.snow)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        }
        else if (weather.contains("wind")){
            mMediaPlayer = MediaPlayer.create(this, R.raw.wind)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        }
        else if (weather.contains("thunderstorm")){
            mMediaPlayer = MediaPlayer.create(this, R.raw.thunderstorm)
            mMediaPlayer!!.isLooping = false
            mMediaPlayer!!.start()
        }
    }

    private fun stopMusic(){
        if(mMediaPlayer != null){
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}