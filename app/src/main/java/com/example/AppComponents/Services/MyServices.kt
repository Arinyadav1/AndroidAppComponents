package com.example.AppComponents.Services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.AppComponents.MainActivity
import com.example.AppComponents.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java


class MyServices : Service() {

    private var counter = 0
    private lateinit var player: MediaPlayer
    var job: Job? = null
    var loop = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        player = MediaPlayer.create(this, R.raw.video)
//        player.isLooping = true
//        player.start()
        loop = true
        val context = this
        job = CoroutineScope(Dispatchers.Default).launch {
            while (loop) {
                delay(500)
                counter++
                val intent = Intent("event").apply {
                    putExtra("counter", counter)
                }
                sendBroadcast(intent)

                createNotificationChannel(context)
                val notification = sendNotification(context, counter)
                startForeground(1, notification)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        loop = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}