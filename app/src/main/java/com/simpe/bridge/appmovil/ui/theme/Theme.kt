package com.simpe.bridge.appmovil.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Air Design System Palette ──────────────────────────────────────────────────
internal val SkyCanvas    = Color(0xFF426188)
internal val ActionBlue   = Color(0xFF2B7FFF)
internal val CharcoalText = Color(0xFF1B1B1B)
internal val HazeGrey     = Color(0xFFF5F5F5)
internal val CloudWhite   = Color(0xFFFFFFFF)

// ── Light mode derived tokens ──────────────────────────────────────────────────
private val ActionBlueContainerLight  = Color(0xFFE4EFFF)
private val OnActionBlueContainerLight = Color(0xFF002F6E)
private val SkyCanvasContainerLight   = Color(0xFFDDE7F5)
private val OnSkyCanvasContainerLight = Color(0xFF1A3557)
private val OutlineLight              = Color(0x14000000)
private val OutlineVariantLight       = Color(0x0A000000)
private val OnSurfaceVariantLight     = Color(0xFF595E66)
private val SurfaceTintLight          = Color(0xFFFAFBFD)
private val SurfaceContainerLight     = Color(0xFFF2F4F7)
private val SurfaceContainerHighLight = Color(0xFFEDEFF3)
private val SurfaceContainerHighestLight = Color(0xFFE7EAEF)

private val GlassLightFill       = Color(0xCCFFFFFF)
private val GlassLightHighlight  = Color(0x66FFFFFF)
private val GlassLightBorder     = Color(0x33000000)
private val GlassLightScrim      = Color(0x66FFFFFF)

// ── Dark mode surface stack — softer, layered, no pure black ───────────────────
private val DarkBackground            = Color(0xFF13171F)
private val DarkSurface               = Color(0xFF1B2029)
private val DarkSurfaceVariant        = Color(0xFF222934)
private val DarkSurfaceContainer      = Color(0xFF252C37)
private val DarkSurfaceContainerHigh  = Color(0xFF2C343F)
private val DarkSurfaceContainerHighest = Color(0xFF353D49)
private val DarkOnSurface             = Color(0xFFE6EAF1)
private val DarkOnSurfaceVariant      = Color(0xFFA6B0BD)
private val ActionBlueDark            = Color(0xFF6FA1FF)
private val OnActionBlueDark          = Color(0xFF002F6E)
private val ActionBlueContainerDark   = Color(0xFF254A82)
private val OnActionBlueContainerDark = Color(0xFFD6E8FF)
private val SkyCanvasContainerDark    = Color(0xFF34465E)
private val OnSkyCanvasContainerDark  = Color(0xFFD4E5F7)
private val OutlineDark               = Color(0x33FFFFFF)
private val OutlineVariantDark        = Color(0x1FFFFFFF)

private val GlassDarkFill       = Color(0xB31B2029)
private val GlassDarkHighlight  = Color(0x33FFFFFF)
private val GlassDarkBorder     = Color(0x4DFFFFFF)
private val GlassDarkScrim      = Color(0x80000000)

// ── Light scheme ──────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = ActionBlue,
    onPrimary            = CloudWhite,
    primaryContainer     = ActionBlueContainerLight,
    onPrimaryContainer   = OnActionBlueContainerLight,
    secondary            = SkyCanvas,
    onSecondary          = CloudWhite,
    secondaryContainer   = SkyCanvasContainerLight,
    onSecondaryContainer = OnSkyCanvasContainerLight,
    tertiary             = Color(0xFF6E90B0),
    onTertiary           = CloudWhite,
    tertiaryContainer    = Color(0xFFEAF0FA),
    onTertiaryContainer  = Color(0xFF264265),
    error                = Color(0xFFBA1A1A),
    onError              = CloudWhite,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),
    background           = SurfaceTintLight,
    onBackground         = CharcoalText,
    surface              = CloudWhite,
    onSurface            = CharcoalText,
    surfaceVariant       = HazeGrey,
    surfaceContainer     = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    onSurfaceVariant     = OnSurfaceVariantLight,
    outline              = OutlineLight,
    outlineVariant       = OutlineVariantLight,
    inverseSurface       = CharcoalText,
    inverseOnSurface     = CloudWhite,
    inversePrimary       = Color(0xFF9ECCFF),
    surfaceTint          = ActionBlue,
    scrim                = Color(0x99000000),
)

// ── Dark scheme ───────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = ActionBlueDark,
    onPrimary            = CloudWhite,
    primaryContainer     = ActionBlueContainerDark,
    onPrimaryContainer   = OnActionBlueContainerDark,
    secondary            = Color(0xFFA0B7CE),
    onSecondary          = Color(0xFF1A3557),
    secondaryContainer   = SkyCanvasContainerDark,
    onSecondaryContainer = OnSkyCanvasContainerDark,
    tertiary             = Color(0xFFA6C4DE),
    onTertiary           = Color(0xFF1A3857),
    tertiaryContainer    = Color(0xFF2A4D72),
    onTertiaryContainer  = Color(0xFFCDE4F8),
    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),
    background           = DarkBackground,
    onBackground         = DarkOnSurface,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceVariant,
    surfaceContainer     = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    onSurfaceVariant     = DarkOnSurfaceVariant,
    outline              = OutlineDark,
    outlineVariant       = OutlineVariantDark,
    inverseSurface       = DarkOnSurface,
    inverseOnSurface     = DarkBackground,
    inversePrimary       = ActionBlue,
    surfaceTint          = ActionBlueDark,
    scrim                = Color(0xAA000000),
)

// ── Glass tokens — exported for premium surfaces ──────────────────────────────
data class GlassTokens(
    val fill: Color,
    val highlight: Color,
    val border: Color,
    val scrim: Color,
)

internal val LightGlass = GlassTokens(
    fill = GlassLightFill,
    highlight = GlassLightHighlight,
    border = GlassLightBorder,
    scrim = GlassLightScrim,
)

internal val DarkGlass = GlassTokens(
    fill = GlassDarkFill,
    highlight = GlassDarkHighlight,
    border = GlassDarkBorder,
    scrim = GlassDarkScrim,
)

@Composable
fun glassTokens(): GlassTokens =
    if (isSystemInDarkTheme()) DarkGlass else LightGlass

// ── Theme ──────────────────────────────────────────────────────────────────────
@Composable
fun SimpeBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (fontScale == 1.0f) AppTypography else scaledTypography(fontScale),
        content = content
    )
}
