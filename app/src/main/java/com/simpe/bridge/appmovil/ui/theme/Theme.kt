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
internal val MidnightInk  = Color(0xFF000000)
internal val CloudWhite   = Color(0xFFFFFFFF)
internal val CharcoalText = Color(0xFF1B1B1B)
internal val HazeGrey     = Color(0xFFF5F5F5)

// ── Light mode derived tokens ──────────────────────────────────────────────────
private val ActionBlueContainerLight  = Color(0xFFE4EFFF)
private val OnActionBlueContainerLight = Color(0xFF002F6E)
private val SkyCanvasContainerLight   = Color(0xFFDDE7F5)
private val OnSkyCanvasContainerLight = Color(0xFF1A3557)
private val OutlineLight              = Color(0x14000000) // rgba(0,0,0,0.08)
private val OutlineVariantLight       = Color(0x0A000000) // rgba(0,0,0,0.04)
private val OnSurfaceVariantLight     = Color(0xFF595E66)
private val SurfaceTintLight          = Color(0xFFFAFBFD)
private val SurfaceContainerLight     = Color(0xFFF2F4F7)
private val GlassLight                = Color(0xCCFFFFFF)
private val GlassBorderLight          = Color(0x1F000000)

// ── Dark mode surface stack — softer, not pure black ──────────────────────────
private val DarkBackground       = Color(0xFF0F1419)
private val DarkSurface          = Color(0xFF161B22)
private val DarkSurfaceVariant   = Color(0xFF1C2128)
private val DarkSurfaceContainer = Color(0xFF1F2630)
private val DarkOnSurface        = Color(0xFFE6EDF3)
private val DarkOnSurfaceVariant = Color(0xFF9DA7B3)
private val ActionBlueDark       = Color(0xFF5C9BFF)
private val OutlineDark          = Color(0x33FFFFFF)
private val OutlineVariantDark   = Color(0x1AFFFFFF)
private val GlassDark            = Color(0xCC161B22)
private val GlassBorderDark      = Color(0x33FFFFFF)

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
    surfaceContainerHigh = Color(0xFFEDEFF3),
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
    onPrimary            = Color(0xFF002F6E),
    primaryContainer     = Color(0xFF1B4A8C),
    onPrimaryContainer   = Color(0xFFD6E8FF),
    secondary            = Color(0xFF8DACC9),
    onSecondary          = Color(0xFF1A3557),
    secondaryContainer   = Color(0xFF2E4F6B),
    onSecondaryContainer = Color(0xFFD4E5F7),
    tertiary             = Color(0xFF9ABFE0),
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
    surfaceContainerHigh = Color(0xFF252C36),
    onSurfaceVariant     = DarkOnSurfaceVariant,
    outline              = OutlineDark,
    outlineVariant       = OutlineVariantDark,
    inverseSurface       = DarkOnSurface,
    inverseOnSurface     = DarkBackground,
    inversePrimary       = ActionBlue,
    surfaceTint          = ActionBlueDark,
    scrim                = Color(0x99000000),
)

// ── Glass tokens — exported for premium surfaces ──────────────────────────────
data class GlassTokens(
    val surface: Color,
    val border: Color,
    val subtleSurface: Color,
)

internal val LightGlass = GlassTokens(
    surface = GlassLight,
    border = GlassBorderLight,
    subtleSurface = Color(0x80FFFFFF),
)

internal val DarkGlass = GlassTokens(
    surface = GlassDark,
    border = GlassBorderDark,
    subtleSurface = Color(0x80161B22),
)

@Composable
fun glassTokens(): GlassTokens =
    if (isSystemInDarkTheme()) DarkGlass else LightGlass

// ── Theme ──────────────────────────────────────────────────────────────────────
@Composable
fun SimpeBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = AppTypography,
        content = content
    )
}
