package com.example.lab5mobile

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.lab5mobile.databinding.ActivityMainBinding
import android.app.Notification
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    // launcher для запроса разрешения
    private val requestPostNotification = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onPostNotificationGranted )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onSetupNotificationChannel()
        binding.notify.setOnClickListener {
            onSendNotification()
        }
    }
    private fun createNotificationForBob(): Notification {
        return NotificationCompat.Builder(this,
            CoolStoryBobNotification.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_cookie_24)// иконка
            .setShowWhen(true) // время
            .setOngoing(true) // может ли пользователь его сам убрать
        .setContentText("Story from bob") // текст
        .build()
    }
    private fun onSetupNotificationChannel() {
        // проверяем текущие устройство
        // если Android ниже 8, то просто возвращаемся, ибо
        // channel не нужен
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        // создание канала
        val channel = NotificationChannel(
            CoolStoryBobNotification.CHANNEL_ID,
            getString(R.string.bob_notification),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // установка его
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }


    // отправка уведомления
    private fun onSendNotification() {
        if (!isPostNotificationGranted()) {
            requestPostNotification()
        }
        val notification = createNotificationForBob()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(CoolStoryBobNotification.NOTIFICATION_ID, notification)
    }
    // запрос разрешения
    private fun requestPostNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    // проверка есть ли разрешение
    private fun isPostNotificationGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    // получение результата разрешения
    private fun onPostNotificationGranted(isGranted: Boolean) {
        if (!isGranted) {
            Toast.makeText(this,
                "Permission not granted",
                Toast.LENGTH_SHORT).show()
            return }
        onSendNotification()
    }




}