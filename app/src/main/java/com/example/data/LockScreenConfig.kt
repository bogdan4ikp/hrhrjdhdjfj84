package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lock_screen_config")
data class LockScreenConfig(
    @PrimaryKey val id: Int = 1,
    
    // System clock style settings
    val clockStyle: String = "MINIMAL", // MINIMAL, BOLD, LIQUID, DIGITAL_ULTRA, FUTURISTIC
    val clockSize: Float = 1.0f, // 0.5f corresponds to 50%, 3.0f to 300%
    val clockWidthStretch: Float = 1.0f, // Width stretch multiplier (0.5 to 2.0)
    val clockHeightStretch: Float = 1.0f, // Height stretch multiplier (0.5 to 2.0)
    val clockSpanHalfScreen: Boolean = false, // Takes up half the screen space
    val clockPosition: String = "TOP", // TOP, CENTER, BOTTOM
    val clockAlignment: String = "CENTER", // LEFT, CENTER, RIGHT
    val clockFontIndex: Int = 0, // Selection of elegant paired fonts
    val clockAlpha: Float = 0.9f, // Clock transparency
    val clockAnimation: String = "FADE", // FADE, SLIDE, ELASTIC, LIQUID

    // Dynamic Island / Smart Capsule
    val islandEnabled: Boolean = true,
    val islandMode: String = "MUSIC", // STATIC, MUSIC, CHARGING, NOTIFICATION, TIMER

    // Wallpapers & background filters
    val wallpaperType: String = "STATIC", // STATIC, LIVE, BLUR
    val wallpaperId: Int = 0, // Selection of beautiful wallpaper choices
    val wallpaperBlur: Float = 0.0f, // Wallpaper blur amount (0 to 25dp)

    // Widgets (Toggles and styles)
    val widgetWeatherEnabled: Boolean = true,
    val widgetCalendarEnabled: Boolean = true,
    val widgetBatteryEnabled: Boolean = true,
    val widgetMusicEnabled: Boolean = false,
    val widgetStepsEnabled: Boolean = false,
    val widgetRemindersEnabled: Boolean = false,
    val widgetAlarmEnabled: Boolean = false,
    val widgetStocksEnabled: Boolean = false,
    val widgetTransparency: Float = 0.3f, // Widgets background alpha
    val widgetCornerRadius: Int = 16, // Rounded corner radius in dp
    val widgetColorHex: String = "#CCCCCC", // Custom widget theme color

    // Always On Display
    val alwaysOnDisplayEnabled: Boolean = false,
    val alwaysOnDisplayMode: String = "MINIMAL", // MINIMAL, BIG_CLOCK, NOTIFICATIONS

    // Animations customizer parameters from the mockup screen
    val animationAppearStyle: String = "FADE", // NONE, FADE, BOTTOM, TOP, SCALE
    val animationUnlockStyle: String = "FADE", // FADE, WAVE, GLOW, BLUR
    val animationSpeed: Float = 0.7f, // Slider percentage (0.0 to 1.0)

    // Theme & styling configs
    val themeMode: String = "DARK", // DARK, LIGHT, AMOLED, DYNAMIC
    val uiCornerRadius: Int = 20, // UI elements radius in dp
    val uiTransparency: Float = 0.3f, // General transparency (Glass alpha)
    val uiSaturation: Float = 1.0f, // Background color saturation influence
    val uiBlur: Float = 15.0f, // Render blur radius
    val uiGlowEnabled: Boolean = true // Neon glow highlights
)
