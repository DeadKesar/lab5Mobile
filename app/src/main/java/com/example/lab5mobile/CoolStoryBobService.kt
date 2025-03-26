package com.example.lab5mobile

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CoolStoryBobService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.d("CoolStoryBobService", "onCreate: ")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
    // имитация работы в потоке
        Thread {
            Thread.sleep(3000L)
            Log.d("CoolStoryBobService","onStartCommand: complete!")
        stopSelf()
        }.start()
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null // Не поддерживаем привязку
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CoolStoryBobService", "onDestroy: ")
    }
}
