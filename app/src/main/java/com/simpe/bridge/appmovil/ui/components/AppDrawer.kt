package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simpe.bridge.appmovil.ui.theme.glassTokens

sealed class DrawerDestination(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val description: String,
) {
    object Dashboard   : DrawerDestination("dashboard",   "Panel",        Icons.Rounded.Dashboard,   "Resumen y métricas")
    object Mensajes    : DrawerDestination("messages",    "Mensajes",     Icons.Rounded.Message,     "Bandeja de SMS")
    object Tema        : DrawerDestination("appearance",  "Apariencia",   Icons.Rounded.Palette,     "Tema, vidrio, densidad")
    object Ajustes     : DrawerDestination("settings",    "Ajustes",      Icons.Rounded.Settings,    "Permisos y bridge")
}

@Composable
fun AppDrawerContent(
    currentKey: String?,
    onDestination: (DrawerDestination) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = glassTokens()
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val navGroup = listOf(
        DrawerDestination.Dashboard,
        DrawerDestination.Mensajes,
    )
    val configGroup = listOf(
        DrawerDestination.Tema,
        DrawerDestination.Ajustes,
    )

    Surface(
        modifier = modifier.fillMaxHeight(),
        color = tokens.fill,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.highlight,
                            Color.Transparent,
                        )
                    )
                )
        ) {
            DrawerHeader(
                primary = primary,
                onSurface = onSurface,
                onSurfaceVariant = onSurfaceVariant,
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(8.dp))
            DrawerSectionLabel("Navegación")
            navGroup.forEach { dest ->
                DrawerItem(
                    destination = dest,
                    selected = dest.key == currentKey,
                    onClick = { onDestination(dest) }
                )
            }

            Spacer(Modifier.height(12.dp))
            DrawerSectionLabel("Personalización")
            configGroup.forEach { dest ->
                DrawerItem(
                    destination = dest,
                    selected = dest.key == currentKey,
                    onClick = { onDestination(dest) }
                )
            }

            Spacer(Modifier.weight(1f))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            DrawerLogout(onClick = onLogout)
        }
    }
}

@Composable
private fun DrawerHeader(
    primary: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(primary, primary.copy(alpha = 0.7f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Android,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Column {
            Text(
                text = "SIMPE Bridge",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
            Text(
                text = "v1.0.0 · Solo Android",
                style = MaterialTheme.typography.labelMedium,
                color = onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun DrawerItem(
    destination: DrawerDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
             else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                    else MaterialTheme.colorScheme.surfaceContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = destination.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = destination.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerLogout(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
