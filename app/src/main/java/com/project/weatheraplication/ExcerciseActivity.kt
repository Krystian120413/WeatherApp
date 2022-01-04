package com.project.weatheraplication

import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView

class ExcerciseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        val timeCounter = findViewById<TextView>(R.id.timer1)
        val timerButton = findViewById<Button>(R.id.start1)

        timerButton.setOnClickListener{
            startTimeCount(timeCounter)
        }
    }
    var started = false
    fun startTimeCount(view: TextView){
        if(!started) {
            started = true
            val countTime: TextView = findViewById(R.id.timer1)
            object : CountDownTimer(120000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minute = (millisUntilFinished / 60000)
                    val second =(millisUntilFinished - (minute * 60000)) / 1000
                    val minuteTxt = "0" + minute.toString()
                    var seccondTxt = second.toString()
                    if(second < 10) seccondTxt = "0" + second.toString()
                    countTime.text = minuteTxt + ":" + seccondTxt
                }

                override fun onFinish() {
                    countTime.text = "02:00"
                    started = false
                }
            }.start()
        }
    }
}