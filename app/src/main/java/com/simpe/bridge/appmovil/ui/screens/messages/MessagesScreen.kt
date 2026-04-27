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
import com.simpe.bridge.appmovil.ui.components.DashboardCards
import com.simpe.bridge.appmovil.ui.components.MessageDetailModal
import com.simpe.bridge.appmovil.ui.components.MessageItem

@Composable
fun MessagesScreen(
    messages: List<SmsMessage>,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit
) {
    var selectedMessage by remember { mutableStateOf<SmsMessage?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Dashboard section
        DashboardCards(
            totalSms = messages.size,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Text(
            text = "Mensajes Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (messages.isEmpty()) {
            EmptyMessagesState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Sms,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
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
