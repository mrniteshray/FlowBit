package xcom.niteshray.xapps.xblockit.feature.focus.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*

/**
 * Manages focus sound playback using AudioTrack.
 * 
 * Features:
 * - Real-time audio generation using coroutines
 * - Smooth fade-in on start (3 seconds)
 * - Smooth fade-out on stop
 * - Supports both mono and stereo output (for binaural beats)
 * - Automatic resource cleanup
 */
class FocusNoisePlayer {
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val MONO = AudioFormat.CHANNEL_OUT_MONO
        private const val STEREO = AudioFormat.CHANNEL_OUT_STEREO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        
        // Fade durations in samples
        private const val FADE_IN_DURATION_SEC = 3.0f  // 3 second fade-in
        private const val FADE_OUT_DURATION_SEC = 1.0f // 1 second fade-out
    }
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var currentNoiseType: NoiseType = NoiseType.WHITE
    private var currentChannelConfig: Int = MONO
    private var isPlaying = false
    
    // Fade state
    private var fadeInSamples = 0
    private var fadeInProgress = 0
    private var currentVolume = 0f
    private var targetVolume = 1f
    
    /**
     * Buffer size calculated based on channel configuration
     */
    private fun getBufferSize(channelConfig: Int): Int {
        return AudioTrack.getMinBufferSize(SAMPLE_RATE, channelConfig, AUDIO_FORMAT)
            .coerceAtLeast(4096)
    }
    
    /**
     * Start playing the specified sound type with smooth fade-in.
     */
    fun play(noiseType: NoiseType = NoiseType.WHITE) {
        if (noiseType == NoiseType.OFF) {
            stop()
            return
        }
        
        val needsStereo = noiseType.requiresStereo
        val requiredChannelConfig = if (needsStereo) STEREO else MONO
        
        // If channel config changed, we need to recreate AudioTrack
        if (isPlaying && currentChannelConfig != requiredChannelConfig) {
            stopInternal()
        }
        
        currentNoiseType = noiseType
        currentChannelConfig = requiredChannelConfig
        
        if (isPlaying) {
            // Just change noise type, generator will pick up new type
            NoiseGenerator.reset()
            // Brief fade for transition
            fadeInSamples = (SAMPLE_RATE * 0.5f).toInt() // 0.5 sec transition fade
            fadeInProgress = 0
            currentVolume = 0.7f // Start at 70% for transitions
            return
        }
        
        isPlaying = true
        NoiseGenerator.reset()
        
        // Initialize fade-in
        fadeInSamples = (SAMPLE_RATE * FADE_IN_DURATION_SEC).toInt()
        fadeInProgress = 0
        currentVolume = 0f
        targetVolume = 1f
        
        val bufferSize = getBufferSize(currentChannelConfig)
        
        // Create AudioTrack with appropriate channel configuration
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(currentChannelConfig)
                    .setEncoding(AUDIO_FORMAT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        
        audioTrack?.play()
        
        // Start playback coroutine
        playbackJob = CoroutineScope(Dispatchers.IO).launch {
            val sampleCount = bufferSize / 2
            
            while (isActive && isPlaying) {
                val samples = NoiseGenerator.generate(currentNoiseType, sampleCount)
                
                // Apply fade envelope
                applyFadeEnvelope(samples)
                
                try {
                    audioTrack?.write(samples, 0, samples.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }
    
    /**
     * Apply smooth fade-in envelope to samples
     */
    private fun applyFadeEnvelope(samples: ShortArray) {
        for (i in samples.indices) {
            // Calculate current fade multiplier
            if (fadeInProgress < fadeInSamples) {
                // Smooth S-curve fade (easeInOutQuad)
                val t = fadeInProgress.toFloat() / fadeInSamples
                currentVolume = if (t < 0.5f) {
                    2f * t * t
                } else {
                    1f - (-2f * t + 2f).let { it * it } / 2f
                }
                fadeInProgress++
            } else {
                currentVolume = targetVolume
            }
            
            // Apply volume
            samples[i] = (samples[i] * currentVolume).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Stop sound playback with fade-out.
     */
    fun stop() {
        // Quick fade out by reducing volume gradually
        targetVolume = 0f
        
        // Give a brief moment for fade, then stop
        CoroutineScope(Dispatchers.IO).launch {
            delay(100) // Brief fade
            stopInternal()
        }
    }
    
    /**
     * Internal stop without fade.
     */
    private fun stopInternal() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
        audioTrack = null
        
        // Reset fade state
        currentVolume = 0f
        fadeInProgress = 0
    }
    
    /**
     * Change sound type while playing with smooth transition.
     */
    fun setNoiseType(noiseType: NoiseType) {
        if (noiseType == NoiseType.OFF) {
            stop()
            return
        }
        
        val needsStereo = noiseType.requiresStereo
        val requiredChannelConfig = if (needsStereo) STEREO else MONO
        
        // If switching between mono/stereo, restart playback
        if (isPlaying && currentChannelConfig != requiredChannelConfig) {
            currentNoiseType = noiseType
            currentChannelConfig = requiredChannelConfig
            NoiseGenerator.reset()
            
            // Restart with new channel config
            stopInternal()
            play(noiseType)
            return
        }
        
        currentNoiseType = noiseType
        NoiseGenerator.reset()
        
        // Smooth transition fade
        fadeInSamples = (SAMPLE_RATE * 0.5f).toInt()
        fadeInProgress = 0
        currentVolume = 0.5f
        
        if (!isPlaying) {
            play(noiseType)
        }
    }
    
    /**
     * Get current sound type
     */
    fun getCurrentNoiseType(): NoiseType = currentNoiseType
    
    /**
     * Check if currently playing
     */
    fun isPlaying(): Boolean = isPlaying
    
    /**
     * Check if current sound requires stereo (headphones recommended)
     */
    fun isStereoRequired(): Boolean = currentNoiseType.requiresStereo
    
    /**
     * Release all resources. Call when done with the player.
     */
    fun release() {
        stopInternal()
    }
}
