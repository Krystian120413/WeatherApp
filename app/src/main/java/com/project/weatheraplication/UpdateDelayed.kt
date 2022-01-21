package com.project.weatheraplication

import java.util.concurrent.TimeUnit

class UpdateDelayed public constructor(activity : MainActivity) : Runnable{
    private val mActivity: MainActivity = activity

    inner class Handler : android.os.Handler() {
    }

    val myHandler = Handler()

    override fun run() {
        update()
        myHandler.postDelayed(UpdateDelayed(mActivity), TimeUnit.HOURS.toMillis(2))
    }

    fun update() {
        SendNotification(mActivity).createNotificationChannel()
        mActivity.WeatherTask().execute()
    }
}