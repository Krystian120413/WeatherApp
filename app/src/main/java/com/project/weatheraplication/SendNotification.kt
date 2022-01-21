package com.project.weatheraplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SendNotification(private val context: Context, val temperature: Int) {
    private var CHANNEL_ID = "10"
    private var CHANNEL_ID_INT = CHANNEL_ID.toInt()

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "WeatherWorkout"
            val descriptionText = "Weather App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(context, MainActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        var message = "It's workout time!"
        if(temperature >= 15) message = "It's time to do some workout! You can go outside."
        else if(temperature in 1..14) message = "Let's do some workout! Wear kalesony and czapka."
        else message = "It's cold outside. Stay at home and do some workout!"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("It's $temperature°C outside.")
            .setAutoCancel(true)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply {
                setContentIntent(resultPendingIntent)
            }
        with(NotificationManagerCompat.from(context)){
            notify(CHANNEL_ID_INT, builder.build())
        }

        NotificationManagerCompat.from(context).notify(CHANNEL_ID.toInt(), builder.build())
    }
}