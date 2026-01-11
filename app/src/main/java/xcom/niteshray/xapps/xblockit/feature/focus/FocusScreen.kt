package xcom.niteshray.xapps.xblockit.feature.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.os.Build
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Widgets
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.Offering
import xcom.niteshray.xapps.xblockit.feature.paywall.PaywallDialog
import xcom.niteshray.xapps.xblockit.data.billing.BillingRepository
import xcom.niteshray.xapps.xblockit.feature.focus.service.FocusManager
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
fun FocusScreen(onNavigateToPaywall: () -> Unit) {
    val context = LocalContext.current
    val focusState by FocusManager.state.collectAsState()
    

    
    
    val isPremium by BillingRepository.isPremium.collectAsState()
    
    // Paywall Dialog State

    
    // Timer state from FocusManager
    val isRunning = focusState.isRunning
    val isPaused = focusState.isPaused
    val isBreakTime = focusState.isBreakTime
    val timeRemaining = focusState.timeRemaining
    val totalTime = focusState.totalTime
    val completedSessions = focusState.completedSessions
    val currentNoise = focusState.currentNoise
    
    var showNoiseSelector by remember { mutableStateOf(false) }
    
    // Commands to Service
    fun startServiceAction(action: String, noiseType: NoiseType? = null) {
        val intent = Intent(context, xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService::class.java).apply {
            this.action = action
            if (noiseType != null) {
                putExtra(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.EXTRA_NOISE_TYPE, noiseType.name)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    // Animated progress (0 to 1)
    val progress by animateFloatAsState(
        targetValue = if (totalTime > 0) timeRemaining.toFloat() / totalTime else 1f,
        animationSpec = tween(300),
        label = "timer_progress"
    )

    // Noise selector bottom sheet

    
    // IMPORTANT: Fix for onDismiss above. The NoiseSelector doesn't dismiss itself, 
    // the bottom sheet does. But if we selected a premium sound, we want to keep the bottom sheet?
    // Actually, usually we close the sheet and show paywall.
    // Let's refine the NoiseSelector call.
    if (showNoiseSelector) {
        ModalBottomSheet(
            onDismissRequest = { showNoiseSelector = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            NoiseSelector(
                selectedNoise = currentNoise,
                isUserPremium = isPremium,
                onNoiseSelected = { noise ->
                     if (noise.isPremium && !isPremium) {
                        showNoiseSelector = false 
                        onNavigateToPaywall()
                    } else {
                        startServiceAction(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.ACTION_SET_NOISE, noise)
                    }
                },
                onDismiss = { showNoiseSelector = false } // Explicit dismiss request
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
        // Rotating animation for music icon when sound is playing
        val isSoundPlaying = isRunning && !isPaused && currentNoise != NoiseType.OFF
        val infiniteTransition = rememberInfiniteTransition(label = "music_rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        
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
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(if (isSoundPlaying) rotation else 0f)
                )
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Widget Tip for new users
            WidgetTip(
                 onDismiss = { /* Handle permanent dismissal in local storage if needed */ },
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(bottom = 32.dp)
            )

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
                        startServiceAction(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.ACTION_START)
                    } else if (isPaused) {
                        startServiceAction(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.ACTION_RESUME)
                    } else {
                        startServiceAction(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.ACTION_PAUSE)
                    }
                },
                onReset = {
                    startServiceAction(xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService.ACTION_RESET)
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

/**
 * Widget Tip Card to help users add the widget
 * Shows only once per user.
 */
@Composable
private fun WidgetTip(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("blockit_prefs", Context.MODE_PRIVATE) }
    var isVisible by remember { 
        mutableStateOf(!prefs.getBoolean("widget_tip_shown", false)) 
    }
    
    if (isVisible) {
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
             // Close button
             IconButton(
                 onClick = { 
                     isVisible = false 
                     prefs.edit().putBoolean("widget_tip_shown", true).apply()
                     onDismiss()
                 },
                 modifier = Modifier
                     .align(Alignment.TopEnd)
                     .size(24.dp)
             ) {
                 Icon(
                     imageVector = Icons.Default.Close,
                     contentDescription = "Dismiss",
                     tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                     modifier = Modifier.size(16.dp)
                 )
             }
             
             Column {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(
                         imageVector = Icons.Default.Widgets,
                         contentDescription = null,
                         tint = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.size(20.dp)
                     )
                     Spacer(modifier = Modifier.width(8.dp))
                     Text(
                         text = "New Focus Widget",
                         style = MaterialTheme.typography.titleSmall,
                         color = MaterialTheme.colorScheme.onSurface
                     )
                 }
                 
                 Spacer(modifier = Modifier.height(8.dp))
                 
                 Text(
                     text = "Access your timer from the home screen!\n\nTo add: Long press on your Home Screen → Widgets → FlowBit.",
                     style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )
                 
                 Spacer(modifier = Modifier.height(12.dp))
                 
                 // "Got it" button
                 Button(
                     onClick = {
                         isVisible = false
                         prefs.edit().putBoolean("widget_tip_shown", true).apply()
                         onDismiss()
                     },
                     colors = ButtonDefaults.buttonColors(
                         containerColor = MaterialTheme.colorScheme.surfaceVariant,
                         contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                     ),
                     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                     modifier = Modifier.height(36.dp)
                 ) {
                     Text("Got it", fontSize = 13.sp)
                 }
             }
        }
    }
}
