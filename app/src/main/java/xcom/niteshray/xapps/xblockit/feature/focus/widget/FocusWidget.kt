package xcom.niteshray.xapps.xblockit.feature.focus.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import xcom.niteshray.xapps.xblockit.R
import xcom.niteshray.xapps.xblockit.feature.focus.service.FocusService

// Keys for storing state in Glance Preferences
object FocusWidgetKeys {
    val isRunning = booleanPreferencesKey("isRunning")
    val isPaused = booleanPreferencesKey("isPaused")
    val isBreakTime = booleanPreferencesKey("isBreakTime")
    val timeRemaining = intPreferencesKey("timeRemaining")
    val totalTime = intPreferencesKey("totalTime")
}

class FocusWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                Content(prefs, context)
            }
        }
    }

    @Composable
    private fun Content(prefs: Preferences, context: Context) {
        // Read state from prefs with defaults
        val isRunning = prefs[FocusWidgetKeys.isRunning] ?: false
        val isPaused = prefs[FocusWidgetKeys.isPaused] ?: false
        val isBreakTime = prefs[FocusWidgetKeys.isBreakTime] ?: false
        val timeRemaining = prefs[FocusWidgetKeys.timeRemaining] ?: (25 * 60)
        val totalTime = prefs[FocusWidgetKeys.totalTime] ?: (25 * 60)

        // Dark theme background
        val backgroundColor = ColorProvider(Color(0xFF121212)) 
        val primaryColor = ColorProvider(Color(0xFF4CAF50)) // Green for Focus
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                // Header (Focus / Break)
                Text(
                    text = if (isBreakTime) "BREAK TIME" else "FOCUS SESSION",
                    style = TextStyle(
                        color = ColorProvider(Color.White.copy(alpha = 0.6f)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Timer
                Text(
                    text = formatTime(timeRemaining),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                // Progress Bar (Background)
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(ColorProvider(Color.DarkGray))
                ) {
                    // Simple progress bar
                    // Note: Glance Box doesn't support float weights directly in same container easily without Row.
                }
                
                // Progress Bar Simulation
                Row(
                   modifier = GlanceModifier
                       .fillMaxWidth()
                       .height(6.dp)
                       .padding(vertical = 2.dp)
                ) {
                    val progress = if (totalTime > 0) timeRemaining.toFloat() / totalTime else 0f
                    // Filled part
                    if (progress > 0) {
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .height(4.dp)
                                .background(if (isBreakTime) ColorProvider(Color.Cyan) else primaryColor)
                        ) {}
                    }
                    // Empty part (inverse)
                     val emptyWeight = 1f - progress
                     if (emptyWeight > 0.01f) {
                         Box(
                             modifier = GlanceModifier
                                 .defaultWeight()
                                 .height(4.dp)
                                 .background(ColorProvider(Color.Transparent)) // Transparent for empty
                        ) {}
                     }
                }
                
                Spacer(modifier = GlanceModifier.height(16.dp))

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Reset Button
                     if (timeRemaining != totalTime || isRunning) {
                         ControlIcon(
                             iconRes = R.drawable.ic_reset, 
                             action = actionStartService(getServiceIntent(context, FocusService.ACTION_RESET)),
                             desc = "Reset",
                             color = Color.DarkGray
                         )
                         Spacer(modifier = GlanceModifier.width(32.dp))
                     }

                    // Play/Pause Button
                    val isPlaying = isRunning && !isPaused
                    val playPauseAction = if (isPlaying) {
                        actionStartService(getServiceIntent(context, FocusService.ACTION_PAUSE))
                    } else {
                        if (isRunning) { // Paused
                             actionStartService(getServiceIntent(context, FocusService.ACTION_RESUME))
                        } else { // Stopped
                             actionStartService(getServiceIntent(context, FocusService.ACTION_START))
                        }
                    }
                    
                    ControlIcon(
                        iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                        action = playPauseAction,
                        desc = if (isPlaying) "Pause" else "Start",
                        color = if (isPlaying) Color(0xFFE53935) else Color(0xFF4CAF50), // Red for Stop, Green for Go
                        size = 56.dp,
                        iconSize = 32.dp
                    )
                }
            }
        }
    }

    @Composable
    private fun ControlIcon(
        iconRes: Int,
        action: Action,
        desc: String,
        color: Color,
        size: androidx.compose.ui.unit.Dp = 48.dp,
        iconSize: androidx.compose.ui.unit.Dp = 24.dp
    ) {
         Box(
             modifier = GlanceModifier
                 .size(size)
                 // Safe bet: just background color.
                 .background(ColorProvider(color))
                 .clickable(action),
             contentAlignment = Alignment.Center
         ) {
             Image(
                 provider = ImageProvider(iconRes),
                 contentDescription = desc,
                 modifier = GlanceModifier.size(iconSize)
             )
         }
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
    
    private fun getServiceIntent(context: Context, action: String): Intent {
        return Intent(context, FocusService::class.java).apply {
            this.action = action
        }
    }
}
