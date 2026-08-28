package com.example.data

import android.app.ActivityManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.Settings
import android.util.Log
import com.example.model.InstalledAppInfo
import com.example.model.SystemMetrics
import java.util.Locale

class DeviceController(private val context: Context) {

    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    }
    private var isTorchEnabled = false

    fun getSystemMetrics(): SystemMetrics {
        // Battery metrics
        var batteryPct = 100
        var isCharging = false
        var tempCelsius = 28.0f
        var voltage = 4.1f

        try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
            batteryStatus?.let { intent ->
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    batteryPct = (level * 100 / scale.toFloat()).toInt()
                }
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
                val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 280)
                tempCelsius = rawTemp / 10.0f
                val rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)
                voltage = if (rawVoltage > 100) rawVoltage / 1000.0f else rawVoltage.toFloat()
            }
        } catch (e: Exception) {
            Log.e("DeviceController", "Battery read error: ${e.message}")
        }

        // RAM Memory metrics
        var availRamMb = 4096L
        var totalRamMb = 8192L
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            availRamMb = memInfo.availMem / (1024 * 1024)
            totalRamMb = memInfo.totalMem / (1024 * 1024)
        } catch (e: Exception) {
            Log.e("DeviceController", "RAM read error: ${e.message}")
        }

        // Storage metrics
        var freeStorageGb = 64.0
        var totalStorageGb = 128.0
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val bytesAvailable = stat.availableBlocksLong * stat.blockSizeLong
            val bytesTotal = stat.blockCountLong * stat.blockSizeLong
            freeStorageGb = (bytesAvailable / (1024.0 * 1024.0 * 1024.0) * 10).toInt() / 10.0
            totalStorageGb = (bytesTotal / (1024.0 * 1024.0 * 1024.0) * 10).toInt() / 10.0
        } catch (e: Exception) {
            Log.e("DeviceController", "Storage read error: ${e.message}")
        }

        // Network metrics
        var isOnline = false
        var networkType = "Offline Mode"
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNetwork = cm?.activeNetwork
            val capabilities = cm?.getNetworkCapabilities(activeNetwork)
            if (capabilities != null) {
                isOnline = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                networkType = when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi (Quantum Link)"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G Telemetry"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Stark LAN Grid"
                    else -> "Connected"
                }
            }
        } catch (e: Exception) {
            Log.e("DeviceController", "Network read error: ${e.message}")
        }

        val deviceName = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        val androidVer = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
        val cpuCount = Runtime.getRuntime().availableProcessors()

        return SystemMetrics(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            batteryTemperatureCelsius = tempCelsius,
            batteryVoltage = voltage,
            availableRamMb = availRamMb,
            totalRamMb = totalRamMb,
            freeStorageGb = freeStorageGb,
            totalStorageGb = totalStorageGb,
            networkType = networkType,
            isOnline = isOnline,
            torchOn = isTorchEnabled,
            deviceModel = deviceName,
            androidVersion = androidVer,
            cpuCores = cpuCount
        )
    }

    fun setTorch(enable: Boolean): Boolean {
        return try {
            val cm = cameraManager ?: return false
            val cameraId = cm.cameraIdList.firstOrNull { id ->
                val characteristics = cm.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK
            } ?: cm.cameraIdList.firstOrNull() ?: return false

            cm.setTorchMode(cameraId, enable)
            isTorchEnabled = enable
            true
        } catch (e: CameraAccessException) {
            Log.e("DeviceController", "Flashlight error: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("DeviceController", "Flashlight unexpected error: ${e.message}")
            false
        }
    }

    fun toggleTorch(): Boolean {
        return setTorch(!isTorchEnabled)
    }

    fun isTorchOn(): Boolean = isTorchEnabled

    fun getInstalledApps(): List<InstalledAppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val list = mutableListOf<InstalledAppInfo>()

        for (resolveInfo in resolveInfos) {
            val appInfo = resolveInfo.activityInfo.applicationInfo
            val appName = resolveInfo.loadLabel(pm).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val icon = resolveInfo.loadIcon(pm)
            list.add(
                InstalledAppInfo(
                    appName = appName,
                    packageName = packageName,
                    isSystemApp = isSystem,
                    iconDrawable = icon
                )
            )
        }
        return list.sortedBy { it.appName.lowercase() }
    }

    fun launchAppByPackage(packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("DeviceController", "Failed to launch package $packageName: ${e.message}")
            false
        }
    }

    fun launchAppByName(query: String): Pair<Boolean, String?> {
        val cleanQuery = query.lowercase(Locale.ROOT).trim()
        val apps = getInstalledApps()

        // 1. Exact or startsWith match
        val matchedApp = apps.firstOrNull { it.appName.lowercase(Locale.ROOT) == cleanQuery }
            ?: apps.firstOrNull { it.appName.lowercase(Locale.ROOT).startsWith(cleanQuery) }
            ?: apps.firstOrNull { it.appName.lowercase(Locale.ROOT).contains(cleanQuery) }

        if (matchedApp != null) {
            val launched = launchAppByPackage(matchedApp.packageName)
            return Pair(launched, matchedApp.appName)
        }

        // 2. Known common intent shortcuts if specific app not matched
        when (cleanQuery) {
            "camera" -> {
                return try {
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Pair(true, "Camera")
                } catch (e: Exception) {
                    Pair(false, null)
                }
            }
            "calculator" -> {
                val calcPackages = listOf(
                    "com.google.android.calculator",
                    "com.android.calculator2",
                    "com.sec.android.app.popupcalculator"
                )
                for (pkg in calcPackages) {
                    if (launchAppByPackage(pkg)) return Pair(true, "Calculator")
                }
            }
            "settings" -> {
                return try {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Pair(true, "Settings")
                } catch (e: Exception) {
                    Pair(false, null)
                }
            }
            "gallery", "photos" -> {
                return try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "image/*"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Pair(true, "Gallery")
                } catch (e: Exception) {
                    Pair(false, null)
                }
            }
            "music" -> {
                return try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MUSIC)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Pair(true, "Music")
                } catch (e: Exception) {
                    Pair(false, null)
                }
            }
            "browser", "chrome", "internet" -> {
                return try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    Pair(true, "Web Browser")
                } catch (e: Exception) {
                    Pair(false, null)
                }
            }
        }

        return Pair(false, null)
    }

    fun searchWeb(query: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            } catch (e2: Exception) {
                false
            }
        }
    }

    fun sendMessage(recipientPhoneOrName: String?, messageText: String): Boolean {
        return try {
            val uri = if (!recipientPhoneOrName.isNullOrBlank()) {
                Uri.parse("smsto:${Uri.encode(recipientPhoneOrName)}")
            } else {
                Uri.parse("smsto:")
            }
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", messageText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Send SMS error: ${e.message}")
            false
        }
    }

    fun dialPhoneNumber(phoneNumber: String): Boolean {
        return try {
            val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Dial phone error: ${e.message}")
            false
        }
    }

    fun setSystemAlarm(hour: Int, minute: Int, message: String = "JARVIS Alert"): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Alarm error: ${e.message}")
            false
        }
    }

    fun setSystemTimer(seconds: Int, message: String = "JARVIS Timer"): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Timer error: ${e.message}")
            false
        }
    }

    fun openDeviceSetting(type: String): Boolean {
        return try {
            val action = when (type.lowercase(Locale.ROOT)) {
                "wifi", "wi-fi", "internet" -> Settings.ACTION_WIFI_SETTINGS
                "bluetooth", "bt" -> Settings.ACTION_BLUETOOTH_SETTINGS
                "sound", "volume", "audio" -> Settings.ACTION_SOUND_SETTINGS
                "display", "brightness", "screen" -> Settings.ACTION_DISPLAY_SETTINGS
                "battery", "power" -> Settings.ACTION_BATTERY_SAVER_SETTINGS
                "location", "gps" -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
                "apps", "applications" -> Settings.ACTION_APPLICATION_SETTINGS
                else -> Settings.ACTION_SETTINGS
            }
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("DeviceController", "Open settings error: ${e.message}")
            false
        }
    }
}
