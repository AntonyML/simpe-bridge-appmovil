package com.simpe.bridge.appmovil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder
import com.simpe.bridge.appmovil.data.remote.toDto
import com.simpe.bridge.appmovil.domain.usecases.SmsMessage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailModal(
    message: SmsMessage?,
    onDismiss: () -> Unit,
    onCopyText: (String) -> Unit,
    onCopyJson: (String) -> Unit,
    isDebugEnabled: Boolean = false // control global
) {
    if (message == null) return

    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
    }

    val gson = remember {
        GsonBuilder().setPrettyPrinting().create()
    }

    val jsonString = remember(message) {
        gson.toJson(message.toDto())
    }

    var showTechDetails by remember { mutableStateOf(false) }
    var showDebug by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                Text(
                    text = message.payload.sender,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = message.envelope.status)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dateFormatter.format(Date(message.envelope.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Message bubble
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = message.payload.body,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onCopyText(message.payload.body) }) {
                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copiar texto")
                }

                IconButton(onClick = { onCopyJson(jsonString) }) {
                    Icon(Icons.Rounded.DataObject, contentDescription = "Copiar JSON")
                }

                // Botón de debug solo si está habilitado globalmente
                if (isDebugEnabled) {
                    IconButton(onClick = { showDebug = !showDebug }) {
                        Icon(
                            Icons.Rounded.BugReport,
                            contentDescription = "Toggle debug"
                        )
                    }
                }
            }

            // Technical details (nivel medio)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTechDetails = !showTechDetails },
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Detalles técnicos",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = if (showTechDetails)
                                Icons.Rounded.ExpandLess
                            else
                                Icons.Rounded.ExpandMore,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(showTechDetails) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TraceItem("Message ID", message.envelope.messageId)
                            TraceItem("Correlation ID", message.envelope.correlationId)
                            TraceItem("Content Hash", message.envelope.contentHash)
                        }
                    }
                }
            }

            // Debug profundo (nivel oculto)
            if (isDebugEnabled) {
                AnimatedVisibility(showDebug) {

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        Text(
                            text = "Debug",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = jsonString,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TraceItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}