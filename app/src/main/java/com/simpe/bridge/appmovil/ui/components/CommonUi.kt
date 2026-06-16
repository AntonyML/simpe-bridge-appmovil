package com.simpe.bridge.appmovil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simpe.bridge.appmovil.ui.preferences.GlassIntensity
import com.simpe.bridge.appmovil.ui.theme.glassTokens
import com.simpe.bridge.appmovil.ui.theme.HazeGrey

// ── Design tokens — fixed by Air Mobile style guide ────────────────────────────
internal val AirCardShape      = RoundedCornerShape(20.dp)
internal val AirCardShapeSmall = RoundedCornerShape(14.dp)
internal val AirInputShape     = RoundedCornerShape(10.dp)
internal val AirCardPadding    = 20.dp
internal val AirScreenPadding  = 16.dp
internal val AirSectionSpacing = 28.dp
internal val AirGlassShape     = RoundedCornerShape(24.dp)

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
            .padding(top = 4.dp, bottom = 8.dp),
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

// ── Section card — surface with hairline border (layered look) ────────────────
@Composable
fun DashboardSectionCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
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

// ── Glass card — translucent premium surface with real layered translucency ────
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    intensity: GlassIntensity = GlassIntensity.Balanced,
    content: @Composable ColumnScope.() -> Unit,
) {
    val tokens = glassTokens()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AirGlassShape,
        color = tokens.fill.copy(alpha = intensity.alpha),
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
                            tokens.highlight,
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
                    modifier = Modifier.size(16.dp),
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
        color = tint.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
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

// ── Premium clickable card wrapper with proper pressed state ──────────────────
@Composable
fun PremiumCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: Dp = AirCardPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    val baseModifier = if (onClick != null) modifier.clip(AirCardShape) else modifier
    Surface(
        modifier = baseModifier.fillMaxWidth(),
        shape = AirCardShape,
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick ?: {},
        enabled = onClick != null,
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
