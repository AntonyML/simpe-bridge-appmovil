package com.simpe.bridge.appmovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Sans = FontFamily.Default

private fun t(
    weight: FontWeight,
    size: Int,
    line: Int,
    letter: Double = 0.0,
) = TextStyle(
    fontFamily = Sans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = letter.sp,
)

val AppTypography = Typography(
    displayLarge   = t(FontWeight.Bold,     40, 48, -0.5),
    displayMedium  = t(FontWeight.Bold,     32, 40, -0.4),
    displaySmall   = t(FontWeight.SemiBold, 28, 34, -0.3),

    headlineLarge  = t(FontWeight.Bold,     26, 32, -0.2),
    headlineMedium = t(FontWeight.Bold,     24, 30, -0.2),
    headlineSmall  = t(FontWeight.SemiBold, 22, 28, -0.1),

    titleLarge     = t(FontWeight.SemiBold, 20, 26, 0.0),
    titleMedium    = t(FontWeight.SemiBold, 17, 24, 0.1),
    titleSmall     = t(FontWeight.Medium,   15, 20, 0.1),

    bodyLarge      = t(FontWeight.Normal,   17, 26, 0.15),
    bodyMedium     = t(FontWeight.Normal,   15, 22, 0.2),
    bodySmall      = t(FontWeight.Normal,   14, 20, 0.25),

    labelLarge     = t(FontWeight.SemiBold, 15, 20, 0.3),
    labelMedium    = t(FontWeight.Medium,   13, 18, 0.4),
    labelSmall     = t(FontWeight.Medium,   12, 16, 0.5),
)

fun scaledTypography(scale: Float): Typography {
    fun s(style: TextStyle): TextStyle = style.copy(fontSize = style.fontSize * scale, lineHeight = style.lineHeight * scale)
    return Typography(
        displayLarge   = s(AppTypography.displayLarge),
        displayMedium  = s(AppTypography.displayMedium),
        displaySmall   = s(AppTypography.displaySmall),
        headlineLarge  = s(AppTypography.headlineLarge),
        headlineMedium = s(AppTypography.headlineMedium),
        headlineSmall  = s(AppTypography.headlineSmall),
        titleLarge     = s(AppTypography.titleLarge),
        titleMedium    = s(AppTypography.titleMedium),
        titleSmall     = s(AppTypography.titleSmall),
        bodyLarge      = s(AppTypography.bodyLarge),
        bodyMedium     = s(AppTypography.bodyMedium),
        bodySmall      = s(AppTypography.bodySmall),
        labelLarge     = s(AppTypography.labelLarge),
        labelMedium    = s(AppTypography.labelMedium),
        labelSmall     = s(AppTypography.labelSmall),
    )
}
