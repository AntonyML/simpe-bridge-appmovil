package com.simpe.bridge.appmovil.ui.theme

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView

// ── Air Design System Palette ──────────────────────────────────────────────────
internal val SkyCanvas    = Color(0xFF426188)
internal val ActionBlue   = Color(0xFF2B7FFF)
internal val CharcoalText = Color(0xFF1B1B1B)
internal val HazeGrey     = Color(0xFFF5F5F5)
internal val CloudWhite   = Color(0xFFFFFFFF)

// ── Light mode tokens ───────────────────────────────────────────────────────────
private val ActionBlueContainerLight   = Color(0xFFE4EFFF)
private val OnActionBlueContainerLight = Color(0xFF002F6E)
private val SkyCanvasContainerLight    = Color(0xFFDDE7F5)
private val OnSkyCanvasContainerLight  = Color(0xFF1A3557)
private val OutlineLight               = Color(0x1F000000)
private val OutlineVariantLight        = Color(0x0F000000)
private val OnSurfaceVariantLight      = Color(0xFF555B66)
private val SurfaceTintLight           = Color(0xFFFAFBFD)
private val SurfaceContainerLight      = Color(0xFFF1F3F7)
private val SurfaceContainerHighLight  = Color(0xFFE9ECF1)
private val SurfaceContainerHighestLight = Color(0xFFE2E6EC)

// ── Light glass tokens — translucent, light-on-light ──────────────────────────
private val GlassLightFill          = Color(0xF2FFFFFF)
private val GlassLightHighlight     = Color(0x99FFFFFF)
private val GlassLightBorder        = Color(0x33000000)
private val GlassLightBorderBright  = Color(0x66FFFFFF)
private val GlassLightScrim         = Color(0xFFFFFF)

// ── Dark mode surface stack — refined, no pure black ──────────────────────────
private val DarkBackground              = Color(0xFF0F1218)
private val DarkSurface                 = Color(0xFF1A1F28)
private val DarkSurfaceVariant          = Color(0xFF232932)
private val DarkSurfaceContainer        = Color(0xFF2A313C)
private val DarkSurfaceContainerHigh    = Color(0xFF323947)
private val DarkSurfaceContainerHighest = Color(0xFF3C4452)
private val DarkOnSurface               = Color(0xFFEDF0F5)
private val DarkOnSurfaceVariant        = Color(0xFFB0B8C4)
private val ActionBlueDark              = Color(0xFF7AAEFF)
private val OnActionBlueDark            = Color(0xFF002F6E)
private val ActionBlueContainerDark     = Color(0xFF2C4F88)
private val OnActionBlueContainerDark   = Color(0xFFD6E8FF)
private val SkyCanvasContainerDark      = Color(0xFF3A4B62)
private val OnSkyCanvasContainerDark    = Color(0xFFD4E5F7)
private val OutlineDark                 = Color(0x33FFFFFF)
private val OutlineVariantDark          = Color(0x22FFFFFF)

// ── Dark glass tokens — translucent dark with visible depth ──────────────────
private val GlassDarkFill         = Color(0xE61A1F28)
private val GlassDarkHighlight    = Color(0x1AFFFFFF)
private val GlassDarkBorder       = Color(0x33FFFFFF)
private val GlassDarkBorderBright = Color(0x55FFFFFF)
private val GlassDarkScrim        = Color(0xFF000000)

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
    scrim                = Color(0xB3000000),
)

// ── Glass tokens — exported for premium surfaces ──────────────────────────────
data class GlassTokens(
    val fill: Color,
    val highlight: Color,
    val border: Color,
    val borderBright: Color,
    val scrim: Color,
)

internal val LightGlass = GlassTokens(
    fill = GlassLightFill,
    highlight = GlassLightHighlight,
    border = GlassLightBorder,
    borderBright = GlassLightBorderBright,
    scrim = GlassLightScrim,
)

internal val DarkGlass = GlassTokens(
    fill = GlassDarkFill,
    highlight = GlassDarkHighlight,
    border = GlassDarkBorder,
    borderBright = GlassDarkBorderBright,
    scrim = GlassDarkScrim,
)

@Composable
fun glassTokens(): GlassTokens =
    if (isSystemInDarkTheme()) DarkGlass else LightGlass

@Composable
fun isAppDark(): Boolean = isSystemInDarkTheme()

// ── Theme ──────────────────────────────────────────────────────────────────────
@Composable
fun SimpeBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode && view.context is ComponentActivity) {
        SideEffect {
            val activity = view.context as ComponentActivity
            activity.enableEdgeToEdge(
                statusBarStyle = if (darkTheme) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                                 else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
                navigationBarStyle = if (darkTheme) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                                     else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = if (fontScale == 1.0f) AppTypography else scaledTypography(fontScale),
        content = content
    )
}
