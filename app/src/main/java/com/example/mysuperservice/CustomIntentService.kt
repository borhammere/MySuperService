package com.example.mysuperservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder

abstract class CustomIntentService(threadName: String) : Service() {
    private val workerThread: HandlerThread = HandlerThread(threadName)

    override fun onBind(intent: Intent?): IBinder? = null

    abstract fun onHandleIntent(intent: Intent?)

    override fun onCreate() {
        super.onCreate()
        workerThread.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Handler(workerThread.looper).postAtFrontOfQueue {
            onHandleIntent(intent)
        }
        Handler(workerThread.looper).post {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }
}