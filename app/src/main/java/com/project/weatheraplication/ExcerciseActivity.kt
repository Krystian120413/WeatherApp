package com.project.weatheraplication

import android.annotation.SuppressLint
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

        val timeCounter1 = findViewById<TextView>(R.id.timer1)
        val timerButton1 = findViewById<Button>(R.id.start1)
        val timeCounter2 = findViewById<TextView>(R.id.timer2)
        val timerButton2 = findViewById<Button>(R.id.start2)
        val timeCounter3 = findViewById<TextView>(R.id.timer3)
        val timerButton3 = findViewById<Button>(R.id.start3)
        val timeCounter4 = findViewById<TextView>(R.id.timer4)
        val timerButton4 = findViewById<Button>(R.id.start4)
        val timeCounter5 = findViewById<TextView>(R.id.timer5)
        val timerButton5 = findViewById<Button>(R.id.start5)
        val timeCounter6 = findViewById<TextView>(R.id.timer6)
        val timerButton6 = findViewById<Button>(R.id.start6)
        val timeCounter7 = findViewById<TextView>(R.id.timer7)
        val timerButton7 = findViewById<Button>(R.id.start7)

        timerButton1.setOnClickListener{
            startTimeCount(timeCounter1, timerButton1, 120000)
        }
        timerButton2.setOnClickListener{
            startTimeCount(timeCounter2, timerButton2, 120000)
        }
        timerButton3.setOnClickListener{
            startTimeCount(timeCounter3, timerButton3, 120000)
        }
        timerButton4.setOnClickListener{
            startTimeCount(timeCounter4, timerButton4, 120000)
        }
        timerButton5.setOnClickListener{
            startTimeCount(timeCounter5, timerButton5, 120000)
        }
        timerButton6.setOnClickListener{
            startTimeCount(timeCounter6, timerButton6, 120000)
        }
        timerButton7.setOnClickListener{
            startTimeCount(timeCounter7, timerButton7, 600000)
        }
    }
    var started = false
    @SuppressLint("SetTextI18n")
    fun startTimeCount(view: TextView, button: Button, time: Long){
        val countTime: TextView = view
        val timer = object : CountDownTimer(time, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val minute = (millisUntilFinished / 60000)
                val second =(millisUntilFinished - (minute * 60000)) / 1000
                val minuteTxt = "0$minute"
                var seccondTxt = second.toString()
                if(second < 10) seccondTxt = "0$second"
                countTime.text = "$minuteTxt:$seccondTxt"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                countTime.text = "02:00"
                started = false
                button.text = "START"
            }
        }
        if(!started) {
            started = true
            button.text = "STOP"
            button.setOnClickListener{
                timer.cancel()
                started = false
                button.text = "START"
                countTime.text = "02:00"
                if(time.compareTo(600000) == 0) countTime.text = "10:00"
                button.setOnClickListener{
                    startTimeCount(view, button, time)
                }
            }
            timer.start()
        }
    }
}