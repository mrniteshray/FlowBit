package xcom.niteshray.xapps.xblockit.feature.focus.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xcom.niteshray.xapps.xblockit.MainActivity
import xcom.niteshray.xapps.xblockit.R
import xcom.niteshray.xapps.xblockit.feature.focus.audio.FocusNoisePlayer
import xcom.niteshray.xapps.xblockit.feature.focus.audio.NoiseType
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import xcom.niteshray.xapps.xblockit.feature.focus.widget.FocusWidget
import xcom.niteshray.xapps.xblockit.feature.focus.widget.FocusWidgetKeys

data class FocusState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isBreakTime: Boolean = false,
    val timeRemaining: Int = 25 * 60,
    val totalTime: Int = 25 * 60,
    val currentNoise: NoiseType = NoiseType.BROWN,
    val completedSessions: Int = 0
)

object FocusManager {
    private val _state = MutableStateFlow(FocusState())
    val state = _state.asStateFlow()

    fun updateState(newState: FocusState) {
        _state.value = newState
    }
}

class FocusService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null
    private val noisePlayer = FocusNoisePlayer()
    
    // State buffer
    private var currentState = FocusState()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        currentState = FocusManager.state.value
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_RESET -> resetTimer()
            ACTION_SET_NOISE -> {
                 val noiseName = intent.getStringExtra(EXTRA_NOISE_TYPE)
                 val noise = try {
                     NoiseType.valueOf(noiseName ?: "BROWN")
                 } catch (e: Exception) { NoiseType.BROWN }
                 setNoise(noise)
            }
        }
        return START_STICKY
    }

    private fun updateState(newState: FocusState) {
        currentState = newState
        FocusManager.updateState(newState)
        updateNotification()
        updateWidget()
    }

    private fun startTimer() {
        if (currentState.isRunning && !currentState.isPaused) return
        
        val newState = currentState.copy(isRunning = true, isPaused = false)
        updateState(newState)
        
        if (currentState.currentNoise != NoiseType.OFF && !currentState.isBreakTime) {
            noisePlayer.play(currentState.currentNoise)
        }
        
        startForeground(NOTIFICATION_ID, buildNotification())
        startTimerJob()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun pauseTimer() {
        val newState = currentState.copy(isPaused = true)
        updateState(newState)
        noisePlayer.stop()
        timerJob?.cancel()
        updateNotification() 
    }

    private fun resetTimer() {
        timerJob?.cancel()
        noisePlayer.stop()
        val newState = FocusState(
            isRunning = false,
            isPaused = false,
            isBreakTime = false,
            timeRemaining = 25 * 60,
            totalTime = 25 * 60,
            currentNoise = currentState.currentNoise,
            completedSessions = currentState.completedSessions
        )
        updateState(newState)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setNoise(noise: NoiseType) {
        val newState = currentState.copy(currentNoise = noise)
        updateState(newState)
        if (currentState.isRunning && !currentState.isPaused && !currentState.isBreakTime) {
            noisePlayer.setNoiseType(noise)
        }
    }

    private fun startTimerJob() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (currentState.isRunning && !currentState.isPaused && currentState.timeRemaining > 0) {
                delay(1000L)
                val newTime = currentState.timeRemaining - 1
                
                // Check for completion
                if (newTime <= 0) {
                     handleSessionCompletion()
                } else {
                     updateState(currentState.copy(timeRemaining = newTime))
                }
            }
        }
    }

    private fun handleSessionCompletion() {
        if (!currentState.isBreakTime) {
            // Work done, start break
            val newState = currentState.copy(
                isBreakTime = true,
                timeRemaining = 5 * 60,
                totalTime = 5 * 60,
                completedSessions = currentState.completedSessions + 1
            )
            updateState(newState)
            noisePlayer.stop()
        } else {
            // Break done, start work
            val newState = currentState.copy(
                isBreakTime = false,
                timeRemaining = 25 * 60,
                totalTime = 25 * 60
            )
            updateState(newState)
            if (currentState.currentNoise != NoiseType.OFF) {
                noisePlayer.play(currentState.currentNoise)
            }
        }
    }
    
    // updateWidget function to trigger Glance update using Preferences
    private fun updateWidget() {
        serviceScope.launch {
            try {
                val manager = GlanceAppWidgetManager(applicationContext)
                val widget = FocusWidget()
                val glanceIds = manager.getGlanceIds(widget.javaClass)
                
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(applicationContext, glanceId) { prefs ->
                        prefs[FocusWidgetKeys.isRunning] = currentState.isRunning
                        prefs[FocusWidgetKeys.isPaused] = currentState.isPaused
                        prefs[FocusWidgetKeys.isBreakTime] = currentState.isBreakTime
                        prefs[FocusWidgetKeys.timeRemaining] = currentState.timeRemaining
                        prefs[FocusWidgetKeys.totalTime] = currentState.totalTime
                    }
                    widget.update(applicationContext, glanceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (currentState.isRunning) {
            manager.notify(NOTIFICATION_ID, buildNotification())
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val title = if (currentState.isBreakTime) "Break Time ☕" else "Focus Mode 🎯"
        val time = formatTime(currentState.timeRemaining)
        val content = if (currentState.isPaused) "Paused ($time)" else "Running ($time)"

        // Add actions like Pause/Resume eventually
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.logo) // Ensure this resource exists, otherwise use a default
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows focus timer status"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        noisePlayer.release()
    }

    companion object {
        const val CHANNEL_ID = "focus_service_channel"
        const val NOTIFICATION_ID = 2001
        
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_RESET = "ACTION_RESET"
        const val ACTION_SET_NOISE = "ACTION_SET_NOISE"
        
        const val EXTRA_NOISE_TYPE = "EXTRA_NOISE_TYPE"
    }
}
