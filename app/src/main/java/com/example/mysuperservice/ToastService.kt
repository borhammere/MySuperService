package com.example.mysuperservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val TAG = "@@@"

class ToastService : Service() {
    companion object {
        private const val TEXT_EXTRA_KEY = "TEXT_EXTRA_KEY"

        fun getLaunchIntent(context: Context, message: String): Intent {
            val serviceIntent = Intent(context, ToastService::class.java)
            serviceIntent.putExtra(TEXT_EXTRA_KEY, message)
            return serviceIntent
        }
    }

    private var callback: (Boolean) -> Unit = {}
    private val binder = ToastServiceBinder()

    override fun onCreate() {
        super.onCreate()
        val notificationManager = NotificationManagerCompat.from(this)
        val channelId = "channelId"

        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_HIGH).build()
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("OLOLO")
            .setContentText("LALALA")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Оказывается очень важно
            .build()
        startForeground(42, notification)
        Log.d(TAG, "onCreate() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(
            TAG,
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        val message = intent.extras?.getString(TEXT_EXTRA_KEY) ?: "EMPTY"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Thread {
            var timeToSleep = 5_000 - startId * 1_000L
            if (timeToSleep <= 0) {
                timeToSleep = 100
            }
            Thread.sleep(timeToSleep)
            Log.d(
                TAG,
                "stopSelf() startId = $startId"
            )
            callback(true)
            stopSelf(startId)
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind() called with: intent = $intent")
        return super.onUnbind(intent)
    }

    fun setCallback(function: (Boolean) -> Unit) {
        callback = function
    }

    inner class ToastServiceBinder : Binder() {
        val service = this@ToastService
    }
}