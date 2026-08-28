package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InstalledAppInfo
import com.example.model.ProtocolType
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBright
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceDark
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.JarvisTextTertiary
import java.util.Locale

@Composable
fun ProtocolsAndActionsPanel(
    currentProtocol: ProtocolType,
    installedApps: List<InstalledAppInfo>,
    onExecuteProtocol: (ProtocolType) -> Unit,
    onExecuteCommand: (String) -> Unit,
    onLaunchApp: (String, String) -> Unit,
    onToggleTorch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appSearchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(installedApps, appSearchQuery) {
        if (appSearchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter {
                it.appName.contains(appSearchQuery, ignoreCase = true) ||
                        it.packageName.contains(appSearchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Stark Protocols Carousel
        item {
            Column {
                Text(
                    text = "STARK PROTOCOL DIRECTIVES",
                    color = JarvisCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ProtocolType.values()) { protocol ->
                        val isSelected = protocol == currentProtocol
                        ProtocolCard(
                            protocol = protocol,
                            isSelected = isSelected,
                            onClick = { onExecuteProtocol(protocol) }
                        )
                    }
                }
            }
        }

        // 2. Phone Actions & Device Commands
        item {
            Column {
                Text(
                    text = "DIRECT PHONE CONTROLS",
                    color = JarvisAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhoneActionButton(
                        icon = Icons.Default.FlashlightOn,
                        title = "PHOTON BEAM",
                        subtitle = "Toggle Torch",
                        color = JarvisAmber,
                        modifier = Modifier.weight(1f),
                        onClick = onToggleTorch
                    )
                    PhoneActionButton(
                        icon = Icons.Default.Search,
                        title = "WEB DATABANKS",
                        subtitle = "Google Search",
                        color = JarvisCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onExecuteCommand("Search latest quantum computing breakthroughs") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhoneActionButton(
                        icon = Icons.AutoMirrored.Filled.Message,
                        title = "COMMS DISPATCH",
                        subtitle = "Send SMS",
                        color = JarvisCyanBright,
                        modifier = Modifier.weight(1f),
                        onClick = { onExecuteCommand("Send message to Tony Stark saying Flight test ready") }
                    )
                    PhoneActionButton(
                        icon = Icons.Default.Alarm,
                        title = "STARK TIMER",
                        subtitle = "Set 5 Min Alert",
                        color = JarvisAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { onExecuteCommand("Set timer for 5 minutes") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhoneActionButton(
                        icon = Icons.Default.Settings,
                        title = "WI-FI SETTINGS",
                        subtitle = "Network Config",
                        color = JarvisCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onExecuteCommand("Open wifi settings") }
                    )
                    PhoneActionButton(
                        icon = Icons.Default.Call,
                        title = "DIALER RELAY",
                        subtitle = "Open Phone Comms",
                        color = JarvisCyanBright,
                        modifier = Modifier.weight(1f),
                        onClick = { onExecuteCommand("Call 911") }
                    )
                }
            }
        }

        // 3. Installed Apps Launcher Matrix
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Apps, contentDescription = "Apps", tint = JarvisCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INSTALLED APPS LAUNCHER (${installedApps.size})",
                            color = JarvisCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = appSearchQuery,
                    onValueChange = { appSearchQuery = it },
                    placeholder = { Text("Filter apps or say 'Open [App Name]'...", color = JarvisTextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_search_input"),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = JarvisSurfaceElevated,
                        unfocusedContainerColor = JarvisSurfaceDark,
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = JarvisSurfaceBorder,
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        items(filteredApps) { app ->
            AppItemRow(
                app = app,
                onLaunch = { onLaunchApp(app.packageName, app.appName) }
            )
        }
    }
}

@Composable
private fun ProtocolCard(
    protocol: ProtocolType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) protocol.color.copy(alpha = 0.2f) else JarvisSurfaceDark)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) protocol.color else JarvisSurfaceBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("protocol_card_${protocol.name.lowercase()}")
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = protocol.title,
                    tint = protocol.color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = protocol.codeName,
                    color = protocol.color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = protocol.title,
                color = JarvisTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = protocol.description,
                color = JarvisTextTertiary,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun PhoneActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(JarvisSurfaceDark)
            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("phone_action_${title.lowercase().replace(" ", "_")}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = JarvisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = JarvisTextTertiary, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun AppItemRow(
    app: InstalledAppInfo,
    onLaunch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(JarvisSurfaceDark)
            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onLaunch)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("app_item_${app.packageName}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = app.appName, color = JarvisTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = app.packageName, color = JarvisTextTertiary, fontSize = 9.sp, maxLines = 1)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(JarvisSurfaceElevated)
                .border(1.dp, JarvisCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = "LAUNCH", color = JarvisCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
