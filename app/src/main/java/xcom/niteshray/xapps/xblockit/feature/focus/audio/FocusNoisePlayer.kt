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
 * - Supports both mono and stereo output (for binaural beats)
 * - Smooth transitions between sound types
 * - Automatic resource cleanup
 * 
 * Usage:
 * ```kotlin
 * val player = FocusNoisePlayer()
 * player.play(NoiseType.SOFT_RAIN)
 * // ... later
 * player.setNoiseType(NoiseType.BINAURAL_ALPHA)
 * // ... when done
 * player.release()
 * ```
 */
class FocusNoisePlayer {
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val MONO = AudioFormat.CHANNEL_OUT_MONO
        private const val STEREO = AudioFormat.CHANNEL_OUT_STEREO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var currentNoiseType: NoiseType = NoiseType.WHITE
    private var currentChannelConfig: Int = MONO
    private var isPlaying = false
    
    /**
     * Buffer size calculated based on channel configuration
     */
    private fun getBufferSize(channelConfig: Int): Int {
        return AudioTrack.getMinBufferSize(SAMPLE_RATE, channelConfig, AUDIO_FORMAT)
            .coerceAtLeast(4096)
    }
    
    /**
     * Start playing the specified sound type.
     * 
     * Automatically handles mono/stereo switching for binaural beats.
     * If already playing, will transition to new sound type.
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
            return
        }
        
        isPlaying = true
        NoiseGenerator.reset()
        
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
            val sampleCount = bufferSize / 2 // Half buffer for smoother streaming
            
            while (isActive && isPlaying) {
                val samples = NoiseGenerator.generate(currentNoiseType, sampleCount)
                
                try {
                    audioTrack?.write(samples, 0, samples.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }
    
    /**
     * Stop sound playback and release resources.
     */
    fun stop() {
        stopInternal()
    }
    
    /**
     * Internal stop without clearing playing state completely.
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
    }
    
    /**
     * Change sound type while playing.
     * 
     * Handles automatic stereo/mono switching if needed.
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
        stop()
    }
}
