package com.example.engine

import android.content.Context
import android.util.Log
import com.example.data.DeviceController
import com.example.data.GeminiApiClient
import com.example.data.OfflineKnowledgeBase
import com.example.model.JarvisActionType
import com.example.model.ProtocolType
import java.util.Locale

data class JarvisExecutionResult(
    val responseText: String,
    val actionType: JarvisActionType,
    val protocolChanged: ProtocolType? = null,
    val actionTarget: String? = null,
    val isSuccess: Boolean = true
)

class JarvisIntelligenceEngine(
    private val context: Context,
    private val deviceController: DeviceController
) {
    private val geminiApiClient = GeminiApiClient()

    suspend fun processQuery(rawQuery: String): JarvisExecutionResult {
        val query = rawQuery.trim()
        if (query.isBlank()) {
            return JarvisExecutionResult(
                responseText = "At your command, sir.",
                actionType = JarvisActionType.OFFLINE_KNOWLEDGE
            )
        }

        val qLower = query.lowercase(Locale.ROOT)

        // 1. Check Protocol Triggers
        when {
            qLower.contains("clean slate") -> {
                return JarvisExecutionResult(
                    responseText = ProtocolType.CLEAN_SLATE.promptResponse,
                    actionType = JarvisActionType.PROTOCOL,
                    protocolChanged = ProtocolType.CLEAN_SLATE
                )
            }
            qLower.contains("house party") -> {
                return JarvisExecutionResult(
                    responseText = ProtocolType.HOUSE_PARTY.promptResponse,
                    actionType = JarvisActionType.PROTOCOL,
                    protocolChanged = ProtocolType.HOUSE_PARTY
                )
            }
            qLower.contains("sentry") || qLower.contains("security protocol") -> {
                return JarvisExecutionResult(
                    responseText = ProtocolType.SENTRY_MODE.promptResponse,
                    actionType = JarvisActionType.PROTOCOL,
                    protocolChanged = ProtocolType.SENTRY_MODE
                )
            }
            qLower.contains("stealth") -> {
                return JarvisExecutionResult(
                    responseText = ProtocolType.STEALTH.promptResponse,
                    actionType = JarvisActionType.PROTOCOL,
                    protocolChanged = ProtocolType.STEALTH
                )
            }
            qLower.contains("diagnostics") || qLower.contains("scan") || qLower.contains("telemetry") -> {
                val metrics = deviceController.getSystemMetrics()
                val status = "Diagnostic scan complete, sir. Core temperature is ${metrics.batteryTemperatureCelsius}°C, power reserves at ${metrics.batteryLevel}%, RAM usage is ${metrics.totalRamMb - metrics.availableRamMb} MB of ${metrics.totalRamMb} MB. Uplink: ${metrics.networkType}."
                return JarvisExecutionResult(
                    responseText = status,
                    actionType = JarvisActionType.SYSTEM_SCAN,
                    protocolChanged = ProtocolType.DIAGNOSTICS
                )
            }
        }

        // 2. Flashlight / Torch
        if (qLower.contains("flashlight") || qLower.contains("torch") || qLower.contains("lumos") || qLower.contains("light")) {
            val enable = !qLower.contains("off") && !qLower.contains("disable") && !qLower.contains("stop")
            val success = deviceController.setTorch(enable)
            val response = if (success) {
                if (enable) "Photon emitter activated, sir." else "Flashlight disengaged, sir."
            } else {
                "Unable to access hardware photon emitter, sir."
            }
            return JarvisExecutionResult(
                responseText = response,
                actionType = JarvisActionType.DEVICE_CONTROL,
                actionTarget = "Flashlight",
                isSuccess = success
            )
        }

        // 3. Battery & Power Check
        if (qLower.contains("battery") || qLower.contains("power level") || qLower.contains("charge") || qLower.contains("energy")) {
            val metrics = deviceController.getSystemMetrics()
            val chargingText = if (metrics.isCharging) " (charging on auxiliary grid)" else ""
            val response = "Arc reactor battery capacity is at ${metrics.batteryLevel}%$chargingText, operating at ${metrics.batteryVoltage} Volts."
            return JarvisExecutionResult(
                responseText = response,
                actionType = JarvisActionType.DEVICE_CONTROL,
                actionTarget = "Battery"
            )
        }

        // 4. Open Application
        val openAppRegex = Regex("""^(?:open|launch|start|run|show)\s+(?:the\s+)?(.+)$""")
        val openMatch = openAppRegex.find(qLower)
        if (openMatch != null) {
            val appQuery = openMatch.groupValues[1].trim()
            val (launched, appName) = deviceController.launchAppByName(appQuery)
            if (launched) {
                return JarvisExecutionResult(
                    responseText = "Opening $appName immediately, sir.",
                    actionType = JarvisActionType.APP_LAUNCH,
                    actionTarget = appName,
                    isSuccess = true
                )
            }
        }

        // 5. Send Message / SMS
        val smsRegex = Regex("""^(?:send message to|text|message|send sms to)\s+([^\s]+)(?:\s+(?:saying|that|with)\s+(.+))?$""")
        val smsMatch = smsRegex.find(qLower)
        if (smsMatch != null) {
            val recipient = smsMatch.groupValues[1].trim()
            val messageBody = if (smsMatch.groupValues.size > 2) smsMatch.groupValues[2].trim() else "Greetings from Stark Industries."
            val sent = deviceController.sendMessage(recipient, messageBody)
            return JarvisExecutionResult(
                responseText = "Drafting transmission to $recipient, sir.",
                actionType = JarvisActionType.SEND_MESSAGE,
                actionTarget = "$recipient: $messageBody",
                isSuccess = sent
            )
        }

        // 6. Dial Phone Number
        val dialRegex = Regex("""^(?:call|dial|phone)\s+([^\s]+)$""")
        val dialMatch = dialRegex.find(qLower)
        if (dialMatch != null) {
            val phone = dialMatch.groupValues[1].trim()
            val dialed = deviceController.dialPhoneNumber(phone)
            return JarvisExecutionResult(
                responseText = "Establishing comms channel with $phone, sir.",
                actionType = JarvisActionType.DIAL_PHONE,
                actionTarget = phone,
                isSuccess = dialed
            )
        }

        // 7. Search Web / Google
        val searchRegex = Regex("""^(?:search|google|look up|search for|find on web)\s+(.+)$""")
        val searchMatch = searchRegex.find(qLower)
        if (searchMatch != null) {
            val searchQuery = searchMatch.groupValues[1].trim()
            val searched = deviceController.searchWeb(searchQuery)
            return JarvisExecutionResult(
                responseText = "Accessing global databanks for '$searchQuery', sir.",
                actionType = JarvisActionType.WEB_SEARCH,
                actionTarget = searchQuery,
                isSuccess = searched
            )
        }

        // 8. Open Device Settings
        if (qLower.startsWith("open setting") || qLower.startsWith("wifi setting") || qLower.startsWith("bluetooth setting") || qLower.startsWith("display setting") || qLower.startsWith("volume setting")) {
            val settingType = qLower.replace("setting", "").replace("settings", "").replace("open", "").trim()
            deviceController.openDeviceSetting(settingType)
            return JarvisExecutionResult(
                responseText = "Opening hardware settings console, sir.",
                actionType = JarvisActionType.DEVICE_CONTROL,
                actionTarget = settingType
            )
        }

        // 9. Alarm / Timer
        val timerRegex = Regex("""^(?:set timer for|timer for)\s+(\d+)\s*(?:minutes?|mins?|seconds?|secs?)""")
        val timerMatch = timerRegex.find(qLower)
        if (timerMatch != null) {
            val count = timerMatch.groupValues[1].toIntOrNull() ?: 60
            val seconds = if (qLower.contains("second") || qLower.contains("sec")) count else count * 60
            deviceController.setSystemTimer(seconds, "Stark Alert")
            return JarvisExecutionResult(
                responseText = "Timer configured for $count ${if (seconds >= 60) "minutes" else "seconds"}, sir.",
                actionType = JarvisActionType.DEVICE_CONTROL,
                actionTarget = "Timer: $count"
            )
        }

        // 10. Math & Calculations (Instant Offline Calculation)
        val mathResult = OfflineMathLogicEngine.tryEvaluateMath(query)
        if (mathResult != null) {
            return JarvisExecutionResult(
                responseText = mathResult,
                actionType = JarvisActionType.CALCULATION
            )
        }

        // 11. Unit Conversions (Instant Offline Calculation)
        val convResult = OfflineMathLogicEngine.tryEvaluateConversion(query)
        if (convResult != null) {
            return JarvisExecutionResult(
                responseText = convResult,
                actionType = JarvisActionType.CONVERSION
            )
        }

        // 12. Offline Knowledge Base & Personality Responses
        val offlineKnowledgeResult = OfflineKnowledgeBase.searchKnowledgeBase(query)
        if (offlineKnowledgeResult != null) {
            return JarvisExecutionResult(
                responseText = offlineKnowledgeResult,
                actionType = JarvisActionType.OFFLINE_KNOWLEDGE
            )
        }

        // 13. Online Gemini AI Reasoning (if online) or Offline Fallback
        val metrics = deviceController.getSystemMetrics()
        return if (metrics.isOnline) {
            try {
                val geminiResponse = geminiApiClient.queryGemini(query)
                JarvisExecutionResult(
                    responseText = geminiResponse,
                    actionType = JarvisActionType.AI_QUERY
                )
            } catch (e: Exception) {
                Log.e("JarvisEngine", "Online query failed: ${e.message}")
                JarvisExecutionResult(
                    responseText = "I have processed your query through offline reasoning arrays, sir. Ready for your next instruction.",
                    actionType = JarvisActionType.OFFLINE_KNOWLEDGE
                )
            }
        } else {
            // Pure Offline logical reply
            val offlineAnswer = "Offline protocol active, sir. Global uplink unavailable, but internal tactical subsystems and phone controls remain fully responsive. Try asking me to open apps, solve math, convert units, toggle flashlight, run diagnostics, or execute protocols."
            JarvisExecutionResult(
                responseText = offlineAnswer,
                actionType = JarvisActionType.OFFLINE_KNOWLEDGE
            )
        }
    }
}
