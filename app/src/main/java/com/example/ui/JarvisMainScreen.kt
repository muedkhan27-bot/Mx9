package com.example.ui

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MotionPhotosAuto
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ProtocolType
import com.example.ui.components.ArcReactorVisualizer
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CommandLogPanel
import com.example.ui.components.JarvisHudTopBar
import com.example.ui.components.ProtocolsAndActionsPanel
import com.example.ui.components.StarkTelemetryPanel
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackgroundDark
import com.example.ui.theme.JarvisBackgroundNavy
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary
import com.example.viewmodel.JarvisUiState
import com.example.viewmodel.JarvisViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun JarvisMainScreen(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Runtime Permissions Request
    val permissionsToRequest = remember {
        val list = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list
    }
    val permissionState = rememberMultiplePermissionsState(permissions = permissionsToRequest)

    LaunchedEffect(Unit) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    var textPromptInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackgroundDark)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding(),
        topBar = {
            JarvisHudTopBar(
                systemMetrics = uiState.systemMetrics,
                currentProtocol = uiState.currentProtocol,
                isBackgroundWakeActive = uiState.isBackgroundWakeActive,
                onToggleWakeService = { viewModel.toggleBackgroundWakeService(context) },
                onToggleTorch = { viewModel.toggleTorch() }
            )
        },
        bottomBar = {
            JarvisBottomNavigationBar(
                selectedTab = uiState.selectedTab,
                currentProtocol = uiState.currentProtocol,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = JarvisBackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status Announcement Marquee
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JarvisSurfaceDark)
                    .border(0.5.dp, JarvisSurfaceBorder)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.statusBanner,
                    color = when {
                        uiState.isListening -> JarvisAlertRed
                        uiState.isSpeaking -> JarvisCyanBright
                        uiState.isProcessing -> JarvisAmber
                        else -> JarvisCyan
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            // Main Tab Views
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.selectedTab) {
                    0 -> JarvisHudCoreView(
                        uiState = uiState,
                        onArcReactorClick = {
                            if (!permissionState.allPermissionsGranted) {
                                permissionState.launchMultiplePermissionRequest()
                            }
                            viewModel.toggleListening()
                        },
                        onQuickCommand = { prompt ->
                            viewModel.submitTextCommand(prompt)
                        }
                    )
                    1 -> StarkTelemetryPanel(
                        systemMetrics = uiState.systemMetrics,
                        onRunScan = { viewModel.executeProtocol(ProtocolType.DIAGNOSTICS) },
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> ProtocolsAndActionsPanel(
                        currentProtocol = uiState.currentProtocol,
                        installedApps = uiState.installedApps,
                        onExecuteProtocol = { viewModel.executeProtocol(it) },
                        onExecuteCommand = { viewModel.submitTextCommand(it) },
                        onLaunchApp = { pkg, name -> viewModel.launchApp(pkg, name) },
                        onToggleTorch = { viewModel.toggleTorch() },
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> CommandLogPanel(
                        logs = uiState.commandLogs,
                        onClearLogs = { viewModel.clearLogs() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bottom Floating Voice & Text Command Console
            JarvisInputConsole(
                textInput = textPromptInput,
                onTextInputChange = { textPromptInput = it },
                isListening = uiState.isListening,
                isSpeaking = uiState.isSpeaking,
                currentProtocol = uiState.currentProtocol,
                onToggleMic = {
                    if (!permissionState.allPermissionsGranted) {
                        permissionState.launchMultiplePermissionRequest()
                    }
                    viewModel.toggleListening()
                },
                onSubmit = {
                    if (textPromptInput.isNotBlank()) {
                        viewModel.submitTextCommand(textPromptInput)
                        textPromptInput = ""
                        keyboardController?.hide()
                    }
                }
            )
        }
    }
}

@Composable
private fun JarvisHudCoreView(
    uiState: JarvisUiState,
    onArcReactorClick: () -> Unit,
    onQuickCommand: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Central Arc Reactor Core
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            ArcReactorVisualizer(
                isListening = uiState.isListening,
                isSpeaking = uiState.isSpeaking,
                isProcessing = uiState.isProcessing,
                rmsLevel = uiState.rmsLevel,
                currentProtocol = uiState.currentProtocol,
                onClick = onArcReactorClick
            )
        }

        // 2. Audio Waveform Spectral Bars
        AudioWaveformVisualizer(
            isListening = uiState.isListening,
            isSpeaking = uiState.isSpeaking,
            rmsLevel = uiState.rmsLevel,
            primaryColor = uiState.currentProtocol.color,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // 3. Holographic Response Terminal Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            JarvisSurfaceDark.copy(alpha = 0.95f),
                            JarvisSurfaceElevated.copy(alpha = 0.85f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = uiState.currentProtocol.color.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.Terminal,
                            contentDescription = "Terminal",
                            tint = uiState.currentProtocol.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "J.A.R.V.I.S. QUANTUM TRANSCEIVER",
                            color = uiState.currentProtocol.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    if (uiState.isSpeaking) {
                        Text(
                            text = "TRANSMITTING...",
                            color = JarvisCyanBright,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = uiState.latestResponse,
                    color = JarvisTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // 4. Quick Suggestion Protocol Chips
        val quickPrompts = listOf(
            "Status report",
            "Open Camera",
            "Flashlight on",
            "Calculate 15% of 2400",
            "Clean Slate",
            "Search Quantum Physics",
            "Convert 100 miles to km",
            "Tell me a joke"
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(JarvisSurfaceDark)
                        .border(1.dp, JarvisCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { onQuickCommand(prompt) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("quick_chip_${prompt.lowercase().replace(" ", "_")}")
                ) {
                    Text(
                        text = prompt,
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun JarvisInputConsole(
    textInput: String,
    onTextInputChange: (String) -> Unit,
    isListening: Boolean,
    isSpeaking: Boolean,
    currentProtocol: ProtocolType,
    onToggleMic: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JarvisBackgroundDark)
            .border(
                width = 1.dp,
                color = currentProtocol.color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Voice Mic Arc Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isListening) JarvisAlertRed else currentProtocol.color.copy(alpha = 0.2f))
                .border(1.5.dp, if (isListening) JarvisAlertRed else currentProtocol.color, CircleShape)
                .clickable(onClick = onToggleMic)
                .testTag("voice_mic_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Speak to Jarvis",
                tint = if (isListening) Color.White else currentProtocol.color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text Input Field
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextInputChange,
            placeholder = {
                Text(
                    text = if (isListening) "Listening to your voice..." else "Command JARVIS or say 'Jarvis wake up'...",
                    color = JarvisTextMuted,
                    fontSize = 12.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .testTag("jarvis_text_input"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = JarvisSurfaceElevated,
                unfocusedContainerColor = JarvisSurfaceDark,
                focusedBorderColor = currentProtocol.color,
                unfocusedBorderColor = JarvisSurfaceBorder,
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSubmit() })
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Send Button
        IconButton(
            onClick = onSubmit,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(JarvisSurfaceElevated)
                .border(1.dp, JarvisSurfaceBorder, CircleShape)
                .testTag("send_command_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Command",
                tint = if (textInput.isNotBlank()) currentProtocol.color else JarvisTextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun JarvisBottomNavigationBar(
    selectedTab: Int,
    currentProtocol: ProtocolType,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JarvisBackgroundDark)
            .border(
                width = 1.dp,
                color = JarvisSurfaceBorder,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavHudItem(
            icon = Icons.Default.MotionPhotosAuto,
            label = "ARC HUD",
            isSelected = selectedTab == 0,
            color = currentProtocol.color,
            onClick = { onTabSelected(0) }
        )
        NavHudItem(
            icon = Icons.Default.Analytics,
            label = "TELEMETRY",
            isSelected = selectedTab == 1,
            color = currentProtocol.color,
            onClick = { onTabSelected(1) }
        )
        NavHudItem(
            icon = Icons.Default.Security,
            label = "PROTOCOLS",
            isSelected = selectedTab == 2,
            color = currentProtocol.color,
            onClick = { onTabSelected(2) }
        )
        NavHudItem(
            icon = Icons.Default.Terminal,
            label = "LOGS",
            isSelected = selectedTab == 3,
            color = currentProtocol.color,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
private fun NavHudItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("nav_item_${label.lowercase().replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) color else JarvisTextTertiary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) color else JarvisTextTertiary,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 1.sp
            )
        }
    }
}
