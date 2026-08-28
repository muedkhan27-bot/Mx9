package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.service.JarvisVoiceService
import com.example.ui.JarvisMainScreen
import com.example.ui.theme.JarvisBackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.JarvisViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = JarvisBackgroundDark
                ) {
                    JarvisMainScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val isWakeTrigger = intent?.getBooleanExtra(JarvisVoiceService.EXTRA_WAKE_TRIGGER, false) == true
        val wakeCommand = intent?.getStringExtra("WAKE_COMMAND")

        if (isWakeTrigger || intent?.action == Intent.ACTION_VOICE_COMMAND) {
            viewModel.onWakeUpTriggered(wakeCommand)
        }
    }
}
