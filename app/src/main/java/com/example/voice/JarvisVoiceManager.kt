package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class JarvisVoiceManager(
    private val context: Context,
    private val onInitComplete: () -> Unit = {}
) {
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false

    private var currentOnResult: ((String) -> Unit)? = null
    private var currentOnRmsChanged: ((Float) -> Unit)? = null
    private var currentOnError: ((String) -> Unit)? = null
    private var currentOnDone: (() -> Unit)? = null
    private var currentOnStart: (() -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ukResult = textToSpeech?.setLanguage(Locale.UK)
                if (ukResult == TextToSpeech.LANG_MISSING_DATA || ukResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.US)
                }
                // Refined British AI cadence and pitch
                textToSpeech?.setPitch(0.93f)
                textToSpeech?.setSpeechRate(1.03f)
                isTtsReady = true

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        currentOnStart?.invoke()
                    }

                    override fun onDone(utteranceId: String?) {
                        currentOnDone?.invoke()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        currentOnDone?.invoke()
                    }
                })

                onInitComplete()
            } else {
                Log.e("JarvisVoiceManager", "TTS initialization failed.")
            }
        }
    }

    fun startListening(
        onResult: (String) -> Unit,
        onRmsChanged: (Float) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device, sir.")
            return
        }

        stopListening()

        currentOnResult = onResult
        currentOnRmsChanged = onRmsChanged
        currentOnError = onError

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {
                        currentOnRmsChanged?.invoke(rmsdB)
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onError(error: Int) {
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No audible command detected, sir."
                            SpeechRecognizer.ERROR_NETWORK -> "Network uplink timeout, sir."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio hardware busy, sir."
                            SpeechRecognizer.ERROR_CLIENT -> "Speech recognizer state reset."
                            else -> "Awaiting voice input..."
                        }
                        currentOnError?.invoke(msg)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            currentOnResult?.invoke(text)
                        } else {
                            currentOnError?.invoke("Voice stream empty, sir.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { partial ->
                            if (partial.isNotBlank()) {
                                currentOnRmsChanged?.invoke(12f)
                            }
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra("android.speech.extra.PREFER_OFFLINE", true)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("JarvisVoiceManager", "Error starting speech recognizer: ${e.message}")
            onError("Voice recognition failed to initiate: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e("JarvisVoiceManager", "Error stopping recognizer: ${e.message}")
        }
    }

    fun speak(
        text: String,
        onStart: () -> Unit = {},
        onDone: () -> Unit = {}
    ) {
        if (!isTtsReady || textToSpeech == null) {
            onDone()
            return
        }

        currentOnStart = onStart
        currentOnDone = onDone

        val utteranceId = "JARVIS_${System.currentTimeMillis()}"
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    fun destroy() {
        stopListening()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
