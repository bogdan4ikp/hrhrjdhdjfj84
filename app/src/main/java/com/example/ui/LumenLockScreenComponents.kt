package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LockScreenConfig
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Represents a decorative live wallpaper particle node
class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var color: Color,
    var alpha: Float = 0.6f
)

/**
 * Live Wallpaper Render Canvas: Draws rich background gradients or dynamic moving interactive nodes
 */
@Composable
fun LiveWallpaper(
    type: String,
    id: Int,
    blurAmount: Float,
    touchPulses: List<LockScreenViewModel.TouchPulse>,
    modifier: Modifier = Modifier
) {
    // Elegant background gradient palettes inspired by modern OS themes
    val palettes = listOf(
        // iOS 26 Sunset Cyber
        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF581C87), Color(0xFF86198F)),
        // Nothing OS Obsidian Slate
        listOf(Color(0xFF0B0F19), Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617)),
        // Material You Pastel Aurora
        listOf(Color(0xFFFFEDD5), Color(0xFFFEF3C7), Color(0xFFFEF08A), Color(0xFFFDE047)),
        // HyperOS Mint Glow
        listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF111827), Color(0xFF0F172A)),
        // Cosmic Violet Glow
        listOf(Color(0xFF0c001f), Color(0xFF1d003a), Color(0xFF001f3f), Color(0xFF0d1117))
    )

    val currentPalette = remember(id) {
        palettes.getOrElse(id) { palettes[0] }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (type == "LIVE") {
            // Live reactive particle system running via an infinite animation update loop
            val infiniteTransition = rememberInfiniteTransition(label = "Particles")
            val tick by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "Tick"
            )

            val particles = remember {
                mutableStateListOf<Particle>().apply {
                    repeat(24) {
                        add(
                            Particle(
                                x = Random.nextFloat(),
                                y = Random.nextFloat(),
                                vx = (Random.nextFloat() - 0.5f) * 0.01f,
                                vy = (Random.nextFloat() - 0.5f) * 0.01f,
                                radius = Random.nextFloat() * 12f + 4f,
                                color = currentPalette.random().copy(alpha = Random.nextFloat() * 0.5f + 0.2f)
                            )
                        )
                    }
                }
            }

            // Force update positions when tick changes
            val pulseList = touchPulses
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw base rich background gradient
                drawRect(
                    brush = Brush.verticalGradient(colors = currentPalette),
                    size = size
                )

                // Update and draw live particles
                particles.forEach { p ->
                    // Move
                    p.x += p.vx
                    p.y += p.vy

                    // Boundary bounce
                    if (p.x < 0 || p.x > 1) p.vx *= -1
                    if (p.y < 0 || p.y > 1) p.vy *= -1
                    p.x = p.x.coerceIn(0f, 1f)
                    p.y = p.y.coerceIn(0f, 1f)

                    // Draw
                    val drawX = p.x * w
                    val drawY = p.y * h
                    drawCircle(
                        color = p.color,
                        radius = p.radius,
                        center = Offset(drawX, drawY)
                    )

                    // Draw connecting micro-lines to simulate Nothing OS dots grid
                    particles.forEach { other ->
                        val dx = (p.x - other.x) * w
                        val dy = (p.y - other.y) * h
                        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                        if (dist < 150f) {
                            drawLine(
                                color = p.color.copy(alpha = (1f - dist / 150f) * 0.15f),
                                start = Offset(drawX, drawY),
                                end = Offset(other.x * w, other.y * h),
                                strokeWidth = 1f
                            )
                        }
                    }
                }

                // Draw touch ripples
                pulseList.forEach { pulse ->
                    // Static drawing of touch pulse indicator
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = 60f,
                        center = Offset(pulse.x, pulse.y)
                    )
                }
            }
        } else {
            // Draw gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(currentPalette))
            )
        }

        // Wallpaper Blur Layer Filter
        if (blurAmount > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurAmount.dp)
                    .background(Color.Black.copy(alpha = 0.15f))
            )
        }
    }
}

/**
 * Customized Clock Composable offering 5 styling aesthetics
 */
@Composable
fun LockScreenClock(
    config: LockScreenConfig,
    isAod: Boolean,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    // Live updating clock time format
    var timeString by remember { mutableStateOf("") }
    var secondsString by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            secondsString = SimpleDateFormat("ss", Locale.getDefault()).format(now)
            dateString = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(now)
            kotlinx.coroutines.delay(1000)
        }
    }

    if (timeString.isEmpty()) {
        timeString = "12:00"
        secondsString = "00"
        dateString = "Thursday, 21 May"
    }

    // Modern typography options
    val fonts = listOf(
        FontFamily.SansSerif,
        FontFamily.Serif,
        FontFamily.Monospace,
        FontFamily.Default
    )
    val chosenFont = fonts.getOrElse(config.clockFontIndex) { fonts[0] }

    val align = when (config.clockAlignment) {
        "LEFT" -> Alignment.Start
        "RIGHT" -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

    val textAlignment = when (config.clockAlignment) {
        "LEFT" -> TextAlign.Left
        "RIGHT" -> TextAlign.Right
        else -> TextAlign.Center
    }

    val scaleX = config.clockWidthStretch
    val scaleY = config.clockHeightStretch

    val clockColor = if (isAod) {
        Color.White
    } else {
        Color.White.copy(alpha = config.clockAlpha)
    }

    // Wrap contents in standard animations matching config settings
    val animatedScaleY by animateFloatAsState(
        targetValue = scaleY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "clockScaleY"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.scaleX = scaleX
                this.scaleY = animatedScaleY
            }
            .padding(horizontal = 24.dp),
        horizontalAlignment = align
    ) {
        // Render different Clock Typography Modes
        when (config.clockStyle) {
            "MINIMAL" -> {
                // Highly minimalist ultra-thin styling
                Text(
                    text = timeString,
                    style = TextStyle(
                        fontSize = (84 * config.clockSize).sp,
                        fontWeight = FontWeight.ExtraLight,
                        fontFamily = chosenFont,
                        color = clockColor,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(0f, 4f),
                            blurRadius = 10f
                        )
                    ),
                    textAlign = textAlignment
                )
            }

            "BOLD" -> {
                // Chunky heavy weight font
                Text(
                    text = timeString,
                    style = TextStyle(
                        fontSize = (92 * config.clockSize).sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = chosenFont,
                        color = clockColor,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(0f, 6f),
                            blurRadius = 16f
                        )
                    ),
                    textAlign = textAlignment
                )
            }

            "LIQUID" -> {
                // Glassmorphic liquid translucent design with heavy shadows and fine border outline
                Box {
                    Text(
                        text = timeString,
                        style = TextStyle(
                            fontSize = (88 * config.clockSize).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = chosenFont,
                            color = clockColor,
                            shadow = Shadow(
                                color = Color.White.copy(alpha = 0.4f),
                                offset = Offset(0f, -2f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier
                            .drawWithContent {
                                drawContent()
                            }
                            .graphicsLayer(alpha = 0.65f),
                        textAlign = textAlignment
                    )
                }
            }

            "DIGITAL_ULTRA" -> {
                // Oversized stacked hours and minutes layout matching iOS style
                val parts = timeString.split(":")
                val hr = parts.getOrNull(0) ?: "12"
                val min = parts.getOrNull(1) ?: "00"

                Column(horizontalAlignment = align) {
                    Text(
                        text = hr,
                        style = TextStyle(
                            fontSize = (76 * config.clockSize).sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = chosenFont,
                            color = clockColor,
                            lineHeight = (72 * config.clockSize).sp
                        ),
                        textAlign = textAlignment
                    )
                    Text(
                        text = min,
                        style = TextStyle(
                            fontSize = (76 * config.clockSize).sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = chosenFont,
                            color = clockColor,
                            lineHeight = (72 * config.clockSize).sp
                        ),
                        textAlign = textAlignment
                    )
                }
            }

            "FUTURISTIC" -> {
                // Neon glow display style
                val glowColor = if (config.uiGlowEnabled && !isAod) Color(0xFF10B981) else Color.White
                Text(
                    text = "$timeString:$secondsString",
                    style = TextStyle(
                        fontSize = (68 * config.clockSize).sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        shadow = Shadow(
                            color = glowColor,
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    ),
                    textAlign = textAlignment
                )
            }
        }

        // Subtitle Date label
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dateString,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = chosenFont,
                color = if (isAod) Color.LightGray else Color.White.copy(alpha = 0.8f),
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = Offset(0f, 2f),
                    blurRadius = 4f
                )
            ),
            textAlign = textAlignment
        )
    }
}

/**
 * Dynamic Island capsule replicating modern interactive smart bubble overlays
 */
@Composable
fun SmartCapsule(
    viewModel: LockScreenViewModel,
    config: LockScreenConfig,
    modifier: Modifier = Modifier
) {
    val activeNotification by viewModel.activeNotification.collectAsState()
    val isCharging by viewModel.isCharging.collectAsState()
    val isPlaying by viewModel.isMusicPlaying.collectAsState()
    val trackIdx by viewModel.currentTrackIndex.collectAsState()
    val currentTrack = viewModel.playlist[trackIdx]

    // Determine target size for capsule layout animations
    val isExpanded = activeNotification != null
    val capsuleWidth by animateDpAsState(
        targetValue = when {
            !isExpanded -> 120.dp
            activeNotification?.type == "CALL" -> 280.dp
            activeNotification?.type == "MUSIC" -> 260.dp
            else -> 290.dp
        },
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
        label = "capsuleWidth"
    )

    val capsuleHeight by animateDpAsState(
        targetValue = when {
            !isExpanded -> 30.dp
            activeNotification?.type == "CALL" -> 72.dp
            activeNotification?.type == "MUSIC" -> 78.dp
            else -> 58.dp
        },
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
        label = "capsuleHeight"
    )

    val glowModifier = if (config.uiGlowEnabled) {
        Modifier.border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .width(capsuleWidth)
                .height(capsuleHeight)
                .then(glowModifier)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
                .clickable {
                    viewModel.clearActiveNotification()
                }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!isExpanded) {
                // Minimalist smart capsule notch dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isCharging) Color.Green else Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCharging) "Charging" else "LumenOS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            } else {
                // Expanded smart pill matching action triggers
                val payload = activeNotification!!
                when (payload.type) {
                    "CHARGING" -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Bolt",
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payload.sender,
                                    color = Color.Green,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = payload.message,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                            Text(
                                text = "87%",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    "MUSIC" -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Spinning album art mockup
                            val infiniteRotation = rememberInfiniteTransition(label = "Vinyl")
                            val rotationAngle by infiniteRotation.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(6000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "Rotation"
                            )

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .graphicsLayer {
                                        rotationZ = if (isPlaying) rotationAngle else 0f
                                    }
                                    .clip(CircleShape)
                                    .background(
                                        Brush.sweepGradient(
                                            listOf(
                                                Color(0xFFEC4899),
                                                Color(0xFF8B5CF6),
                                                Color(0xFF3B82F6),
                                                Color(0xFFEC4899)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payload.message, // Track Title
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = payload.sender, // Artist
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                // Simulated track bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(Color.DarkGray)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.4f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            // Wave animations simulation
                            Row(verticalAlignment = Alignment.Bottom) {
                                repeat(3) { index ->
                                    val infiniteAmp = rememberInfiniteTransition(label = "Wave$index")
                                    val ampHeightVal by infiniteAmp.animateFloat(
                                        initialValue = 4f,
                                        targetValue = 20f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(300 + index * 120, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "Amp"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .width(2.5.dp)
                                            .height(if (isPlaying) ampHeightVal.dp else 4.dp)
                                            .background(Color(0xFF8B5CF6))
                                    )
                                }
                            }
                        }
                    }

                    "CALL" -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Active call",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payload.sender,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = payload.message,
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = { viewModel.clearActiveNotification() },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.Red, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallEnd,
                                        contentDescription = "Decline",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    else -> { // MESSAGE / SYSTEM info alerts
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = "Msg",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payload.sender,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = payload.message,
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    fontFamily = FontFamily.SansSerif,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Customizable visual widgets aligning to Material 3 Glassmorphism principles
 */
@Composable
fun LockScreenWidgets(
    config: LockScreenConfig,
    isMusicPlaying: Boolean,
    currentTrackTitle: String,
    currentTrackArtist: String,
    modifier: Modifier = Modifier
) {
    val widgetShape = RoundedCornerShape(config.widgetCornerRadius.dp)
    val backdropAlpha = config.widgetTransparency

    // Glow highlights if enabled
    val glowModifier = if (config.uiGlowEnabled) {
        Modifier.border(0.5.dp, Color.White.copy(alpha = 0.12f), widgetShape)
    } else Modifier

    val activeWidgets = remember(config, isMusicPlaying, currentTrackTitle, currentTrackArtist) {
        mutableListOf<@Composable (Modifier) -> Unit>().apply {
            if (config.widgetWeatherEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Weather",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Weather", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("+21°C • Облачно", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetCalendarEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Events",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Events", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("12:00 Tech Review", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetBatteryEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Battery",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Battery", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("87% • Optimized", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetMusicEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isMusicPlaying) Icons.Default.PauseCircleOutline else Icons.Default.PlayCircleOutline,
                            contentDescription = "Music",
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(currentTrackArtist, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, maxLines = 1)
                            Text(currentTrackTitle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        }
                    }
                }
            }
            if (config.widgetStepsEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = "Steps",
                            tint = Color(0xFFFB923C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Шаги", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("4 350 шагов", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetRemindersEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Reminders",
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Напоминания", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("3 задачи", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetAlarmEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Alarm",
                            tint = Color(0xFFF472B6),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Будильник", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("07:00", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (config.widgetStocksEnabled) {
                add { mod ->
                    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Stocks",
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Акции", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            Text("AAPL +1,32%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group the dynamically active widgets in rows of 2
        activeWidgets.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { widgetCompos ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(glowModifier)
                            .shadow(2.dp, shape = widgetShape)
                            .clip(widgetShape)
                            .background(Color.Black.copy(alpha = backdropAlpha))
                            .padding(10.dp)
                    ) {
                        widgetCompos(Modifier.fillMaxWidth())
                    }
                }
                // Handle odd count alignment
                if (pair.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
