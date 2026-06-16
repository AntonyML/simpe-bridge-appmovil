package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simpe.bridge.appmovil.ui.theme.HazeGrey

// ── Summary section ────────────────────────────────────────────────────────────
@Composable
fun SummarySection(
    totalSms: Int,
    modifier: Modifier = Modifier,
) {
    DashboardSectionCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Android,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
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
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = HazeGrey
        )
    }
}
