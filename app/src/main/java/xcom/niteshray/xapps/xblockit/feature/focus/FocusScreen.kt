package xcom.niteshray.xapps.xblockit.feature.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import xcom.niteshray.xapps.xblockit.feature.focus.audio.FocusNoisePlayer
import xcom.niteshray.xapps.xblockit.feature.focus.audio.NoiseType
import xcom.niteshray.xapps.xblockit.feature.focus.components.NoiseSelector
import xcom.niteshray.xapps.xblockit.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// 🍅 CLEAN MINIMAL POMODORO TIMER
// Fixed 25 minutes with focus noise support
// ═══════════════════════════════════════════════════════════════

private const val WORK_MINUTES = 25
private const val BREAK_MINUTES = 5

/**
 * Main Pomodoro Focus Screen
 * Clean, minimal design with hero timer and focus noise
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen() {
    // Timer state
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isBreakTime by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableIntStateOf(WORK_MINUTES * 60) }
    var totalTime by remember { mutableIntStateOf(WORK_MINUTES * 60) }
    var completedSessions by remember { mutableIntStateOf(0) }
    
    // Noise state
    val noisePlayer = remember { FocusNoisePlayer() }
    var currentNoise by remember { mutableStateOf(NoiseType.WHITE) }
    var showNoiseSelector by remember { mutableStateOf(false) }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            noisePlayer.release()
        }
    }
    
    // Timer countdown logic
    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused && timeRemaining > 0) {
            delay(1000L)
            timeRemaining--
        }
        
        // Handle session completion
        if (timeRemaining == 0 && isRunning) {
            if (!isBreakTime) {
                // Work session completed -> Start break
                completedSessions++
                isBreakTime = true
                timeRemaining = BREAK_MINUTES * 60
                totalTime = BREAK_MINUTES * 60
                // Stop noise during break
                noisePlayer.stop()
            } else {
                // Break completed -> Ready for next work session
                isBreakTime = false
                timeRemaining = WORK_MINUTES * 60
                totalTime = WORK_MINUTES * 60
                // Resume noise for work session
                if (currentNoise != NoiseType.OFF) {
                    noisePlayer.play(currentNoise)
                }
            }
        }
    }
    
    // Start/stop noise with timer
    LaunchedEffect(isRunning, isPaused, isBreakTime) {
        if (isRunning && !isPaused && !isBreakTime && currentNoise != NoiseType.OFF) {
            noisePlayer.play(currentNoise)
        } else if (!isRunning || isPaused || isBreakTime) {
            noisePlayer.stop()
        }
    }

    // Animated progress (0 to 1)
    val progress by animateFloatAsState(
        targetValue = if (totalTime > 0) timeRemaining.toFloat() / totalTime else 1f,
        animationSpec = tween(300),
        label = "timer_progress"
    )

    // Noise selector bottom sheet
    if (showNoiseSelector) {
        ModalBottomSheet(
            onDismissRequest = { showNoiseSelector = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            NoiseSelector(
                selectedNoise = currentNoise,
                onNoiseSelected = { noise ->
                    currentNoise = noise
                    if (isRunning && !isPaused && !isBreakTime) {
                        noisePlayer.setNoiseType(noise)
                    }
                },
                onDismiss = { showNoiseSelector = false }
            )
        }
    }

    // Main Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Music icon (top right) - appears when timer is running
        AnimatedVisibility(
            visible = isRunning,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { showNoiseSelector = true },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = if (currentNoise != NoiseType.OFF) GlowWhiteMedium else BorderGlow,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Focus Sounds",
                    tint = if (currentNoise != NoiseType.OFF) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Session indicator dots
            SessionDots(
                completed = completedSessions,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Hero Timer Circle
            PomodoroTimer(
                progress = progress,
                timeRemaining = timeRemaining,
                isBreak = isBreakTime,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Control buttons
            TimerControls(
                isRunning = isRunning,
                isPaused = isPaused,
                onPlayPause = {
                    if (!isRunning) {
                        isRunning = true
                        isPaused = false
                    } else {
                        isPaused = !isPaused
                    }
                },
                onReset = {
                    isRunning = false
                    isPaused = false
                    isBreakTime = false
                    timeRemaining = WORK_MINUTES * 60
                    totalTime = WORK_MINUTES * 60
                    noisePlayer.stop()
                }
            )
        }
    }
}

/**
 * Session indicator dots showing completed Pomodoro sessions
 */
@Composable
private fun SessionDots(
    completed: Int,
    modifier: Modifier = Modifier,
    maxDots: Int = 4
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(maxDots) { index ->
            val isFilled = index < (completed % (maxDots + 1))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .then(
                        if (!isFilled) Modifier.border(
                            width = 1.dp,
                            color = BorderGlow,
                            shape = CircleShape
                        ) else Modifier
                    )
            )
        }
    }
}

/**
 * Main circular timer with progress arc
 */
@Composable
private fun PomodoroTimer(
    progress: Float,
    timeRemaining: Int,
    isBreak: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Background ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = CardDark,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
        }

        // Progress arc with gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val arcColors = if (isBreak) {
                listOf(MediumGray, LightGray)
            } else {
                listOf(PureWhite, LightGray)
            }
            
            drawArc(
                brush = Brush.sweepGradient(colors = arcColors),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 16f, cap = StrokeCap.Round)
            )
        }

        // Timer content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode indicator
            Text(
                text = if (isBreak) "BREAK" else "FOCUS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Time display
            Text(
                text = formatTime(timeRemaining),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-2).sp
            )
        }
    }
}

/**
 * Timer control buttons (Play/Pause and Reset)
 */
@Composable
private fun TimerControls(
    isRunning: Boolean,
    isPaused: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset button (only when running)
        AnimatedVisibility(
            visible = isRunning,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (isRunning) {
            Spacer(modifier = Modifier.width(24.dp))
        }

        // Main Play/Pause button
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 2.dp,
                    color = GlowWhite,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (!isRunning || isPaused) 
                    Icons.Default.PlayArrow 
                else 
                    Icons.Default.Pause,
                contentDescription = if (!isRunning || isPaused) "Start" else "Pause",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

/**
 * Format seconds to MM:SS string
 */
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
