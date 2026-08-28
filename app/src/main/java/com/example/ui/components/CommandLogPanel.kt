package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisActionType
import com.example.model.JarvisLogEntry
import com.example.model.Speaker
import com.example.ui.theme.JarvisAlertRed
import com.example.ui.theme.JarvisAmber
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommandLogPanel(
    logs: List<JarvisLogEntry>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Terminal",
                    tint = JarvisCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "QUANTUM COMMAND LOG (${logs.size})",
                    color = JarvisCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            if (logs.isNotEmpty()) {
                Button(
                    onClick = onClearLogs,
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceElevated),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                        .testTag("clear_logs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        tint = JarvisTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PURGE",
                        color = JarvisTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(JarvisSurfaceDark)
                    .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AWAITING USER TRANSMISSIONS OR VOICE INPUT...",
                    color = JarvisTextMuted,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(logs) { log ->
                    LogEntryCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun LogEntryCard(log: JarvisLogEntry) {
    val isJarvis = log.speaker == Speaker.JARVIS
    val timeFormatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))

    val speakerColor = if (isJarvis) JarvisCyan else JarvisAmber
    val tagColor = when (log.actionType) {
        JarvisActionType.APP_LAUNCH -> JarvisGreen
        JarvisActionType.DEVICE_CONTROL -> JarvisAmber
        JarvisActionType.CALCULATION -> JarvisCyanBright
        JarvisActionType.PROTOCOL -> JarvisCyan
        JarvisActionType.SEND_MESSAGE -> JarvisCyanBright
        JarvisActionType.DIAL_PHONE -> JarvisGreen
        else -> JarvisCyan
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isJarvis) JarvisSurfaceDark else JarvisSurfaceElevated)
            .border(
                1.dp,
                if (isJarvis) JarvisSurfaceBorder else JarvisAmber.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isJarvis) "JARVIS >" else "USER >",
                        color = speakerColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tagColor.copy(alpha = 0.15f))
                            .border(0.5.dp, tagColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = log.actionType.name,
                            color = tagColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = timeFormatted,
                    color = JarvisTextTertiary,
                    fontSize = 9.sp
                )
            }

            Text(
                text = log.text,
                color = JarvisTextPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            if (!log.actionTarget.isNullOrBlank()) {
                Text(
                    text = "TARGET: ${log.actionTarget}",
                    color = JarvisTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
