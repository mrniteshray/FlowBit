package xcom.niteshray.xapps.xblockit.feature.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import xcom.niteshray.xapps.xblockit.ui.theme.*

data class FocusSession(
    val name: String,
    val workMinutes: Int,
    val breakMinutes: Int,
    val icon: String
)

@Composable
fun FocusScreen() {
    val sessions = listOf(
        FocusSession("Quick Focus", 15, 3, "⚡"),
        FocusSession("Pomodoro", 25, 5, "🍅"),
        FocusSession("Deep Work", 50, 10, "🧠"),
        FocusSession("Custom", 45, 10, "⚙️")
    )
    
    var selectedSession by remember { mutableStateOf(sessions[1]) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isBreakTime by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(selectedSession.workMinutes * 60) }
    var totalTime by remember { mutableStateOf(selectedSession.workMinutes * 60) }
    var completedSessions by remember { mutableStateOf(0) }
    var totalFocusMinutes by remember { mutableStateOf(0) }
    
    // Timer Logic
    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused && timeRemaining > 0) {
            delay(1000)
            timeRemaining--
            if (!isBreakTime) {
                totalFocusMinutes = (selectedSession.workMinutes * 60 - timeRemaining) / 60
            }
        }
        if (timeRemaining == 0 && isRunning) {
            if (!isBreakTime) {
                completedSessions++
                isBreakTime = true
                timeRemaining = selectedSession.breakMinutes * 60
                totalTime = selectedSession.breakMinutes * 60
            } else {
                isBreakTime = false
                timeRemaining = selectedSession.workMinutes * 60
                totalTime = selectedSession.workMinutes * 60
            }
        }
    }
    
    val progress by animateFloatAsState(
        targetValue = if (totalTime > 0) timeRemaining.toFloat() / totalTime else 0f,
        animationSpec = tween(300),
        label = "progress"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Focus Timer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isBreakTime) "Break Time 🎉" else if (isRunning) "Stay focused!" else "Choose a session",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Stats Badge with shine
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = GlowWhite,
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$completedSessions",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Session Type Selector
        if (!isRunning) {
            Text(
                text = "Select Session Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    SessionCard(
                        session = session,
                        isSelected = selectedSession == session,
                        onClick = {
                            selectedSession = session
                            timeRemaining = session.workMinutes * 60
                            totalTime = session.workMinutes * 60
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Timer Circle - White arc on dark background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Circle - subtle dark ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = CardDark,
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
            
            // Progress Arc - White gradient
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = if (isBreakTime)
                            listOf(MediumGray, LightGray)
                        else
                            listOf(PureWhite, LightGray)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
            
            // Timer Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBreakTime) "☕ BREAK" else "🎯 FOCUS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = formatTime(timeRemaining),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = selectedSession.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Control Buttons - White filled
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            if (isRunning) {
                IconButton(
                    onClick = {
                        isRunning = false
                        isPaused = false
                        isBreakTime = false
                        timeRemaining = selectedSession.workMinutes * 60
                        totalTime = selectedSession.workMinutes * 60
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
            }
            
            // Main Play/Pause Button - White with black icon
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
                    .clickable {
                        if (!isRunning) {
                            isRunning = true
                            isPaused = false
                        } else {
                            isPaused = !isPaused
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!isRunning || isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (!isRunning || isPaused) "Start" else "Pause",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            // Skip Button (for breaks)
            if (isRunning && isBreakTime) {
                Spacer(modifier = Modifier.width(24.dp))
                
                IconButton(
                    onClick = {
                        isBreakTime = false
                        timeRemaining = selectedSession.workMinutes * 60
                        totalTime = selectedSession.workMinutes * 60
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip Break",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Today's Stats with border
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = BorderGlow,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "⏱️",
                    value = "${totalFocusMinutes}m",
                    label = "Focus Time"
                )
                StatItem(
                    icon = "🎯",
                    value = "$completedSessions",
                    label = "Sessions"
                )
                StatItem(
                    icon = "🔥",
                    value = "${completedSessions * 25}",
                    label = "Points"
                )
            }
        }
    }
}

@Composable
fun SessionCard(
    session: FocusSession,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) GlowWhiteMedium else BorderGlow,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = session.icon,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = session.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${session.workMinutes}/${session.breakMinutes}m",
                fontSize = 12.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
