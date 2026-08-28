package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DeviceController
import com.example.engine.JarvisIntelligenceEngine
import com.example.model.InstalledAppInfo
import com.example.model.JarvisActionType
import com.example.model.JarvisLogEntry
import com.example.model.LogStatus
import com.example.model.ProtocolType
import com.example.model.Speaker
import com.example.model.SystemMetrics
import com.example.service.JarvisVoiceService
import com.example.voice.JarvisVoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JarvisUiState(
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val isProcessing: Boolean = false,
    val speechInput: String = "",
    val latestResponse: String = "J.A.R.V.I.S. Core online, sir. Systems nominal. Say 'Jarvis wake up' or tap the Arc Reactor.",
    val systemMetrics: SystemMetrics = SystemMetrics(),
    val currentProtocol: ProtocolType = ProtocolType.STANDARD,
    val commandLogs: List<JarvisLogEntry> = emptyList(),
    val rmsLevel: Float = 0f,
    val isBackgroundWakeActive: Boolean = false,
    val selectedTab: Int = 0,
    val installedApps: List<InstalledAppInfo> = emptyList(),
    val isArcReactorPulsing: Boolean = true,
    val statusBanner: String = "ONLINE • READY FOR INPUT"
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceController = DeviceController(application)
    private val intelligenceEngine = JarvisIntelligenceEngine(application, deviceController)
    private val voiceManager = JarvisVoiceManager(application) {
        // Welcome greeting on init
        speakInitialGreeting()
    }

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    init {
        refreshMetrics()
        loadInstalledApps()
        startMetricsPolling()
    }

    private fun speakInitialGreeting() {
        val welcome = "At your service, sir. Stark AI subroutines initialized."
        addLog(Speaker.JARVIS, welcome, JarvisActionType.AI_QUERY)
        voiceManager.speak(
            text = welcome,
            onStart = { _uiState.update { it.copy(isSpeaking = true) } },
            onDone = { _uiState.update { it.copy(isSpeaking = false) } }
        )
    }

    private fun startMetricsPolling() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                val metrics = deviceController.getSystemMetrics()
                val isServiceActive = JarvisVoiceService.isServiceRunning
                _uiState.update {
                    it.copy(
                        systemMetrics = metrics,
                        isBackgroundWakeActive = isServiceActive
                    )
                }
            }
        }
    }

    fun refreshMetrics() {
        viewModelScope.launch(Dispatchers.IO) {
            val metrics = deviceController.getSystemMetrics()
            _uiState.update { it.copy(systemMetrics = metrics) }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = deviceController.getInstalledApps()
            _uiState.update { it.copy(installedApps = apps) }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun startListening() {
        if (_uiState.value.isSpeaking) {
            voiceManager.stopSpeaking()
        }

        _uiState.update {
            it.copy(
                isListening = true,
                speechInput = "",
                statusBanner = "LISTENING FOR STARK COMMAND..."
            )
        }

        voiceManager.startListening(
            onResult = { resultText ->
                _uiState.update { it.copy(isListening = false, speechInput = resultText) }
                processCommand(resultText)
            },
            onRmsChanged = { rms ->
                _uiState.update { it.copy(rmsLevel = (rms / 10f).coerceIn(0f, 1f)) }
            },
            onError = { errorMsg ->
                _uiState.update {
                    it.copy(
                        isListening = false,
                        statusBanner = "STANDBY • $errorMsg"
                    )
                }
            }
        )
    }

    fun stopListening() {
        voiceManager.stopListening()
        _uiState.update { it.copy(isListening = false, statusBanner = "STANDBY") }
    }

    fun toggleListening() {
        if (_uiState.value.isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun submitTextCommand(text: String) {
        if (text.isBlank()) return
        _uiState.update { it.copy(speechInput = text) }
        processCommand(text)
    }

    fun processCommand(commandText: String) {
        val trimmed = commandText.trim()
        if (trimmed.isBlank()) return

        addLog(Speaker.USER, trimmed, JarvisActionType.AI_QUERY)

        _uiState.update {
            it.copy(
                isProcessing = true,
                statusBanner = "PROCESSING QUANTUM ARRAYS..."
            )
        }

        viewModelScope.launch {
            val result = intelligenceEngine.processQuery(trimmed)

            result.protocolChanged?.let { newProtocol ->
                _uiState.update { it.copy(currentProtocol = newProtocol) }
                if (newProtocol == ProtocolType.CLEAN_SLATE) {
                    clearLogs()
                }
            }

            addLog(
                speaker = Speaker.JARVIS,
                text = result.responseText,
                actionType = result.actionType,
                status = if (result.isSuccess) LogStatus.SUCCESS else LogStatus.FAILED,
                actionTarget = result.actionTarget
            )

            _uiState.update {
                it.copy(
                    isProcessing = false,
                    latestResponse = result.responseText,
                    statusBanner = "TRANSMITTING TO MR. STARK"
                )
            }

            // Speak response via TTS
            voiceManager.speak(
                text = result.responseText,
                onStart = {
                    _uiState.update { it.copy(isSpeaking = true) }
                },
                onDone = {
                    _uiState.update {
                        it.copy(
                            isSpeaking = false,
                            statusBanner = "ONLINE • READY FOR INPUT"
                        )
                    }
                }
            )

            refreshMetrics()
        }
    }

    fun onWakeUpTriggered(wakeCommand: String?) {
        val text = wakeCommand ?: "Jarvis wake up"
        _uiState.update {
            it.copy(
                selectedTab = 0,
                statusBanner = "WAKE WORD TRIGGER DETECTED"
            )
        }
        processCommand(text)
    }

    fun executeProtocol(protocol: ProtocolType) {
        _uiState.update { it.copy(currentProtocol = protocol) }
        if (protocol == ProtocolType.CLEAN_SLATE) {
            clearLogs()
        }
        processCommand(protocol.title)
    }

    fun toggleTorch() {
        val currentTorch = _uiState.value.systemMetrics.torchOn
        val newTorch = !currentTorch
        deviceController.setTorch(newTorch)
        refreshMetrics()
        val msg = if (newTorch) "Photon emitter engaged, sir." else "Flashlight turned off, sir."
        _uiState.update { it.copy(latestResponse = msg) }
        voiceManager.speak(msg)
    }

    fun launchApp(packageName: String, appName: String) {
        val launched = deviceController.launchAppByPackage(packageName)
        val reply = if (launched) "Opening $appName immediately, sir." else "Unable to launch $appName, sir."
        addLog(Speaker.JARVIS, reply, JarvisActionType.APP_LAUNCH, if (launched) LogStatus.SUCCESS else LogStatus.FAILED, appName)
        _uiState.update { it.copy(latestResponse = reply) }
        voiceManager.speak(reply)
    }

    fun toggleBackgroundWakeService(context: Context) {
        val intent = Intent(context, JarvisVoiceService::class.java)
        if (JarvisVoiceService.isServiceRunning) {
            intent.action = JarvisVoiceService.ACTION_STOP
            context.startService(intent)
            _uiState.update { it.copy(isBackgroundWakeActive = false) }
            val msg = "Background wake listener disengaged, sir."
            voiceManager.speak(msg)
        } else {
            intent.action = JarvisVoiceService.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _uiState.update { it.copy(isBackgroundWakeActive = true) }
            val msg = "Background wake listener active. I will respond anytime you say 'Jarvis wake up', sir."
            voiceManager.speak(msg)
        }
    }

    private fun addLog(
        speaker: Speaker,
        text: String,
        actionType: JarvisActionType,
        status: LogStatus = LogStatus.SUCCESS,
        actionTarget: String? = null
    ) {
        val entry = JarvisLogEntry(
            speaker = speaker,
            text = text,
            actionType = actionType,
            status = status,
            actionTarget = actionTarget
        )
        _uiState.update {
            it.copy(commandLogs = listOf(entry) + it.commandLogs.take(50))
        }
    }

    fun clearLogs() {
        _uiState.update { it.copy(commandLogs = emptyList()) }
    }

    override fun onCleared() {
        voiceManager.destroy()
        super.onCleared()
    }
}
