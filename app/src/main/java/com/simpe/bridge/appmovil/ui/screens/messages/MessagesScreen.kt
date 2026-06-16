package com.simpe.bridge.appmovil.ui.screens.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import com.simpe.bridge.appmovil.ui.components.AirScreenPadding
import com.simpe.bridge.appmovil.ui.components.DashboardHeroSection
import com.simpe.bridge.appmovil.ui.components.MessageDetailModal
import com.simpe.bridge.appmovil.ui.components.MessageItem
import com.simpe.bridge.appmovil.ui.components.QuickActionsSection
import com.simpe.bridge.appmovil.ui.components.SectionTitle
import com.simpe.bridge.appmovil.ui.components.SummarySection
import com.simpe.bridge.appmovil.ui.components.SystemStatusSection
import com.simpe.bridge.appmovil.ui.components.UsageDistributionSection
import com.simpe.bridge.appmovil.ui.preferences.UiPreferences

@Composable
fun MessagesScreen(
    messages: List<SmsMessage>,
    uiPrefs: UiPreferences,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit,
    onTestSms: () -> Unit,
    onOpenScan: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
) {
    var selectedMessage by remember { mutableStateOf<SmsMessage?>(null) }
    val densityScale = uiPrefs.density.scale

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AirScreenPadding),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy((16.dp * densityScale).coerceAtLeast(10.dp))
    ) {
        item {
            DashboardHeroSection(
                totalSms = messages.size,
                onTestSms = onTestSms,
            )
        }
        item {
            SummarySection(totalSms = messages.size)
        }
        if (uiPrefs.showSystemStatus) {
            item { SystemStatusSection() }
        }
        if (uiPrefs.showDistribution) {
            item { UsageDistributionSection(totalSms = messages.size) }
        }
        item {
            QuickActionsSection(
                onTestSms = onTestSms,
                onOpenScan = onOpenScan,
                onOpenSettings = onOpenSettings,
                onOpenAppearance = onOpenAppearance,
            )
        }
        if (uiPrefs.showActivityFeed) {
            item {
                Spacer(Modifier.height(4.dp))
                SectionTitle(
                    title = "Actividad reciente",
                    subtitle = if (messages.isEmpty()) "Sin mensajes" else "${messages.size} mensajes"
                )
            }
            if (messages.isEmpty()) {
                item { EmptyMessagesState() }
            } else {
                items(items = messages, key = { it.envelope.messageId }) { message ->
                    MessageItem(
                        message = message,
                        onClick = { selectedMessage = message }
                    )
                }
            }
        }
    }

    if (selectedMessage != null) {
        MessageDetailModal(
            message = selectedMessage,
            onDismiss = { selectedMessage = null },
            onCopyText = onCopyText,
            onCopyJson = onCopyJson
        )
    }
}

@Composable
fun EmptyMessagesState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Sms,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )
            Text(
                text = "Sin mensajes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Activa el listener o usa Test SMS para comenzar a recibir mensajes.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
