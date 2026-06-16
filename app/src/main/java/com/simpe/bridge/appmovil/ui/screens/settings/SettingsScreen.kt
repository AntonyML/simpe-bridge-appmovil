package com.simpe.bridge.appmovil.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.simpe.bridge.appmovil.domain.utils.SettingsManager
import com.simpe.bridge.appmovil.ui.components.AirCardPadding
import com.simpe.bridge.appmovil.ui.components.AirCardShape
import com.simpe.bridge.appmovil.ui.components.AirScreenPadding
import com.simpe.bridge.appmovil.ui.components.AirSectionSpacing

@Composable
fun SettingsScreen(
    isListenerEnabled: Boolean,
    onListenerToggle: (Boolean) -> Unit,
    hasSmsPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    hasCameraPermission: Boolean,
    onRequestCameraPermission: () -> Unit,
    onLogout: () -> Unit,
    onOpenAppearance: () -> Unit,
    themeLabel: String,
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    var showSendersDialog by remember { mutableStateOf(false) }
    var senders by remember { mutableStateOf(settingsManager.getSinpeSenders()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AirScreenPadding, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(AirSectionSpacing)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        SettingsSection(title = "Apariencia") {
            SettingsActionItem(
                title = "Tema y estilo",
                description = themeLabel,
                icon = Icons.Rounded.Palette,
                onClick = onOpenAppearance
            )
        }

        SettingsSection(title = "General") {
            SettingsToggleItem(
                title = "Listener de SMS",
                description = "Capturar mensajes entrantes automáticamente.",
                icon = Icons.Rounded.Sms,
                checked = isListenerEnabled,
                onCheckedChange = onListenerToggle
            )
        }

        SettingsSection(title = "Remitentes SINPE") {
            SettingsSendersItem(
                senders = senders,
                onClick = { showSendersDialog = true }
            )
        }

        SettingsSection(title = "Permisos") {
            SettingsPermissionItem(
                title = "Lectura de SMS",
                status = if (hasSmsPermissions) "Concedido" else "Requerido",
                icon = Icons.Rounded.Lock,
                isGranted = hasSmsPermissions,
                onClick = onRequestPermissions
            )
            SettingsPermissionItem(
                title = "Cámara",
                status = if (hasCameraPermission) "Concedido" else "Requerido para escanear",
                icon = Icons.Rounded.PhotoCamera,
                isGranted = hasCameraPermission,
                onClick = onRequestCameraPermission
            )
        }

        SettingsSection(title = "Información") {
            SettingsInfoItem(
                title = "Versión",
                value = "1.0.0 (Build 2026)",
                icon = Icons.Rounded.Info
            )
            SettingsInfoItem(
                title = "Estado del Bridge",
                value = "Solo Android",
                icon = Icons.Rounded.CellTower
            )
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = AirCardShape
        ) {
            Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }

    if (showSendersDialog) {
        SendersDialog(
            senders = senders,
            onDismiss = { showSendersDialog = false },
            onAddSender = { newSender ->
                settingsManager.addSinpeSender(newSender)
                senders = settingsManager.getSinpeSenders()
            },
            onRemoveSender = { sender ->
                settingsManager.removeSinpeSender(sender)
                senders = settingsManager.getSinpeSenders()
            },
            onReset = {
                settingsManager.resetToDefaults()
                senders = settingsManager.getSinpeSenders()
            }
        )
    }
}

@Composable
fun SettingsSendersItem(
    senders: Set<String>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AirCardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.ContactPhone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Remitentes esperados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${senders.size} configurados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Rounded.Edit,
                contentDescription = "Editar",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SendersDialog(
    senders: Set<String>,
    onDismiss: () -> Unit,
    onAddSender: (String) -> Unit,
    onRemoveSender: (String) -> Unit,
    onReset: () -> Unit
) {
    var newSender by remember { mutableStateOf("") }
    var showAddField by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = AirCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(AirCardPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Remitentes SINPE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Números o palabras clave que identifican mensajes SINPE.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(senders.toList().sorted()) { sender ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sender,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(
                                onClick = { onRemoveSender(sender) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                if (showAddField) {
                    OutlinedTextField(
                        value = newSender,
                        onValueChange = { newSender = it },
                        label = { Text("Nuevo remitente") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddField = false }) { Text("Cancelar") }
                        TextButton(
                            onClick = {
                                if (newSender.isNotBlank()) {
                                    onAddSender(newSender)
                                    newSender = ""
                                    showAddField = false
                                }
                            }
                        ) { Text("Agregar") }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddField = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar")
                        }
                        OutlinedButton(onClick = onReset) { Text("Reset") }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = AirCardShape,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column { content() }
        }
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(AirCardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AirCardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsPermissionItem(
    title: String,
    status: String,
    icon: ImageVector,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AirCardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
        if (!isGranted) {
            TextButton(onClick = onClick) { Text("Habilitar") }
        }
    }
}

@Composable
fun SettingsInfoItem(
    title: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AirCardPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
