package com.project.weatheraplication

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Gravity.*
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import java.io.*
import android.widget.RelativeLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat.setBackgroundTintList

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

            val newLl = LinearLayout(this)

            newLl.id = n
            newLl.orientation = LinearLayout.VERTICAL
            newLl.setBackgroundColor(Color.parseColor("#c4c4c4"))

            val newLlh = LinearLayout(this)
            newLlh.orientation = LinearLayout.HORIZONTAL

            val changeButton = Button(this)
            val removeButton = Button(this)

            removeButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff6363"))

            changeButton.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
                    .toInt())

            removeButton.setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
                    .toInt())

            changeButton.text = "Change"
            removeButton.text = "Remove"

            changeButton.setTextColor(Color.BLACK)
            removeButton.setTextColor(Color.BLACK)

            newLlh.addView(changeButton)

            val viewDivider = View(this)
            val dividerWidth = resources.displayMetrics.density * 10
            viewDivider.layoutParams = RelativeLayout.LayoutParams(dividerWidth.toInt(), WRAP_CONTENT)

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
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                    .toInt()
            )
            newTw.layoutParams = params

            // Linear Layout params

            params = newLl.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
                .toInt()
            )

            params.width = MATCH_PARENT
            params.height = MATCH_PARENT

            newLl.layoutParams = params
            newLl.gravity = CENTER

            params = newLlh.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
                    .toInt()
            )
            newLlh.layoutParams = params

            // Button params

            params = changeButton.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
                    .toInt()
            )
            changeButton.layoutParams = params
            removeButton.layoutParams = params

            changeButton.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1F)
            removeButton.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1F)

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