package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.LockScreenConfig
import com.example.data.LockScreenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LockScreenViewModel(private val repository: LockScreenRepository) : ViewModel() {

    // Persistent Room Database lock screen config State
    val configState: StateFlow<LockScreenConfig> = repository.configFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LockScreenConfig()
        )

    // Interactive simulator UI variables (non-persisted real-time state)
    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> = _isMusicPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _activeNotification = MutableStateFlow<NotificationPayload?>(null)
    val activeNotification: StateFlow<NotificationPayload?> = _activeNotification.asStateFlow()

    private val _isAodMode = MutableStateFlow(false)
    val isAodMode: StateFlow<Boolean> = _isAodMode.asStateFlow()

    private val _touchPulses = MutableStateFlow<List<TouchPulse>>(emptyList())
    val touchPulses: StateFlow<List<TouchPulse>> = _touchPulses.asStateFlow()

    init {
        // Pre-set some fun default notification sequences
        viewModelScope.launch {
            // Give a welcome tips alert expanding from Dynamic Island on startup
            _activeNotification.value = NotificationPayload(
                sender = "LumenOS Core System",
                message = "Кастомизация Lock Screen запущена. Выберите настроечную панель справа!",
                type = "SYSTEM"
            )
        }
    }

    // List of simulated music tracks
    val playlist = listOf(
        TrackInfo("After Hours", "The Weeknd", "R&B / Synth", 192),
        TrackInfo("Stargazing", "Travis Scott", "Trap", 270),
        TrackInfo("Blinding Lights", "The Weeknd", "Synthwave", 200),
        TrackInfo("Space Walk", "Nothing OS Synth", "Futuristic Ambient", 145)
    )

    // Structure of simulated notifications
    data class NotificationPayload(val sender: String, val message: String, val type: String)
    data class TrackInfo(val title: String, val artist: String, val genre: String, val durationSec: Int)
    data class TouchPulse(val x: Float, val y: Float, val id: Long = System.currentTimeMillis())

    // Update Persistent Database
    private fun updateConfig(update: (LockScreenConfig) -> LockScreenConfig) {
        viewModelScope.launch {
            val current = configState.value
            repository.saveConfig(update(current))
        }
    }

    // Setters for Clock System
    fun setClockStyle(style: String) = updateConfig { it.copy(clockStyle = style) }
    fun setClockSize(size: Float) = updateConfig { it.copy(clockSize = size.coerceIn(0.5f, 3.0f)) }
    fun setClockWidthStretch(stretch: Float) = updateConfig { it.copy(clockWidthStretch = stretch.coerceIn(0.5f, 2.0f)) }
    fun setClockHeightStretch(stretch: Float) = updateConfig { it.copy(clockHeightStretch = stretch.coerceIn(0.5f, 2.0f)) }
    fun setClockSpanHalfScreen(span: Boolean) = updateConfig { it.copy(clockSpanHalfScreen = span) }
    fun setClockPosition(position: String) = updateConfig { it.copy(clockPosition = position) }
    fun setClockAlignment(alignment: String) = updateConfig { it.copy(clockAlignment = alignment) }
    fun setClockFontIndex(index: Int) = updateConfig { it.copy(clockFontIndex = index) }
    fun setClockAlpha(alpha: Float) = updateConfig { it.copy(clockAlpha = alpha.coerceIn(0.0f, 1.0f)) }
    fun setClockAnimation(anim: String) = updateConfig { it.copy(clockAnimation = anim) }

    // Setters for Dynamic Island
    fun setIslandEnabled(enabled: Boolean) = updateConfig { it.copy(islandEnabled = enabled) }
    fun setIslandMode(mode: String) = updateConfig { it.copy(islandMode = mode) }

    // Setters for Wallpaper
    fun setWallpaperType(type: String) = updateConfig { it.copy(wallpaperType = type) }
    fun setWallpaperId(id: Int) = updateConfig { it.copy(wallpaperId = id) }
    fun setWallpaperBlur(blur: Float) = updateConfig { it.copy(wallpaperBlur = blur) }

    // Setters for Widgets
    fun setWidgetWeatherEnabled(enabled: Boolean) = updateConfig { it.copy(widgetWeatherEnabled = enabled) }
    fun setWidgetCalendarEnabled(enabled: Boolean) = updateConfig { it.copy(widgetCalendarEnabled = enabled) }
    fun setWidgetBatteryEnabled(enabled: Boolean) = updateConfig { it.copy(widgetBatteryEnabled = enabled) }
    fun setWidgetMusicEnabled(enabled: Boolean) = updateConfig { it.copy(widgetMusicEnabled = enabled) }
    fun setWidgetStepsEnabled(enabled: Boolean) = updateConfig { it.copy(widgetStepsEnabled = enabled) }
    fun setWidgetRemindersEnabled(enabled: Boolean) = updateConfig { it.copy(widgetRemindersEnabled = enabled) }
    fun setWidgetAlarmEnabled(enabled: Boolean) = updateConfig { it.copy(widgetAlarmEnabled = enabled) }
    fun setWidgetStocksEnabled(enabled: Boolean) = updateConfig { it.copy(widgetStocksEnabled = enabled) }
    fun setWidgetTransparency(trans: Float) = updateConfig { it.copy(widgetTransparency = trans) }
    fun setWidgetCornerRadius(radius: Int) = updateConfig { it.copy(widgetCornerRadius = radius) }
    fun setWidgetColorHex(color: String) = updateConfig { it.copy(widgetColorHex = color) }

    // Setters for Always On Display
    fun setAlwaysOnDisplayEnabled(enabled: Boolean) = updateConfig { it.copy(alwaysOnDisplayEnabled = enabled) }
    fun setAlwaysOnDisplayMode(mode: String) = updateConfig { it.copy(alwaysOnDisplayMode = mode) }

    // Setters for Animations
    fun setAnimationAppearStyle(style: String) = updateConfig { it.copy(animationAppearStyle = style) }
    fun setAnimationUnlockStyle(style: String) = updateConfig { it.copy(animationUnlockStyle = style) }
    fun setAnimationSpeed(speed: Float) = updateConfig { it.copy(animationSpeed = speed) }

    // Setters for Styling & Theme
    fun setThemeMode(mode: String) = updateConfig { it.copy(themeMode = mode) }
    fun setUiCornerRadius(radius: Int) = updateConfig { it.copy(uiCornerRadius = radius) }
    fun setUiTransparency(trans: Float) = updateConfig { it.copy(uiTransparency = trans) }
    fun setUiSaturation(sat: Float) = updateConfig { it.copy(uiSaturation = sat) }
    fun setUiBlur(blur: Float) = updateConfig { it.copy(uiBlur = blur) }
    fun setUiGlowEnabled(glow: Boolean) = updateConfig { it.copy(uiGlowEnabled = glow) }

    // Interactive simulator actions
    fun toggleChargingPlug() {
        _isCharging.value = !_isCharging.value
        if (_isCharging.value) {
            _activeNotification.value = NotificationPayload(
                sender = "Power Source",
                message = "Подключено быстрое зарядное устройство superVOOC • 100W",
                type = "CHARGING"
            )
        } else {
            _activeNotification.value = null
        }
    }

    fun toggleMusicPlayback() {
        _isMusicPlaying.value = !_isMusicPlaying.value
        if (_isMusicPlaying.value) {
            val track = playlist[_currentTrackIndex.value]
            _activeNotification.value = NotificationPayload(
                sender = track.artist,
                message = "Now Playing: ${track.title}",
                type = "MUSIC"
            )
        }
    }

    fun changeMusicTrack(next: Boolean) {
        val size = playlist.size
        val current = _currentTrackIndex.value
        val newIdx = if (next) (current + 1) % size else (current - 1 + size) % size
        _currentTrackIndex.value = newIdx
        if (_isMusicPlaying.value) {
            val track = playlist[newIdx]
            _activeNotification.value = NotificationPayload(
                sender = track.artist,
                message = track.title,
                type = "MUSIC"
            )
        }
    }

    fun simulateIncomingCall() {
        _activeNotification.value = NotificationPayload(
            sender = "+7 (999) 111-OS26",
            message = "Входящий вызов LumenCall...",
            type = "CALL"
        )
    }

    fun simulateMessageNotification() {
        val senders = listOf("Dmitry", "Veronika", "Alex", "Team Lead Room", "Telegram Bot")
        val messages = listOf(
            "Привет! Зацени новые часы в LumenOS! 🔥",
            "Дизайн iOS 26 на андроиде просто пушка!",
            "Все виджеты настраиваются налету через Room",
            "Проверил анимации - стабильные 120 FPS!",
            "Слушай, а Live обои завезли? С частицами?"
        )
        val randomIndex = (senders.indices).random()
        _activeNotification.value = NotificationPayload(
            sender = senders[randomIndex],
            message = messages[randomIndex],
            type = "MESSAGE"
        )
    }

    fun clearActiveNotification() {
        _activeNotification.value = null
    }

    fun toggleAodMode() {
        _isAodMode.value = !_isAodMode.value
    }

    fun addTouchPulse(x: Float, y: Float) {
        val newPulse = TouchPulse(x, y)
        _touchPulses.value = _touchPulses.value + newPulse
        // Keep last 4 pulses
        if (_touchPulses.value.size > 4) {
            _touchPulses.value = _touchPulses.value.takeLast(4)
        }
    }
}

class LockScreenViewModelFactory(private val repository: LockScreenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LockScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LockScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
