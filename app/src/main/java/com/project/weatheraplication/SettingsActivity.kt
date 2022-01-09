package com.project.weatheraplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.*

class SettingsActivity : AppCompatActivity(), LocationDialog.LocationDialogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val address = intent.getStringExtra("Address")
        writeLocation()
        val cities = readSavedLocations()
        appendLayouts(cities)
        //findViewById<TextView>(R.id.currentLocation).text = address
        //findViewById<FloatingActionButton>(R.id.addLocation).setOnClickListener{addLocationButton()}
        //findViewById<Button>(R.id.removeLocation1).setOnClickListener{removeLocationButton()}
    }

    private fun addLocationButton() {
        openDialog()
    }

    private fun removeLocationButton() {
        findViewById<LinearLayout>(R.id.locationLayout1).removeAllViews()
        findViewById<ScrollView>(R.id.scrollView2).removeView(findViewById<LinearLayout>(R.id.locationLayout1))
    }

    private fun openDialog() {
        val locationDialog = LocationDialog()
        locationDialog.show(supportFragmentManager, "example dialog")
    }

    @SuppressLint("ResourceType")
    override fun applyText(city: String) {
        findViewById<TextView>(R.id.savedLocation1).text = city
    }

    private fun appendLayouts(cities: List<String>) {
        val mainll = findViewById<LinearLayout>(R.id.main_llayout)
        for ((n, i) in cities.withIndex()) {
            val new_ll = LinearLayout(this)
            new_ll.id = n
            new_ll.orientation = LinearLayout.VERTICAL
            new_ll.setBackgroundColor(getColor(R.color.green))

            val new_llh = LinearLayout(this)
            new_llh.orientation = LinearLayout.HORIZONTAL

            val changeButton = Button(this)
            val removeButton = Button(this)

            changeButton.text = "Change"
            removeButton.text = "Remove"

            new_llh.addView(changeButton)
            new_llh.addView(removeButton)

            val new_tw = TextView(this)
            new_tw.text = i
            new_tw.setTextColor(getColor(R.color.black))
            new_tw.textSize = 20F

            new_ll.addView(new_tw)
            new_ll.addView(new_llh)

            mainll.addView(new_ll)
        }
    }

    private fun writeLocation() {
        val fOut: FileOutputStream = openFileOutput(
            "savedLocations.txt",
            MODE_PRIVATE
        )
        val osw = OutputStreamWriter(fOut)
        osw.write("Rzeszów\nKraków\nBerlin")
        osw.flush()
        osw.close()
        fOut.close()
    }

    @SuppressLint("SetTextI18n")
    private fun readSavedLocations(): List<String> {
        return try {
            val fIn: FileInputStream = openFileInput("savedLocations.txt")
            val isr = InputStreamReader(fIn)

            val cities = isr.readText().split("\n")
            isr.close()
            fIn.close()

            cities
        } catch (e: FileNotFoundException) {
            emptyList()
        }
    }
}