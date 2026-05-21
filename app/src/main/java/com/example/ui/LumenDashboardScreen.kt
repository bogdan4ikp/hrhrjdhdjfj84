package com.example.ui

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LockScreenConfig

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LumenDashboardScreen(
    viewModel: LockScreenViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.configState.collectAsState()
    val isAodMode by viewModel.isAodMode.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val isPlaying by viewModel.isMusicPlaying.collectAsState()
    val trackIdx by viewModel.currentTrackIndex.collectAsState()
    val currentTrack = viewModel.playlist[trackIdx]
    val touchPulses by viewModel.touchPulses.collectAsState()

    val context = LocalContext.current

    // Navigation Screens: "HOME", "CLOCK", "WALLPAPER", "WIDGETS", "DYNAMIC_ISLAND", "ANIMATIONS", "ALWAYS_ON", "COLORS", "THEMES"
    var currentScreen by remember { mutableStateOf("HOME") }
    // Bottom Tab Bar active tab: 0 - Главная, 1 - Галерея, 2 - Сохраненные
    var activeTab by remember { mutableStateOf(0) }

    // Set Default Dialog state
    var showSetDefaultDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712)) // Deep black-purple sleek ambient canvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Sub-Screen Header Controller (For customize pages)
            if (currentScreen != "HOME") {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                "CLOCK" -> "Часы"
                                "WALLPAPER" -> "Обои"
                                "WIDGETS" -> "Виджеты"
                                "DYNAMIC_ISLAND" -> "Dynamic Island"
                                "ANIMATIONS" -> "Анимации"
                                "ALWAYS_ON" -> "Always On"
                                "COLORS" -> "Цветовые Тона"
                                "THEMES" -> "Темы Оформления"
                                else -> "Кастомизация"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { currentScreen = "HOME" }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            currentScreen = "HOME"
                            Toast.makeText(context, "Настройки успешно сохранены!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Готово",
                                tint = Color(0xFF10B981)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0B0F19)
                    )
                )
            } else {
                // HOME (Главная) Screen Top App Bar
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "LumenOS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                "Создай свой идеальный экран блокировки",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF030712)
                    ),
                    actions = {
                        IconButton(onClick = { currentScreen = "THEMES" }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Параметры",
                                tint = Color.White
                            )
                        }
                    }
                )
            }

            // Screen Content State Machine
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    1 -> {
                        // Gallery Screen View
                        GalleryTabScreen(viewModel = viewModel, onSelectTheme = { activeTab = 0 })
                    }
                    2 -> {
                        // Saved Themes Screen View
                        SavedThemesTabScreen(viewModel = viewModel, config = config, onApply = { activeTab = 0 })
                    }
                    else -> {
                        // Main Layout Screens depending on currentScreen state
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() with fadeOut()
                            },
                            label = "screen_trans"
                        ) { screen ->
                            when (screen) {
                                "HOME" -> {
                                    MainDashboardView(
                                        config = config,
                                        isAodMode = isAodMode,
                                        viewModel = viewModel,
                                        isPlaying = isPlaying,
                                        currentTrack = currentTrack,
                                        touchPulses = touchPulses,
                                        onNavigate = { currentScreen = it },
                                        onSetDefaultClick = { showSetDefaultDialog = true }
                                    )
                                }
                                "CLOCK" -> {
                                    ClockCustomizerView(
                                        config = config,
                                        viewModel = viewModel,
                                        isPlaying = isPlaying,
                                        currentTrack = currentTrack,
                                        touchPulses = touchPulses
                                    )
                                }
                                "WALLPAPER" -> {
                                    WallpaperCustomizerView(config = config, viewModel = viewModel)
                                }
                                "WIDGETS" -> {
                                    WidgetsCustomizerView(config = config, viewModel = viewModel)
                                }
                                "DYNAMIC_ISLAND" -> {
                                    DynamicIslandCustomizerView(config = config, viewModel = viewModel)
                                }
                                "ANIMATIONS" -> {
                                    AnimationsCustomizerView(
                                        config = config,
                                        viewModel = viewModel,
                                        isPlaying = isPlaying,
                                        currentTrack = currentTrack,
                                        touchPulses = touchPulses
                                    )
                                }
                                "ALWAYS_ON" -> {
                                    AlwaysOnCustomizerView(config = config, viewModel = viewModel)
                                }
                                "COLORS" -> {
                                    ColorsCustomizerView(config = config, viewModel = viewModel)
                                }
                                "THEMES" -> {
                                    ThemesCustomizerView(config = config, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }

            // Immersive Bottom Tab Navigation Bar matching the blueprint aesthetics
            NavigationBar(
                containerColor = Color(0xFF0D0E15),
                tonalElevation = 12.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == 0 && currentScreen == "HOME",
                    onClick = {
                        activeTab = 0
                        currentScreen = "HOME"
                    },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Главная"
                        )
                    },
                    label = { Text("Главная", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF8B5CF6),
                        selectedTextColor = Color(0xFF8B5CF6),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == 1) Icons.Default.Collections else Icons.Outlined.Collections,
                            contentDescription = "Галерея"
                        )
                    },
                    label = { Text("Галерея", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF8B5CF6),
                        selectedTextColor = Color(0xFF8B5CF6),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (activeTab == 2) Icons.Default.LibraryAddCheck else Icons.Outlined.LibraryAddCheck,
                            contentDescription = "Сохраненные"
                        )
                    },
                    label = { Text("Сохраненные", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF8B5CF6),
                        selectedTextColor = Color(0xFF8B5CF6),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF8B5CF6).copy(alpha = 0.15f)
                    )
                )
            }
        }

        // Safe Default Setup Dialog
        if (showSetDefaultDialog) {
            AlertDialog(
                onDismissRequest = { showSetDefaultDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NewReleases,
                            contentDescription = "Сообщение",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Установка по умолчанию", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Чтобы установить LumenOS в качестве основного или обойного экрана блокировки:",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                        Text(
                            "1. Нажмите кнопку «Открыть Настройки» ниже.\n" +
                            "2. Перейдите в раздел управления По умолчанию или Обоями.\n" +
                            "3. Назначьте LumenOS вашим активным помощником блокировки/живыми обоями.",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSetDefaultDialog = false
                            launchDefaultSettings(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("Открыть Настройки", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSetDefaultDialog = false }) {
                        Text("Отмена", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF0F172A),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

/**
 * Main Index dashboard view rendering live lockscreen emulator and quick actions
 */
@Composable
fun MainDashboardView(
    config: LockScreenConfig,
    isAodMode: Boolean,
    viewModel: LockScreenViewModel,
    isPlaying: Boolean,
    currentTrack: LockScreenViewModel.TrackInfo,
    touchPulses: List<LockScreenViewModel.TouchPulse>,
    onNavigate: (String) -> Unit,
    onSetDefaultClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Beautiful Simulated Physical Phone Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentAlignment = Alignment.Center
        ) {
            PhoneHardwareMockup(
                config = config,
                isAodMode = isAodMode,
                viewModel = viewModel,
                isPlaying = isPlaying,
                currentTrack = currentTrack,
                touchPulses = touchPulses
            )
        }

        // Live Event simulation row
        SimulationActionPanel(viewModel = viewModel, isAodMode = isAodMode, isPlaying = isPlaying)

        // Blue/Purple prominent "Настроить экран блокировки >" button
        Button(
            onClick = { onNavigate("CLOCK") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Настроить экран блокировки",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Далее",
                    tint = Color.White
                )
            }
        }

        // User explicit request: "сделай кнопку установить по умолчанию"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Install default",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Установить по умолчанию",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Запуск экрана как системного при разблокировке",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
                Button(
                    onClick = onSetDefaultClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Установить", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Быстрые действия Layout Grid matching Screen 1 "Быстрые действия"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Быстрые действия",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            // 2x4 Grid
            val quickActions = listOf(
                QuickActionItem("Часы", Icons.Default.Schedule, "CLOCK"),
                QuickActionItem("Обои", Icons.Default.Wallpaper, "WALLPAPER"),
                QuickActionItem("Виджеты", Icons.Default.Widgets, "WIDGETS"),
                QuickActionItem("Dynamic Island", Icons.Default.BlurOn, "DYNAMIC_ISLAND"),
                QuickActionItem("Анимации", Icons.Default.Animation, "ANIMATIONS"),
                QuickActionItem("Always On", Icons.Default.SettingsBrightness, "ALWAYS_ON"),
                QuickActionItem("Цвета", Icons.Default.Palette, "COLORS"),
                QuickActionItem("Темы", Icons.Default.Style, "THEMES")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                items(quickActions) { action ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.6f))
                            .clickable { onNavigate(action.screenRoute) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = action.label,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}

data class QuickActionItem(val label: String, val icon: ImageVector, val screenRoute: String)

/**
 * Screen 2: Часы (Clock Customizer with 3x3 Dot Alignment Grid)
 */
@Composable
fun ClockCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel,
    isPlaying: Boolean,
    currentTrack: LockScreenViewModel.TrackInfo,
    touchPulses: List<LockScreenViewModel.TouchPulse>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Focused Clock Dotted Frame Preview matching Screen 2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(2.dp, Color(0xFF8B5CF6).copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LiveWallpaper(
                    type = config.wallpaperType,
                    id = config.wallpaperId,
                    blurAmount = config.wallpaperBlur,
                    touchPulses = touchPulses
                )
                // Position Clock based on alignment settings
                val alignVal = when (config.clockPosition) {
                    "TOP" -> Alignment.TopCenter
                    "CENTER" -> Alignment.Center
                    else -> Alignment.BottomCenter
                }
                LockScreenClock(
                    config = config,
                    isAod = false,
                    modifier = Modifier
                        .align(alignVal)
                        .padding(vertical = 12.dp)
                )
                // Corner drag anchors dots simulator
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)).align(Alignment.TopStart))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)).align(Alignment.TopEnd))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)).align(Alignment.BottomStart))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8B5CF6)).align(Alignment.BottomEnd))
                }
            }
        }

        // Стиль часов
        Text("Стиль часов", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        val clockStyles = listOf(
            Triple("MINIMAL", "09:41", "Minimal"),
            Triple("BOLD", "09:41", "Bold"),
            Triple("LIQUID", "09:41", "Liquid"),
            Triple("DIGITAL_ULTRA", "09\n41", "Futuristic")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            clockStyles.forEach { style ->
                val isSelected = config.clockStyle == style.first
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1F1D2C) else Color(0xFF0F172A))
                        .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setClockStyle(style.first) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = style.second,
                        color = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                        fontSize = 20.sp,
                        fontWeight = if (style.first == "BOLD") FontWeight.ExtraBold else FontWeight.Light,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(style.third, color = Color.Gray, fontSize = 10.sp)
                }
            }
        }

        // Размер
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Размер", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("${(config.clockSize * 100).toInt()}%", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = config.clockSize,
            onValueChange = { viewModel.setClockSize(it) },
            valueRange = 0.5f..2.5f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        // Растяжение
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Растяжение X", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Slider(
                value = config.clockWidthStretch,
                onValueChange = { viewModel.setClockWidthStretch(it) },
                valueRange = 0.5f..2.0f,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Растяжение Y", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Slider(
                value = config.clockHeightStretch,
                onValueChange = { viewModel.setClockHeightStretch(it) },
                valueRange = 0.5f..2.0f,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
            )
        }

        // Позиция 3x3 Grid indicator matching Screen 2
        Text("Позиция", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .padding(14.dp)
            ) {
                val positions = listOf(
                    listOf("TOP" to "LEFT", "TOP" to "CENTER", "TOP" to "RIGHT"),
                    listOf("CENTER" to "LEFT", "CENTER" to "CENTER", "CENTER" to "RIGHT"),
                    listOf("BOTTOM" to "LEFT", "BOTTOM" to "CENTER", "BOTTOM" to "RIGHT")
                )

                positions.forEach { rowCoords ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowCoords.forEach { coord ->
                            val isSelected = config.clockPosition == coord.first && config.clockAlignment == coord.second
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF8B5CF6) else Color.DarkGray)
                                    .clickable {
                                        viewModel.setClockPosition(coord.first)
                                        viewModel.setClockAlignment(coord.second)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Screen 3: Обои (Wallpapers Creator with horizontal presets and coloring swatches)
 */
@Composable
fun WallpaperCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    var wallpaperTab by remember { mutableStateOf(0) } // 0: Статические, 1: Видео, 2: Live, 3: Blur

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wallpaper Segmented tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Статические", "Видео", "Live", "Blur").forEachIndexed { idx, title ->
                val isSelected = wallpaperTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF4F46E5) else Color.Transparent)
                        .clickable {
                            wallpaperTab = idx
                            if (idx == 2) {
                                viewModel.setWallpaperType("LIVE")
                            } else {
                                viewModel.setWallpaperType("STATIC")
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Static scenic presets list
        Text("Scenic градиенты", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val wallpaperOptions = listOf(
                Pair(0, Color(0xFF581C87)),
                Pair(1, Color(0xFF1E293B)),
                Pair(2, Color(0xFFFEF08A)),
                Pair(3, Color(0xFF064E3B))
            )

            wallpaperOptions.forEach { option ->
                val isSelected = config.wallpaperId == option.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(option.second, option.second.copy(alpha = 0.5f))
                            )
                        )
                        .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setWallpaperId(option.first) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Размытие фона slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Размытие фона", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("${(config.wallpaperBlur * 4).toInt()}%", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = config.wallpaperBlur,
            onValueChange = { viewModel.setWallpaperBlur(it) },
            valueRange = 0f..25f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        // Цветовой тон row
        Text("Цветовой тон", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        val swatches = listOf(
            Color(0xFF8B5CF6), Color(0xFF3B82F6), Color(0xFF22D3EE),
            Color(0xFF10B981), Color(0xFF84CC16), Color(0xFFEAB308),
            Color(0xFFEF4444), Color(0xFFEC4899)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            swatches.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { viewModel.setWidgetColorHex("#" + Integer.toHexString(color.value.toInt()).substring(2)) }
                )
            }
        }
    }
}

/**
 * Screen 4: Виджеты Customizer
 */
@Composable
fun WidgetsCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        Text("Добавленные", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        
        val addedWidgets = remember(config) {
            mutableListOf<Triple<String, String, () -> Unit>>().apply {
                if (config.widgetWeatherEnabled) {
                    add(Triple("Погода", "21° Облачно", { viewModel.setWidgetWeatherEnabled(false) }))
                }
                if (config.widgetBatteryEnabled) {
                    add(Triple("Батарея", "76%", { viewModel.setWidgetBatteryEnabled(false) }))
                }
                if (config.widgetCalendarEnabled) {
                    add(Triple("Календарь", "Встреча в 10:00", { viewModel.setWidgetCalendarEnabled(false) }))
                }
                if (config.widgetMusicEnabled) {
                    add(Triple("Музыка", "Into You - Ariana Grande", { viewModel.setWidgetMusicEnabled(false) }))
                }
                if (config.widgetStepsEnabled) {
                    add(Triple("Шаги", "4 350 шагов", { viewModel.setWidgetStepsEnabled(false) }))
                }
                if (config.widgetRemindersEnabled) {
                    add(Triple("Напоминания", "3 задачи", { viewModel.setWidgetRemindersEnabled(false) }))
                }
                if (config.widgetAlarmEnabled) {
                    add(Triple("Будильник", "07:00", { viewModel.setWidgetAlarmEnabled(false) }))
                }
                if (config.widgetStocksEnabled) {
                    add(Triple("Акции", "AAPL +1,32%", { viewModel.setWidgetStocksEnabled(false) }))
                }
            }
        }

        // Render Added widgets
        if (addedWidgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Список пуст. Добавьте виджеты ниже!", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            addedWidgets.forEach { triple ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = triple.third,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircle,
                            contentDescription = "Remove",
                            tint = Color(0xFFEF4444)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(triple.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(triple.second, color = Color.Gray, fontSize = 11.sp)
                    }
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Reorder",
                        tint = Color.DarkGray
                    )
                }
            }
        }

        // Добавить виджеты
        Text("Добавить виджеты", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        val availableWidgets = remember(config) {
            mutableListOf<Triple<String, String, () -> Unit>>().apply {
                if (!config.widgetStepsEnabled) {
                    add(Triple("Шаги", "4 350 шагов", { viewModel.setWidgetStepsEnabled(true) }))
                }
                if (!config.widgetRemindersEnabled) {
                    add(Triple("Напоминания", "3 задачи", { viewModel.setWidgetRemindersEnabled(true) }))
                }
                if (!config.widgetAlarmEnabled) {
                    add(Triple("Будильник", "07:00", { viewModel.setWidgetAlarmEnabled(true) }))
                }
                if (!config.widgetStocksEnabled) {
                    add(Triple("Акции", "AAPL +1,32%", { viewModel.setWidgetStocksEnabled(true) }))
                }
                if (!config.widgetWeatherEnabled) {
                    add(Triple("Погода", "+21°C Облачно", { viewModel.setWidgetWeatherEnabled(true) }))
                }
                if (!config.widgetBatteryEnabled) {
                    add(Triple("Батарея", "87%", { viewModel.setWidgetBatteryEnabled(true) }))
                }
                if (!config.widgetCalendarEnabled) {
                    add(Triple("Календарь", "Tech Review 12:00", { viewModel.setWidgetCalendarEnabled(true) }))
                }
                if (!config.widgetMusicEnabled) {
                    add(Triple("Музыка", "The Weeknd - After Hours", { viewModel.setWidgetMusicEnabled(true) }))
                }
            }
        }

        availableWidgets.forEach { triple ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (triple.first) {
                        "Шаги" -> Icons.Default.DirectionsRun
                        "Напоминания" -> Icons.Default.NotificationsActive
                        "Будильник" -> Icons.Default.Alarm
                        "Акции" -> Icons.Default.TrendingUp
                        "Погода" -> Icons.Default.CloudQueue
                        "Календарь" -> Icons.Default.CalendarToday
                        "Батарея" -> Icons.Default.BatteryChargingFull
                        else -> Icons.Default.MusicNote
                    }
                    Icon(icon, null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(18.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(triple.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(triple.second, color = Color.Gray, fontSize = 11.sp)
                }
                IconButton(onClick = triple.third) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add",
                        tint = Color(0xFF8B5CF6)
                    )
                }
            }
        }
    }
}

/**
 * Screen 5: Dynamic Island configuration controller
 */
@Composable
fun DynamicIslandCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        
        // Включить Dynamic Island Switch row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .padding(14.dp)
        ) {
            Text(
                "Включить Dynamic Island",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = config.islandEnabled,
                onCheckedChange = { viewModel.setIslandEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF8B5CF6)
                )
            )
        }

        // Live Dynamic Island Mockup Indicator Center Card matching Screen 5
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .width(180.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Headphones, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Connected", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Размер
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Размер", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("100%", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = 1.0f,
            onValueChange = {},
            valueRange = 0.5f..1.5f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        // Положение segment
        Text("Положение", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val positionOpts = listOf("Слева", "Центр", "Справа")
            positionOpts.forEach { opt ->
                val isSelected = opt == "Центр"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF4F46E5) else Color.Transparent)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(opt, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Прозрачность
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Прозрачность", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("80%", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = 0.8f,
            onValueChange = {},
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        // Анимация cards row
        Text("Анимация", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val animTypes = listOf(
                Pair("Плавная", "Легкое скольжение"),
                Pair("Быстрая", "Прямой переход"),
                Pair("Эластичная", "Фирменный отскок")
            )
            animTypes.forEach { type ->
                val isSelected = type.first == "Плавная"
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1F1D2C) else Color(0xFF0F172A))
                        .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(type.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(type.second, color = Color.Gray, fontSize = 8.sp, textAlign = TextAlign.Center)
                }
            }
        }

        // Показывать switches
        Text("Показывать", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        val showOptions = listOf("Музыка", "Уведомления", "Звонки", "Зарядка")
        showOptions.forEach { opt ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .padding(10.dp)
            ) {
                Text(opt, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF8B5CF6))
                )
            }
        }
    }
}

/**
 * Screen 6: Анимации Customizer
 */
@Composable
fun AnimationsCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel,
    isPlaying: Boolean,
    currentTrack: LockScreenViewModel.TrackInfo,
    touchPulses: List<LockScreenViewModel.TouchPulse>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preview Lockscreen Thumbnail
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(2.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LiveWallpaper(
                    type = config.wallpaperType,
                    id = config.wallpaperId,
                    blurAmount = config.wallpaperBlur,
                    touchPulses = touchPulses
                )
                LockScreenClock(
                    config = config,
                    isAod = false,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp)
                )
            }
        }

        // Эффект появления grid matching Screen 6
        Text("Эффект появления", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        val effectCards = listOf(
            Triple("NONE", "Нет", Icons.Default.Close),
            Triple("FADE", "Плавно", Icons.Default.WaterDrop),
            Triple("BOTTOM", "Снизу", Icons.Default.KeyboardArrowUp),
            Triple("TOP", "Сверху", Icons.Default.KeyboardArrowDown),
            Triple("SCALE", "Масштаб", Icons.Default.PhotoSizeSelectLarge)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            effectCards.forEach { card ->
                val isSelected = config.animationAppearStyle == card.first
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1F1D2C) else Color(0xFF0F172A))
                        .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setAnimationAppearStyle(card.first) }
                        .padding(vertical = 8.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = card.third,
                        contentDescription = card.second,
                        tint = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(card.second, color = Color.White, fontSize = 9.sp)
                }
            }
        }

        // Скорость анимации slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Скорость анимации", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("${(config.animationSpeed * 100).toInt()}%", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = config.animationSpeed,
            onValueChange = { viewModel.setAnimationSpeed(it) },
            valueRange = 0.1f..1.5f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        // Эффект разблокировки
        Text("Эффект разблокировки", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        val unlockEffects = listOf(
            Pair("FADE", "Плавно"),
            Pair("WAVE", "Волна"),
            Pair("GLOW", "Свечение"),
            Pair("BLUR", "Размытие")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            unlockEffects.forEach { effect ->
                val isSelected = config.animationUnlockStyle == effect.first
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF1F1D2C) else Color(0xFF0F172A))
                        .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { viewModel.setAnimationUnlockStyle(effect.first) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = effect.second,
                        color = if (isSelected) Color(0xFF8B5CF6) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Screen 7: Always On settings
 */
@Composable
fun AlwaysOnCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Включить Always On Display", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Отображение часов при выключенном экране", color = Color.Gray, fontSize = 11.sp)
            }
            Switch(
                checked = config.alwaysOnDisplayEnabled,
                onCheckedChange = { viewModel.setAlwaysOnDisplayEnabled(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF8B5CF6))
            )
        }

        // Display AOD presets options
        val presets = listOf("MINIMAL" to "Минимум (Только часы)", "BIG_CLOCK" to "Большие часы", "NOTIFICATIONS" to "Полный (С иконками)")
        presets.forEach { pair ->
            val isSelected = config.alwaysOnDisplayMode == pair.first
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setAlwaysOnDisplayMode(pair.first) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { viewModel.setAlwaysOnDisplayMode(pair.first) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(pair.second, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

/**
 * Colors configuration screen
 */
@Composable
fun ColorsCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Экранная сатурация", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("Регулируйте насыщенность оттенков интерфейса", color = Color.Gray, fontSize = 11.sp)
        Slider(
            value = config.uiSaturation,
            onValueChange = { viewModel.setUiSaturation(it) },
            valueRange = 0.0f..2.0f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6), activeTrackColor = Color(0xFF8B5CF6))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Матовое стекло Glassmorphic", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Прозрачность стекла", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Slider(
                value = config.uiTransparency,
                onValueChange = { viewModel.setUiTransparency(it) },
                valueRange = 0.1f..0.9f,
                modifier = Modifier.width(160.dp),
                colors = SliderDefaults.colors(thumbColor = Color(0xFF8B5CF6))
            )
        }

        // Tonal glow toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .padding(12.dp)
        ) {
            Text("Свечение элементов", color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Switch(
                checked = config.uiGlowEnabled,
                onCheckedChange = { viewModel.setUiGlowEnabled(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF8B5CF6))
            )
        }
    }
}

/**
 * Themes Page Configuration
 */
@Composable
fun ThemesCustomizerView(
    config: LockScreenConfig,
    viewModel: LockScreenViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Режим оформления темы", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        val themes = listOf("DARK" to "Темная тема", "LIGHT" to "Светлая тема", "AMOLED" to "Глубокий черный (AMOLED)")
        themes.forEach { pair ->
            val isSelected = config.themeMode == pair.first
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, if (isSelected) Color(0xFF8B5CF6) else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setThemeMode(pair.first) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { viewModel.setThemeMode(pair.first) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(pair.second, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

/**
 * Gallery Preset Themes screen tab
 */
@Composable
fun GalleryTabScreen(
    viewModel: LockScreenViewModel,
    onSelectTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Рекомендованные Темы", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Готовые пресеты экранов блокировки", color = Color.Gray, fontSize = 11.sp)

        val templates = listOf(
            Triple("iOS Neon Sunset", "Ультрасовременный градиент с Bold часами", 0),
            Triple("Nothing Minimalist", "Абсолютный монохромный дизайн с пиксельными шрифтами", 1),
            Triple("Cosmic Horizon", "Иммерсивная темная тема с точечной сеткой", 4)
        )

        templates.forEach { temp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setWallpaperId(temp.third)
                        if (temp.third == 1) {
                            viewModel.setClockStyle("MINIMAL")
                            viewModel.setClockSize(2.2f)
                        } else {
                            viewModel.setClockStyle("BOLD")
                            viewModel.setClockSize(1.8f)
                        }
                        onSelectTheme()
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6))
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(temp.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(temp.second, color = Color.Gray, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ArrowForwardIos, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

/**
 * Saved Themes screen tab
 */
@Composable
fun SavedThemesTabScreen(
    viewModel: LockScreenViewModel,
    config: LockScreenConfig,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ваши Сохранения", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Управляйте созданными экранами", color = Color.Gray, fontSize = 11.sp)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Активная сборка #1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Шрифт: ${config.clockStyle}, Обои: ID #${config.wallpaperId}", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Применить", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.setClockStyle("MINIMAL")
                            viewModel.setClockSize(1.0f)
                            viewModel.setWallpaperId(0)
                        },
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сброс", color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Smart events action trigger simulator panel
 */
@Composable
fun SimulationActionPanel(
    viewModel: LockScreenViewModel,
    isAodMode: Boolean,
    isPlaying: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0E15)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Симулятор событий в реальном времени",
                color = Color.LightGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { viewModel.toggleAodMode() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAodMode) Color(0xFF10B981) else Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isAodMode) "AOD ВКЛ" else "Режим AOD", color = if (isAodMode) Color.Black else Color.White, fontSize = 10.sp)
                }
                
                Button(
                    onClick = { viewModel.toggleMusicPlayback() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) Color(0xFFC084FC) else Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isPlaying) "Музыка ||" else "Включить Музыку", color = if (isPlaying) Color.Black else Color.White, fontSize = 10.sp)
                }

                Button(
                    onClick = { viewModel.simulateMessageNotification() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Уведомление", color = Color.White, fontSize = 10.sp)
                }

                Button(
                    onClick = { viewModel.toggleChargingPlug() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Зарядка ⚡", color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

/**
 * Android default settings trigger helper intent
 */
private fun launchDefaultSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e2: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e3: Exception) {
                Toast.makeText(context, "Настройки блокировки недоступны на этом устройстве.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

/**
 * Beautiful physical inner phone device frame embedding the active lock screen simulator
 */
@Composable
fun PhoneHardwareMockup(
    config: LockScreenConfig,
    isAodMode: Boolean,
    viewModel: LockScreenViewModel,
    isPlaying: Boolean,
    currentTrack: LockScreenViewModel.TrackInfo,
    touchPulses: List<LockScreenViewModel.TouchPulse>
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(410.dp)
            .shadow(16.dp, shape = RoundedCornerShape(32.dp))
            .border(4.dp, Color(0xFF1E293B), RoundedCornerShape(32.dp)) // sleek bezel
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    viewModel.addTouchPulse(offset.x, offset.y)
                }
            }
    ) {
        if (!isAodMode) {
            // Live beautiful gradient background
            LiveWallpaper(
                type = config.wallpaperType,
                id = config.wallpaperId,
                blurAmount = config.wallpaperBlur,
                touchPulses = touchPulses
            )

            // Absolute alignments layout
            Box(modifier = Modifier.fillMaxSize()) {
                val alignVal = when (config.clockPosition) {
                    "TOP" -> Alignment.TopCenter
                    "CENTER" -> Alignment.Center
                    else -> Alignment.BottomCenter
                }
                
                val topPadding = if (config.clockPosition == "TOP") 70.dp else 24.dp
                val bottomPadding = if (config.clockPosition == "BOTTOM") 70.dp else 24.dp

                // Relocatable Clock
                LockScreenClock(
                    config = config,
                    isAod = false,
                    modifier = Modifier
                        .align(alignVal)
                        .padding(top = topPadding, bottom = bottomPadding)
                )

                // Top Smart Capsule
                if (config.islandEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                    ) {
                        SmartCapsule(viewModel = viewModel, config = config)
                    }
                }

                // Bottom widgets layout grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                ) {
                    LockScreenWidgets(
                        config = config,
                        isMusicPlaying = isPlaying,
                        currentTrackTitle = currentTrack.title,
                        currentTrackArtist = currentTrack.artist
                    )
                }
            }
        } else {
            // ALWAYS ON DISPLAY
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Minimal ambient Clock style
                    LockScreenClock(
                        config = config.copy(clockAlpha = 0.45f, clockSize = config.clockSize * 0.85f),
                        isAod = true,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Low light info text
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Tap",
                            tint = Color.Gray,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "TAP TO WAKE",
                            color = Color.Gray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
