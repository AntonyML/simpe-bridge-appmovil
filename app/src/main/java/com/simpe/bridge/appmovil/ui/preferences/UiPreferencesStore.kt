package com.simpe.bridge.appmovil.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { System, Light, Dark }

enum class VisualDensity(val scale: Float) {
    Compact(0.94f),
    Comfortable(1.0f),
    Spacious(1.08f),
}

enum class FontScale(val scale: Float, val label: String) {
    Normal(1.0f, "Normal"),
    Large(1.10f, "Grande"),
    ExtraLarge(1.20f, "Muy grande"),
    Huge(1.30f, "Extra grande"),
}

enum class GlassIntensity(val alpha: Float) {
    Subtle(0.70f),
    Balanced(0.85f),
    Pronounced(0.96f),
}

enum class AccentIntensity(val level: Float) {
    Calm(0.85f),
    Balanced(1.0f),
    Vibrant(1.15f),
}

data class UiPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val density: VisualDensity = VisualDensity.Comfortable,
    val fontScale: FontScale = FontScale.Normal,
    val glass: GlassIntensity = GlassIntensity.Balanced,
    val accent: AccentIntensity = AccentIntensity.Balanced,
    val showHero: Boolean = true,
    val showDistribution: Boolean = true,
    val showSystemStatus: Boolean = true,
    val showActivityFeed: Boolean = true,
    val showQuickActions: Boolean = true,
)

class UiPreferencesStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    private val _prefsFlow = MutableStateFlow(loadFromDisk())
    val prefsFlow: StateFlow<UiPreferences> = _prefsFlow.asStateFlow()

    private fun loadFromDisk(): UiPreferences = UiPreferences(
        themeMode   = ThemeMode.valueOf(prefs.getString(K_THEME, ThemeMode.System.name)   ?: ThemeMode.System.name),
        density     = VisualDensity.valueOf(prefs.getString(K_DENSITY, VisualDensity.Comfortable.name) ?: VisualDensity.Comfortable.name),
        fontScale   = FontScale.valueOf(prefs.getString(K_FONT, FontScale.Normal.name) ?: FontScale.Normal.name),
        glass       = GlassIntensity.valueOf(prefs.getString(K_GLASS, GlassIntensity.Balanced.name) ?: GlassIntensity.Balanced.name),
        accent      = AccentIntensity.valueOf(prefs.getString(K_ACCENT, AccentIntensity.Balanced.name) ?: AccentIntensity.Balanced.name),
        showHero           = prefs.getBoolean(K_SHOW_HERO, true),
        showDistribution   = prefs.getBoolean(K_SHOW_DIST, true),
        showSystemStatus   = prefs.getBoolean(K_SHOW_SYS, true),
        showActivityFeed   = prefs.getBoolean(K_SHOW_ACT, true),
        showQuickActions   = prefs.getBoolean(K_SHOW_QA, true),
    )

    fun load(): UiPreferences = loadFromDisk()

    fun save(newPrefs: UiPreferences) {
        this.prefs.edit()
            .putString(K_THEME,  newPrefs.themeMode.name)
            .putString(K_DENSITY, newPrefs.density.name)
            .putString(K_FONT,   newPrefs.fontScale.name)
            .putString(K_GLASS,  newPrefs.glass.name)
            .putString(K_ACCENT, newPrefs.accent.name)
            .putBoolean(K_SHOW_HERO, newPrefs.showHero)
            .putBoolean(K_SHOW_DIST, newPrefs.showDistribution)
            .putBoolean(K_SHOW_SYS,  newPrefs.showSystemStatus)
            .putBoolean(K_SHOW_ACT,  newPrefs.showActivityFeed)
            .putBoolean(K_SHOW_QA,   newPrefs.showQuickActions)
            .apply()
        _prefsFlow.value = newPrefs
    }

    private companion object {
        const val NAME = "simpe_bridge_ui_prefs"
        const val K_THEME     = "theme_mode"
        const val K_DENSITY   = "density"
        const val K_FONT      = "font_scale"
        const val K_GLASS     = "glass"
        const val K_ACCENT    = "accent"
        const val K_SHOW_HERO = "show_hero"
        const val K_SHOW_DIST = "show_dist"
        const val K_SHOW_SYS  = "show_sys"
        const val K_SHOW_ACT  = "show_act"
        const val K_SHOW_QA   = "show_qa"
    }
}
