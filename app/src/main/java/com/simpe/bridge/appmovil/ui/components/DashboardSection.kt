package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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

// ── Hero header — premium "first thing you see" panel ────────────────────────
@Composable
fun DashboardHeroSection(
    totalSms: Int,
    onTestSms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.18f),
                            primary.copy(alpha = 0.04f),
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Dashboard,
                            contentDescription = null,
                            tint = onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Panel principal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Bridge local activo",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusPill(label = "En línea", tone = MetricTone.Positive)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = totalSms.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Text(
                            text = "Mensajes recibidos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        onClick = onTestSms,
                        shape = RoundedCornerShape(999.dp),
                        color = primary,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Test",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Summary section ────────────────────────────────────────────────────────────
@Composable
fun SummarySection(
    totalSms: Int,
    modifier: Modifier = Modifier,
) {
    DashboardSectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricItem(
                    label = "Total SMS",
                    value = totalSms.toString(),
                    icon = Icons.Rounded.Sms,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Spam",
                    value = "0",
                    icon = Icons.Rounded.CheckCircle,
                    valueTone = MetricTone.Positive,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ── System status section ──────────────────────────────────────────────────────
@Composable
fun SystemStatusSection(modifier: Modifier = Modifier) {
    DashboardSectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Android,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sistema",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bridge local",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusPill(label = "En línea", tone = MetricTone.Positive)
            }

            HairlineDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricItem(
                    label = "Riesgo",
                    value = "Bajo",
                    icon = Icons.Rounded.Security,
                    valueTone = MetricTone.Positive,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Plataforma",
                    value = "Android",
                    icon = Icons.Rounded.Android,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ── Distribution / usage bar section ───────────────────────────────────────────
@Composable
fun UsageDistributionSection(
    totalSms: Int,
    modifier: Modifier = Modifier,
) {
    val safeCount = totalSms.coerceAtLeast(0)
    val spamCount = 0
    val sinpeCount = safeCount
    val sinpePct = if (safeCount == 0) 0f else (sinpeCount.toFloat() / safeCount.toFloat())
    val spamPct  = if (safeCount == 0) 0f else (spamCount.toFloat()  / safeCount.toFloat())

    DashboardSectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Distribución",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DistributionRow(
                label = "SINPE",
                count = sinpeCount,
                progress = sinpePct,
                tone = MetricTone.Positive
            )
            DistributionRow(
                label = "Spam",
                count = spamCount,
                progress = spamPct,
                tone = MetricTone.Warning
            )
        }
    }
}

@Composable
private fun DistributionRow(
    label: String,
    count: Int,
    progress: Float,
    tone: MetricTone,
) {
    val color = when (tone) {
        MetricTone.Positive -> MaterialTheme.colorScheme.primary
        MetricTone.Warning  -> MaterialTheme.colorScheme.tertiary
        MetricTone.Negative -> MaterialTheme.colorScheme.error
        MetricTone.Neutral  -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

// ── Quick actions section — premium entry points ──────────────────────────────
@Composable
fun QuickActionsSection(
    onTestSms: () -> Unit,
    onOpenScan: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardSectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionTile(
                    label = "Test SMS",
                    icon = Icons.Rounded.Bolt,
                    tone = MetricTone.Positive,
                    modifier = Modifier.weight(1f),
                    onClick = onTestSms
                )
                QuickActionTile(
                    label = "Escanear",
                    icon = Icons.Rounded.PhotoCamera,
                    tone = MetricTone.Positive,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenScan
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionTile(
                    label = "Apariencia",
                    icon = Icons.Rounded.Palette,
                    tone = MetricTone.Neutral,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAppearance
                )
                QuickActionTile(
                    label = "Ajustes",
                    icon = Icons.Rounded.Settings,
                    tone = MetricTone.Neutral,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSettings
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    tone: MetricTone = MetricTone.Neutral,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val tint = when (tone) {
        MetricTone.Positive -> MaterialTheme.colorScheme.primary
        MetricTone.Warning  -> MaterialTheme.colorScheme.tertiary
        MetricTone.Negative -> MaterialTheme.colorScheme.error
        MetricTone.Neutral  -> MaterialTheme.colorScheme.onSurface
    }
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
