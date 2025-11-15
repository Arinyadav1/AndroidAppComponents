package com.example.appComponents.services


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.appComponents.MainActivity
import com.example.appComponents.R


@SuppressLint("WrongConstant")
fun createNotificationChannel(context: Context) {
    val channelId = "channel1"
    val channelName = "Example Channel"
    val channelDescription = "This is notification"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
    }

    val notificationManager =
        ContextCompat.getSystemService(context, NotificationManager::class.java)
    notificationManager!!.createNotificationChannel(channel)
    Log.d("checkChannel", "Notification Channel created with ID: $channelId")

}



fun sendNotification(context: Context, notificationId: Int): Notification {

    val intent = Intent(context, MainActivity::class.java)

    //send notification
    val builder = NotificationCompat.Builder(context, "ID")
        .setContentText("This is a sample notification $notificationId using the example channel.")
        .setContentTitle("Example Notification $notificationId")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setOngoing(true)
        .build()



//    val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.notify(notificationId, builder)

    return builder
}