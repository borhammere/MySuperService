package com.example.mysuperservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mysuperservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val service = (binder as ToastService.ToastServiceBinder).service
            service.setCallback { success: Boolean ->
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Успешный успех", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("@@@", "onServiceConnected() called with: p0 = $p0")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("@@@", "onServiceDisconnected() called with: p0 = $p0")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = ToastService.getLaunchIntent(this, "Привет!")
        binding.startToastServiceButton.setOnClickListener {
            startService(intent)
        }

        binding.stopToastServiceButton.setOnClickListener {
            stopService(intent)
        }

        binding.bindToastServiceButton.setOnClickListener {
            bindService(intent, connection, BIND_AUTO_CREATE)
        }

        binding.unbindToastServiceButton.setOnClickListener {
            unbindService(connection)
        }

        binding.foregroundToastServiceButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            }
        }

        binding.toastIntentServiceButton.setOnClickListener {
            ToastIntentService.startToastJob(this, "Я intent service")
        }
    }
}