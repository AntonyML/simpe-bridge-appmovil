package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.simpe.bridge.appmovil.ui.preferences.GlassIntensity
import com.simpe.bridge.appmovil.ui.theme.glassTokens
import com.simpe.bridge.appmovil.ui.theme.HazeGrey

// ── Design tokens — fixed by Air Mobile style guide ────────────────────────────
internal val AirCardShape      = RoundedCornerShape(14.dp)
internal val AirInputShape     = RoundedCornerShape(4.dp)
internal val AirCardPadding    = 20.dp
internal val AirScreenPadding  = 16.dp
internal val AirSectionSpacing = 48.dp
internal val AirGlassShape     = RoundedCornerShape(20.dp)

// ── Section title — reusable across all dashboard sections ────────────────────
@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (action != null) {
            action()
        }
    }
}

// ── Section card — solid surface (non-glass) ──────────────────────────────────
@Composable
fun DashboardSectionCard(
    modifier: Modifier = Modifier,
    containerColor: Color = HazeGrey,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AirCardShape,
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(AirCardPadding)) {
            content()
        }
    }
}

// ── Glass card — translucent premium surface with subtle gradient + border ───
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Balanced,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tokens = glassTokens()
    val baseSurface = tokens.surface

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AirGlassShape,
        color = baseSurface.copy(alpha = intensity.alpha),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, tokens.border),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent,
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(AirCardPadding)) {
                content()
            }
        }
    }
}

// ── Soft section card — surface with hairline border for layered look ────────
@Composable
fun SoftSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AirCardShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(AirCardPadding)) {
            content()
        }
    }
}

// ── Metric item — small label + large value with optional icon ────────────────
@Composable
fun MetricItem(
    label: String,
    value: String,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    valueTone: MetricTone = MetricTone.Neutral,
    modifier: Modifier = Modifier,
) {
    val valueColor = when (valueTone) {
        MetricTone.Positive -> MaterialTheme.colorScheme.primary
        MetricTone.Warning  -> MaterialTheme.colorScheme.tertiary
        MetricTone.Negative -> MaterialTheme.colorScheme.error
        MetricTone.Neutral  -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

enum class MetricTone { Neutral, Positive, Warning, Negative }

// ── Status pill — small inline indicator (used inside metric items) ───────────
@Composable
fun StatusPill(
    label: String,
    tone: MetricTone = MetricTone.Positive,
    modifier: Modifier = Modifier,
) {
    val tint = when (tone) {
        MetricTone.Positive -> MaterialTheme.colorScheme.primary
        MetricTone.Warning  -> MaterialTheme.colorScheme.tertiary
        MetricTone.Negative -> MaterialTheme.colorScheme.error
        MetricTone.Neutral  -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = tint.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(tint)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = tint
            )
        }
    }
}

// ── Divider — visual separator between metric items inside a section card ─────
@Composable
fun HairlineDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
