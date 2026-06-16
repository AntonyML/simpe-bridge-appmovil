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

// ── Derived light tokens ───────────────────────────────────────────────────────
private val ActionBlueContainer   = Color(0xFFD6E8FF)
private val OnActionBlueContainer = Color(0xFF003C8F)
private val SkyCanvasContainer    = Color(0xFFDDE7F5)
private val OnSkyCanvasContainer  = Color(0xFF1A3557)
private val OutlineColor          = Color(0x1A000000) // rgba(0,0,0,0.10)
private val OutlineVariantColor   = Color(0x0D000000) // rgba(0,0,0,0.05)
private val OnSurfaceVariantLight = Color(0xFF5A5A5A)

// ── Dark mode surface stack ────────────────────────────────────────────────────
private val DarkBackground       = Color(0xFF0D1117)
private val DarkSurface          = Color(0xFF161B22)
private val DarkSurfaceVariant   = Color(0xFF1C2128)
private val DarkOnSurface        = Color(0xFFE6EDF3)
private val DarkOnSurfaceVariant = Color(0xFF8B949E)
private val ActionBlueDark       = Color(0xFF4D9BFF)

// ── Light scheme ───────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = ActionBlue,
    onPrimary            = CloudWhite,
    primaryContainer     = ActionBlueContainer,
    onPrimaryContainer   = OnActionBlueContainer,
    secondary            = SkyCanvas,
    onSecondary          = CloudWhite,
    secondaryContainer   = SkyCanvasContainer,
    onSecondaryContainer = OnSkyCanvasContainer,
    tertiary             = Color(0xFF6E90B0),
    onTertiary           = CloudWhite,
    tertiaryContainer    = Color(0xFFEAF0FA),
    onTertiaryContainer  = Color(0xFF264265),
    error                = Color(0xFFBA1A1A),
    onError              = CloudWhite,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),
    background           = CloudWhite,
    onBackground         = CharcoalText,
    surface              = CloudWhite,
    onSurface            = CharcoalText,
    surfaceVariant       = HazeGrey,
    onSurfaceVariant     = OnSurfaceVariantLight,
    outline              = OutlineColor,
    outlineVariant       = OutlineVariantColor,
    inverseSurface       = CharcoalText,
    inverseOnSurface     = CloudWhite,
    inversePrimary       = Color(0xFF9ECCFF),
    surfaceTint          = ActionBlue,
    scrim                = MidnightInk,
)

// ── Dark scheme ────────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = ActionBlueDark,
    onPrimary            = Color(0xFF003070),
    primaryContainer     = Color(0xFF0059C1),
    onPrimaryContainer   = ActionBlueContainer,
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
    onSurfaceVariant     = DarkOnSurfaceVariant,
    outline              = Color(0x33FFFFFF),
    outlineVariant       = Color(0x1AFFFFFF),
    inverseSurface       = DarkOnSurface,
    inverseOnSurface     = DarkBackground,
    inversePrimary       = ActionBlue,
    surfaceTint          = ActionBlueDark,
    scrim                = MidnightInk,
)

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
