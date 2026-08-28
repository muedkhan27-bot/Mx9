package com.example.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGreen

enum class Speaker {
    USER,
    JARVIS
}

enum class JarvisActionType {
    AI_QUERY,
    APP_LAUNCH,
    DEVICE_CONTROL,
    WEB_SEARCH,
    SEND_MESSAGE,
    DIAL_PHONE,
    CALCULATION,
    CONVERSION,
    PROTOCOL,
    SYSTEM_SCAN,
    OFFLINE_KNOWLEDGE
}

enum class LogStatus {
    SUCCESS,
    PENDING,
    FAILED,
    INFO
}

data class JarvisLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val speaker: Speaker,
    val text: String,
    val actionType: JarvisActionType = JarvisActionType.AI_QUERY,
    val status: LogStatus = LogStatus.SUCCESS,
    val actionTarget: String? = null
)

data class SystemMetrics(
    val batteryLevel: Int = 100,
    val isCharging: Boolean = false,
    val batteryTemperatureCelsius: Float = 28.5f,
    val batteryVoltage: Float = 4.1f,
    val availableRamMb: Long = 4096,
    val totalRamMb: Long = 8192,
    val freeStorageGb: Double = 64.0,
    val totalStorageGb: Double = 128.0,
    val networkType: String = "Wi-Fi (Quantum Uplink)",
    val isOnline: Boolean = true,
    val torchOn: Boolean = false,
    val deviceModel: String = "Stark Mark VII",
    val androidVersion: String = "Android 15 (StarkOS 9.4)",
    val cpuCores: Int = 8
)

enum class ProtocolType(
    val title: String,
    val codeName: String,
    val description: String,
    val color: Color,
    val promptResponse: String
) {
    STANDARD(
        title = "STANDARD ASSISTANT",
        codeName = "MARK-ONLINE",
        description = "Standard Jarvis interactive assistant mode with online & offline capabilities.",
        color = JarvisCyan,
        promptResponse = "Standard operational parameters restored, sir. All sub-routines active."
    ),
    DIAGNOSTICS(
        title = "DEEP SCAN PROTOCOL",
        codeName = "DIAGNOSTIC-SCAN",
        description = "Full telemetry and sensor scan across all onboard system components.",
        color = JarvisGreen,
        promptResponse = "Running full diagnostics on all Stark hardware and device telemetry, sir."
    ),
    HOUSE_PARTY(
        title = "HOUSE PARTY PROTOCOL",
        codeName = "PROTOCOL-PARTY",
        description = "Activates all auxiliary protocols and maximum visual/audio responsiveness.",
        color = JarvisGold,
        promptResponse = "House Party Protocol initiated, sir. All auxiliary suits and sound systems on standby."
    ),
    SENTRY_MODE(
        title = "SENTRY SECURITY",
        codeName = "PROTOCOL-SENTRY",
        description = "Heightened alert mode with continuous perimeter monitoring.",
        color = JarvisAlertRed,
        promptResponse = "Sentry perimeter active, sir. Security protocols running at 100% vigilance."
    ),
    STEALTH(
        title = "STEALTH PROTOCOL",
        codeName = "PROTOCOL-STEALTH",
        description = "Low-emission acoustic mode with suppressed visual signatures and minimal power.",
        color = JarvisAmber,
        promptResponse = "Stealth protocol engaged. Thermal and acoustic signatures dampened, sir."
    ),
    CLEAN_SLATE(
        title = "CLEAN SLATE PROTOCOL",
        codeName = "PROTOCOL-CLEAN-SLATE",
        description = "Clears active command memory, logs, and resets all background processes.",
        color = JarvisCyan,
        promptResponse = "Clean Slate Protocol executed. Command cache purged, memory refreshed."
    )
}

data class QuickCommand(
    val id: String,
    val title: String,
    val command: String,
    val actionType: JarvisActionType,
    val iconName: String
)

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean = false,
    val iconDrawable: Drawable? = null
)
