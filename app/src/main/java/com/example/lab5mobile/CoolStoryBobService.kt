package com.example.lab5mobile

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

private const val MY_CUSTOM_ACTION = "my_custom_action"
private const val NOTIFICATION_REQUEST_CODE = 1

// Интерфейс, чтобы ограничить возможности из вне
interface CoolStoryBob {
    // будет поток историй
    val stories: SharedFlow<String>
}

class CoolStoryBobService : Service(), CoolStoryBob {
    // Создание Binder
    // inner - значит имеет ссылку на родителя, где определен
    // т.е. на CoolStoryBobService
    inner class LocalBinder : Binder() {
        fun getBob(): CoolStoryBob = this@CoolStoryBobService
    }

    private val binder = LocalBinder()
    private val _stories = MutableSharedFlow<String>(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val stories: SharedFlow<String> = _stories.asSharedFlow()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getStringExtra(MY_CUSTOM_ACTION) != null) {
            // реакция на MY_CUSTOM_ACTION
            Log.d("CoolStoryBobService", "onStartCommand: ${intent.extras}")
            return START_STICKY
        }
        // установка Foreground service
        val notification = createNotification()
        startForeground(1, notification)

        // имитация работы в потоке
        Thread {
            repeat(10) { data ->
                // каждую секунду отправляем историю
                _stories.tryEmit("Story $data")
                Thread.sleep(1000L)
            }
            Log.d("CoolStoryBobService", "onStartCommand: complete!")
            stopSelf()
        }.start()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        // изначальный интент
            val intent = Intent(applicationContext, CoolStoryBobService::class.java).apply {
                putExtra(MY_CUSTOM_ACTION, "somedata")
            }
        // делаем PendingIntent
            val pendingIntent = PendingIntent.getService( applicationContext,
                NOTIFICATION_REQUEST_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        return NotificationCompat.Builder(this, CoolStoryBobNotification.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_cookie_24)
            .setShowWhen(true)
            .setOngoing(true)
            .setContentTitle("CoolStoryBobService started")
            .setContentText("Story from bob")
            .addAction(R.drawable.ic_play_arrow, "Play", pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}
