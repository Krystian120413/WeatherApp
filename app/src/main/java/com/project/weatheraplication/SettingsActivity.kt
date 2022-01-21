package com.project.weatheraplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*

private var cities: List<String> = listOf<String>()

class SettingsActivity : AppCompatActivity(), LocationDialog.LocationDialogListener,
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val address = intent.getStringExtra("Address")
        val longitude = intent.getDoubleExtra("Longitude", 0.0)
        val latitude = intent.getDoubleExtra("Latitude", 0.0)

        cities = readSavedLocations()

        appendLayouts(cities)

        findViewById<TextView>(R.id.currentLocation).text = address

        findViewById<FloatingActionButton>(R.id.resetLocation).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.addLocation).setOnClickListener {
            addLocationButton()
        }
        findViewById<Button>(R.id.saveLocation).setOnClickListener {
            val lon = longitude.toString()
            val lat = latitude.toString()

            cities += listOf("$address, $lon, $lat")

            writeLocation()
            finish()
            startActivity(intent)
        }
    }

    private fun addLocationButton() {
        openDialog()
    }

    private fun openDialog() {
        val locationDialog = LocationDialog()
        locationDialog.show(supportFragmentManager, "example dialog")
    }

    @SuppressLint("ResourceType")
    override fun applyText(city: String) {
        if (city.isNotBlank() && city.isNotEmpty()) {
            getCity(city, OkHttpClient())
            Thread.sleep(170)
        }

        finish()
        startActivity(intent)
    }

    private fun appendLayouts(cities: List<String>) {
        val mainll = findViewById<LinearLayout>(R.id.main_llayout)
        for ((n, i) in cities.withIndex()) {
            if (i.isNotEmpty() && i.isNotBlank()) {
                val newLl = LinearLayout(this)

                newLl.id = n
                newLl.tag = "layout$n"
                newLl.orientation = LinearLayout.VERTICAL
                newLl.setBackgroundColor(Color.parseColor("#c4c4c4"))

                val newLlh = LinearLayout(this)
                newLlh.orientation = LinearLayout.HORIZONTAL

                val changeButton = Button(this)
                changeButton.tag = "changeButton$n"
                val removeButton = Button(this)
                removeButton.tag = "removeButton$n"

                removeButton.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#ff6363"))

                changeButton.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )
                        .toInt()
                )

                removeButton.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )
                        .toInt()
                )

                changeButton.text = "Change"
                removeButton.text = "Remove"

                changeButton.setTextColor(Color.BLACK)
                removeButton.setTextColor(Color.BLACK)

                newLlh.addView(changeButton)

                val viewDivider = View(this)
                val dividerWidth = resources.displayMetrics.density * 10
                viewDivider.layoutParams =
                    RelativeLayout.LayoutParams(dividerWidth.toInt(), WRAP_CONTENT)

                newLlh.addView(viewDivider)
                newLlh.addView(removeButton)

                val newTw = TextView(this)
                newTw.text = i
                newTw.setTextColor(getColor(R.color.black))
                newTw.textSize = 20F
                newTw.textAlignment = TEXT_ALIGNMENT_CENTER

                newLl.addView(newTw)
                newLl.addView(newLlh)

                mainll.addView(newLl)

                newLlh.layoutParams.width = MATCH_PARENT
                newLlh.layoutParams.height = MATCH_PARENT
                newLlh.gravity = CENTER

                // Text View params

                var params = newTw.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5f,
                        resources.displayMetrics
                    )
                        .toInt()
                )
                newTw.layoutParams = params

                // Linear Layout params

                params = newLl.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )
                        .toInt()
                )

                params.width = MATCH_PARENT
                params.height = MATCH_PARENT

                newLl.layoutParams = params
                newLl.gravity = CENTER

                params = newLlh.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )
                        .toInt()
                )
                newLlh.layoutParams = params

                // Button params

                params = changeButton.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        6f,
                        resources.displayMetrics
                    )
                        .toInt()
                )
                changeButton.layoutParams = params
                removeButton.layoutParams = params

                changeButton.layoutParams =
                    LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1F)
                removeButton.layoutParams =
                    LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1F)

                removeButton.setOnClickListener(this)
                changeButton.setOnClickListener(this)
            }
        }
    }

    private fun writeLocation() {
        val fOut = File("/data/data/com.project.weatheraplication/files/savedLocations.txt")
        val fos = FileOutputStream(fOut)
        val writer = BufferedWriter(OutputStreamWriter(fos))

        for (i in cities) {
            if (i.isNotEmpty() && i.isNotBlank()) {
                writer.appendLine(i)
                writer.flush()
            }
        }

        writer.close()
    }

    @SuppressLint("SetTextI18n")
    private fun readSavedLocations(): List<String> {
        return try {
            val fIn: FileInputStream = openFileInput("savedLocations.txt")
            val isr = InputStreamReader(fIn)

            val cities = isr.readText().split("\n")
            fIn.close()
            isr.close()

            if (cities[0].isNullOrBlank()) emptyList()
            else cities
        } catch (e: FileNotFoundException) {
            val fOut: FileOutputStream = openFileOutput(
                "savedLocations.txt",
                MODE_PRIVATE
            )
            fOut.close()
            emptyList()
        }
    }

    fun message(e: String) {
        this.runOnUiThread {
            Toast.makeText(applicationContext, e, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCity(city: String, client: OkHttpClient) {
        val zip = city.replace(Regex("[, ]+"), ",").split(',')
        val api = "79aaa968a0069d0b93d757ed27fea018"
        val url = if (city.contains(','))
            "https://api.openweathermap.org/data/2.5/weather?zip=${zip[0]},${zip[1]}&appid=$api&units=metric"
        else
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$api&units=metric"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                message("HTTP Request Error")
            }

            override fun onResponse(call: Call, response: Response) {
                val jobj = JSONObject(response.body()!!.string())
                try {
                    val info = jobj.getString("name") + ", " +
                            jobj.getJSONObject("sys").getString("country") + ", " +
                            jobj.getJSONObject("coord").getDouble("lon").toString() + ", " +
                            jobj.getJSONObject("coord").getDouble("lat").toString()
                    cities += info
                    writeLocation()
                } catch (e: JSONException) {
                    message("Couldn't find the city")
                }

            }
        })
    }

    override fun onClick(v: View?) {
        if (v?.tag.toString().startsWith('r')) {
            val view = v?.parent?.parent as ViewGroup
            view.removeView(view)
            view.removeAllViews()
            deleteLocation(v.tag.toString().last())
        }
        if (v?.tag.toString().startsWith('c')) {
            changeLocation(v?.tag.toString().last())
        }
    }

    private fun changeLocation(n: Char) {
        try {
            val fIn =
                BufferedReader(FileReader("/data/data/com.project.weatheraplication/files/savedLocations.txt"))
            var longitude = 0.0
            var latitude = 0.0

            var nlines = 0
            var currentline = fIn.readLine()


            while (currentline != null) {
                if (nlines == n.digitToInt()) {
                    longitude =
                        currentline.replace(Regex("[, a-żA-Ż]+"), " ").split(" ")[1].toDouble()
                    latitude =
                        currentline.replace(Regex("[, a-żA-Ż]+"), " ").split(" ")[2].toDouble()

                    break
                }
                nlines++
                currentline = fIn.readLine()
            }
            fIn.close()

            val intentMain = Intent(this, MainActivity::class.java)
            intentMain.putExtra("Longitude", longitude)
            intentMain.putExtra("Latitude", latitude)
            finish()
            startActivity(intentMain)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun deleteLocation(n: Char) {
        try {
            val fOut = File("/data/data/com.project.weatheraplication/files/temp.txt")
            val fOutWriter = BufferedWriter(FileWriter(fOut))

            val fIn = File("/data/data/com.project.weatheraplication/files/savedLocations.txt")
            val fInReader = BufferedReader(FileReader(fIn))

            var nlines = 0
            var currentline = fInReader.readLine()

            println(n)
            println(currentline)

            while (currentline != "" && currentline != null) {
                if (nlines != n.digitToInt()) {
                    fOutWriter.write("$currentline\n")
                    fOutWriter.flush()
                }
                nlines++
                currentline = fInReader.readLine()
            }
            fInReader.close()
            fOutWriter.close()

            fOut.renameTo(fIn)
            fOut.delete()

            cities = readSavedLocations()

            finish()
            startActivity(intent)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}