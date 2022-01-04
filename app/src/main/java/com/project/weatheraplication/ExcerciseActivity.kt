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
            startTimeCount(timeCounter1, timerButton1)
        }
        timerButton2.setOnClickListener{
            startTimeCount(timeCounter2, timerButton2)
        }
        timerButton3.setOnClickListener{
            startTimeCount(timeCounter3, timerButton3)
        }
        timerButton4.setOnClickListener{
            startTimeCount(timeCounter4, timerButton4)
        }
        timerButton5.setOnClickListener{
            startTimeCount(timeCounter5, timerButton5)
        }
        timerButton6.setOnClickListener{
            startTimeCount(timeCounter6, timerButton6)
        }
        timerButton7.setOnClickListener{
            startTimeCount(timeCounter7, timerButton7)
        }
    }
    var started = false
    fun startTimeCount(view: TextView, button: Button){
        if(!started) {
            started = true
            button.text = "STOP"
            button.id = "stop".toInt()
            val countTime: TextView = view
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
                    button.text = "START"
                }
            }.start()
        }
    }
}