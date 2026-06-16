package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
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

    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

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
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(modifier = Modifier.fillMaxHeight()) {
            // Full-bleed glass background that extends edge-to-edge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(340.dp)
                    .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                    .background(tokens.fill)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(340.dp)
                        .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    tokens.highlight,
                                    Color.Transparent,
                                    Color.Transparent,
                                )
                            )
                        )
                )
            }

            // Right edge highlight (1dp border-like)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .align(Alignment.CenterEnd)
                    .background(tokens.borderBright)
            )

            // Content column with safe insets
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(340.dp)
                    .padding(top = statusBarTop, bottom = navBarBottom)
            ) {
                DrawerHeader(
                    primary = primary,
                    onSurface = onSurface,
                    onSurfaceVariant = onSurfaceVariant,
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(12.dp))
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
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                DrawerLogout(onClick = onLogout)
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    primary: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                primary,
                                primary.copy(alpha = 0.65f),
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Android,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "SIMPE Bridge",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onSurface
                )
                Text(
                    text = "v1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariant
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(999.dp),
            color = primary.copy(alpha = 0.12f),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(primary)
                )
                Text(
                    text = "Solo Android · activo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = primary
                )
            }
        }
    }
}

@Composable
private fun DrawerSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
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
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
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
