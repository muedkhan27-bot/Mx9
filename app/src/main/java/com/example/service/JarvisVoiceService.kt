package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Locale

class JarvisVoiceService : Service() {

    companion object {
        const val CHANNEL_ID = "jarvis_voice_service_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_JARVIS_SERVICE"
        const val ACTION_STOP = "ACTION_STOP_JARVIS_SERVICE"
        const val EXTRA_WAKE_TRIGGER = "EXTRA_WAKE_TRIGGER"
        var isServiceRunning = false
            private set
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isListening = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        isServiceRunning = true
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        startContinuousWakeListening()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "J.A.R.V.I.S. Core Listener",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors for 'Jarvis wake up' and Stark system actions"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingOpen = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, JarvisVoiceService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("J.A.R.V.I.S. Core Online")
            .setContentText("Listening for 'Jarvis wake up' | All systems optimal")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingOpen)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disengage", pendingStop)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startContinuousWakeListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return

        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            isListening = true
                        }
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            isListening = false
                        }
                        override fun onError(error: Int) {
                            isListening = false
                            restartListeningWithDelay(1500)
                        }

                        override fun onResults(results: Bundle?) {
                            isListening = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()?.lowercase(Locale.ROOT) ?: ""
                            Log.d("JarvisVoiceService", "Background heard: $text")

                            if (text.contains("wake up") || text.contains("jarvis") || text.contains("hey jarvis")) {
                                onWakeWordDetected(text)
                            } else {
                                restartListeningWithDelay(1000)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()?.lowercase(Locale.ROOT) ?: ""
                            if (text.contains("wake up") || text.contains("hey jarvis") || (text.contains("jarvis") && text.contains("up"))) {
                                onWakeWordDetected(text)
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                    putExtra("android.speech.extra.PREFER_OFFLINE", true)
                }
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                Log.e("JarvisVoiceService", "Error in background recognizer: ${e.message}")
                restartListeningWithDelay(3000)
            }
        }
    }

    private fun restartListeningWithDelay(delayMs: Long) {
        if (!isServiceRunning) return
        mainHandler.postDelayed({
            if (isServiceRunning && !isListening) {
                startContinuousWakeListening()
            }
        }, delayMs)
    }

    private fun onWakeWordDetected(commandText: String) {
        Log.i("JarvisVoiceService", "WAKE WORD TRIGGERED: $commandText")
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            putExtra(EXTRA_WAKE_TRIGGER, true)
            putExtra("WAKE_COMMAND", commandText)
        }
        startActivity(launchIntent)
        restartListeningWithDelay(4000)
    }

    private fun stopForegroundService() {
        isServiceRunning = false
        speechRecognizer?.destroy()
        speechRecognizer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        isServiceRunning = false
        speechRecognizer?.destroy()
        speechRecognizer = null
        super.onDestroy()
    }
}
