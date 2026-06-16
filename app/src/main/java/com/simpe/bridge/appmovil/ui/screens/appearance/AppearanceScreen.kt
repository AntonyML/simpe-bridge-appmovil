package com.simpe.bridge.appmovil.ui.screens.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DensityLarge
import androidx.compose.material.icons.rounded.DensityMedium
import androidx.compose.material.icons.rounded.DensitySmall
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simpe.bridge.appmovil.ui.components.AirCardPadding
import com.simpe.bridge.appmovil.ui.components.AirCardShape
import com.simpe.bridge.appmovil.ui.components.AirScreenPadding
import com.simpe.bridge.appmovil.ui.components.AirSectionSpacing
import com.simpe.bridge.appmovil.ui.components.GlassCard
import com.simpe.bridge.appmovil.ui.components.HairlineDivider
import com.simpe.bridge.appmovil.ui.components.MetricTone
import com.simpe.bridge.appmovil.ui.components.SectionTitle
import com.simpe.bridge.appmovil.ui.components.StatusPill
import com.simpe.bridge.appmovil.ui.preferences.AccentIntensity
import com.simpe.bridge.appmovil.ui.preferences.FontScale
import com.simpe.bridge.appmovil.ui.preferences.GlassIntensity
import com.simpe.bridge.appmovil.ui.preferences.ThemeMode
import com.simpe.bridge.appmovil.ui.preferences.UiPreferences
import com.simpe.bridge.appmovil.ui.preferences.VisualDensity

@Composable
fun AppearanceScreen(
    prefs: UiPreferences,
    onChange: (UiPreferences) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AirScreenPadding, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(AirSectionSpacing)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Personaliza el aspecto visual de la app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SectionTitle(title = "Vista previa", subtitle = "Cómo se ve tu app")
        GlassCard {
            AppearancePreview(prefs = prefs)
        }

        SectionTitle(title = "Tema")
        AppearanceSectionCard {
            Column {
                ThemeOptionRow(
                    label = "Seguir sistema",
                    description = "Se adapta al ajuste del dispositivo",
                    icon = Icons.Rounded.Brightness6,
                    selected = prefs.themeMode == ThemeMode.System,
                    onClick = { onChange(prefs.copy(themeMode = ThemeMode.System)) }
                )
                HairlineDivider()
                ThemeOptionRow(
                    label = "Claro",
                    description = "Fondo brillante, alta legibilidad",
                    icon = Icons.Rounded.LightMode,
                    selected = prefs.themeMode == ThemeMode.Light,
                    onClick = { onChange(prefs.copy(themeMode = ThemeMode.Light)) }
                )
                HairlineDivider()
                ThemeOptionRow(
                    label = "Oscuro",
                    description = "Superficies suaves y profundas",
                    icon = Icons.Rounded.DarkMode,
                    selected = prefs.themeMode == ThemeMode.Dark,
                    onClick = { onChange(prefs.copy(themeMode = ThemeMode.Dark)) }
                )
            }
        }

        SectionTitle(title = "Tamaño de fuente", subtitle = "Escala la tipografía global")
        AppearanceSectionCard {
            Column {
                FontScaleOptionRow(
                    label = "Normal",
                    description = "Tipografía estándar",
                    selected = prefs.fontScale == FontScale.Normal,
                    onClick = { onChange(prefs.copy(fontScale = FontScale.Normal)) }
                )
                HairlineDivider()
                FontScaleOptionRow(
                    label = "Grande",
                    description = "Más legible, contenido cómodo",
                    selected = prefs.fontScale == FontScale.Large,
                    onClick = { onChange(prefs.copy(fontScale = FontScale.Large)) }
                )
                HairlineDivider()
                FontScaleOptionRow(
                    label = "Muy grande",
                    description = "Para sesiones de lectura largas",
                    selected = prefs.fontScale == FontScale.ExtraLarge,
                    onClick = { onChange(prefs.copy(fontScale = FontScale.ExtraLarge)) }
                )
                HairlineDivider()
                FontScaleOptionRow(
                    label = "Extra grande",
                    description = "Máxima legibilidad",
                    selected = prefs.fontScale == FontScale.Huge,
                    onClick = { onChange(prefs.copy(fontScale = FontScale.Huge)) }
                )
            }
        }

        SectionTitle(title = "Densidad de interfaz")
        AppearanceSectionCard {
            Column {
                DensityOptionRow(
                    label = "Compacta",
                    description = "Más contenido visible",
                    icon = Icons.Rounded.DensitySmall,
                    selected = prefs.density == VisualDensity.Compact,
                    onClick = { onChange(prefs.copy(density = VisualDensity.Compact)) }
                )
                HairlineDivider()
                DensityOptionRow(
                    label = "Cómoda",
                    description = "Equilibrio entre aire y contenido",
                    icon = Icons.Rounded.DensityMedium,
                    selected = prefs.density == VisualDensity.Comfortable,
                    onClick = { onChange(prefs.copy(density = VisualDensity.Comfortable)) }
                )
                HairlineDivider()
                DensityOptionRow(
                    label = "Espaciosa",
                    description = "Más aire, lectura relajada",
                    icon = Icons.Rounded.DensityLarge,
                    selected = prefs.density == VisualDensity.Spacious,
                    onClick = { onChange(prefs.copy(density = VisualDensity.Spacious)) }
                )
            }
        }

        SectionTitle(title = "Intensidad del vidrio")
        AppearanceSectionCard {
            Column {
                GlassOptionRow(
                    label = "Sutil",
                    description = "Más opaco, mayor legibilidad",
                    selected = prefs.glass == GlassIntensity.Subtle,
                    onClick = { onChange(prefs.copy(glass = GlassIntensity.Subtle)) }
                )
                HairlineDivider()
                GlassOptionRow(
                    label = "Equilibrado",
                    description = "Balance entre profundidad y lectura",
                    selected = prefs.glass == GlassIntensity.Balanced,
                    onClick = { onChange(prefs.copy(glass = GlassIntensity.Balanced)) }
                )
                HairlineDivider()
                GlassOptionRow(
                    label = "Pronunciado",
                    description = "Más translúcido, sensación premium",
                    selected = prefs.glass == GlassIntensity.Pronounced,
                    onClick = { onChange(prefs.copy(glass = GlassIntensity.Pronounced)) }
                )
            }
        }

        SectionTitle(title = "Intensidad del acento")
        AppearanceSectionCard {
            Column {
                AccentOptionRow(
                    label = "Calmado",
                    description = "Acento azul más suave",
                    selected = prefs.accent == AccentIntensity.Calm,
                    onClick = { onChange(prefs.copy(accent = AccentIntensity.Calm)) }
                )
                HairlineDivider()
                AccentOptionRow(
                    label = "Equilibrado",
                    description = "Color principal estándar",
                    selected = prefs.accent == AccentIntensity.Balanced,
                    onClick = { onChange(prefs.copy(accent = AccentIntensity.Balanced)) }
                )
                HairlineDivider()
                AccentOptionRow(
                    label = "Vibrante",
                    description = "Acento más saturado",
                    selected = prefs.accent == AccentIntensity.Vibrant,
                    onClick = { onChange(prefs.copy(accent = AccentIntensity.Vibrant)) }
                )
            }
        }

        SectionTitle(title = "Secciones del panel")
        AppearanceSectionCard {
            Column {
                ToggleRow(
                    label = "Panel principal (hero)",
                    description = "Resumen rápido arriba de todo",
                    checked = prefs.showHero,
                    onCheckedChange = { onChange(prefs.copy(showHero = it)) }
                )
                HairlineDivider()
                ToggleRow(
                    label = "Mostrar distribución",
                    description = "Barras de SINPE y spam",
                    checked = prefs.showDistribution,
                    onCheckedChange = { onChange(prefs.copy(showDistribution = it)) }
                )
                HairlineDivider()
                ToggleRow(
                    label = "Mostrar estado del sistema",
                    description = "Bloque de sistema y riesgo",
                    checked = prefs.showSystemStatus,
                    onCheckedChange = { onChange(prefs.copy(showSystemStatus = it)) }
                )
                HairlineDivider()
                ToggleRow(
                    label = "Mostrar acciones rápidas",
                    description = "Botones Test SMS, Escanear, etc.",
                    checked = prefs.showQuickActions,
                    onCheckedChange = { onChange(prefs.copy(showQuickActions = it)) }
                )
                HairlineDivider()
                ToggleRow(
                    label = "Mostrar actividad reciente",
                    description = "Lista de mensajes",
                    checked = prefs.showActivityFeed,
                    onCheckedChange = { onChange(prefs.copy(showActivityFeed = it)) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AppearancePreview(prefs: UiPreferences) {
    val scaleText = when (prefs.fontScale) {
        FontScale.Normal -> 15
        FontScale.Large -> 17
        FontScale.ExtraLarge -> 18
        FontScale.Huge -> 20
    }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Panel",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mensajes hoy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "0",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            StatusPill(label = "En línea", tone = MetricTone.Positive)
        }
        Text(
            text = "Texto de ejemplo a ${scaleText}sp según escala.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AppearanceSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = AirCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableRow(label = label, description = description, icon = icon, selected = selected, onClick = onClick)
}

@Composable
private fun FontScaleOptionRow(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableRow(
        label = label,
        description = description,
        icon = Icons.Rounded.FormatSize,
        selected = selected,
        onClick = onClick
    )
}

@Composable
private fun DensityOptionRow(
    label: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableRow(label = label, description = description, icon = icon, selected = selected, onClick = onClick)
}

@Composable
private fun GlassOptionRow(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableRow(
        label = label,
        description = description,
        icon = Icons.Rounded.Opacity,
        selected = selected,
        onClick = onClick
    )
}

@Composable
private fun AccentOptionRow(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SelectableRow(
        label = label,
        description = description,
        icon = Icons.Rounded.Palette,
        selected = selected,
        onClick = onClick
    )
}

@Composable
private fun SelectableRow(
    label: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
                    else MaterialTheme.colorScheme.surfaceContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
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
