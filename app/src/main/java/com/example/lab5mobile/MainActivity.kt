package com.example.lab5mobile

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.lab5mobile.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val collStoryBobConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val coolStoryBob = (service as CoolStoryBobService.LocalBinder).getBob()
            onProcessBobStories(coolStoryBob)
        }
        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startService.setOnClickListener {
            val intent = Intent(this, CoolStoryBobService::class.java)
            startService(intent)
            bindService(intent, collStoryBobConnection,
                Context.BIND_AUTO_CREATE) }
    }

    private fun onProcessBobStories(coolStoryBob: CoolStoryBob) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                coolStoryBob.stories.collect {
                    story -> Log.d("MainActivity", "onProcessBobStories: $story") }
            }
        }
    }
}