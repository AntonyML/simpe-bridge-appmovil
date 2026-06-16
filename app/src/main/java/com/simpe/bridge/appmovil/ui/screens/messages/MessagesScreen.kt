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
import com.simpe.bridge.appmovil.ui.components.AirCardPadding
import com.simpe.bridge.appmovil.ui.components.AirScreenPadding
import com.simpe.bridge.appmovil.ui.components.GlassCard
import com.simpe.bridge.appmovil.ui.components.MessageDetailModal
import com.simpe.bridge.appmovil.ui.components.MessageItem
import com.simpe.bridge.appmovil.ui.components.QuickActionsSection
import com.simpe.bridge.appmovil.ui.components.SectionTitle
import com.simpe.bridge.appmovil.ui.components.SummarySection
import com.simpe.bridge.appmovil.ui.components.SystemStatusSection
import com.simpe.bridge.appmovil.ui.components.UsageDistributionSection
import com.simpe.bridge.appmovil.ui.preferences.GlassIntensity
import com.simpe.bridge.appmovil.ui.preferences.UiPreferences

@Composable
fun MessagesScreen(
    messages: List<SmsMessage>,
    uiPrefs: UiPreferences,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit,
    onTestSms: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var selectedMessage by remember { mutableStateOf<SmsMessage?>(null) }
    val densityScale = uiPrefs.density.scale
    val glass = uiPrefs.glass

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AirScreenPadding),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy((16.dp * densityScale).coerceAtLeast(8.dp))
    ) {
        item {
            DashboardHeader(totalSms = messages.size)
        }
        item {
            GlassCard(intensity = glass) {
                DashboardHeaderContent(totalSms = messages.size)
            }
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
                onOpenSettings = onOpenSettings,
                onOpenAppearance = onOpenSettings
            )
        }
        if (uiPrefs.showActivityFeed) {
            item {
                Spacer(Modifier.height(8.dp))
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
private fun DashboardHeader(totalSms: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Panel",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Hola",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DashboardHeaderContent(totalSms: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Resumen general",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bridge activo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Capturando mensajes en segundo plano",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = totalSms.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "SMS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyMessagesState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Sms,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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
