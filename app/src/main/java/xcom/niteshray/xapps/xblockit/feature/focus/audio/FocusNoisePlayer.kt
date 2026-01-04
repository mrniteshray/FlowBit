package xcom.niteshray.xapps.xblockit.feature.focus.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*

/**
 * Manages focus noise playback using AudioTrack.
 * 
 * Generates noise programmatically in real-time using coroutines.
 * Supports smooth transitions between noise types.
 */
class FocusNoisePlayer {
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var currentNoiseType: NoiseType = NoiseType.WHITE
    private var isPlaying = false
    
    private val bufferSize: Int by lazy {
        AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            .coerceAtLeast(4096)
    }
    
    /**
     * Start playing the specified noise type
     */
    fun play(noiseType: NoiseType = NoiseType.WHITE) {
        if (noiseType == NoiseType.OFF) {
            stop()
            return
        }
        
        currentNoiseType = noiseType
        
        if (isPlaying) {
            // Just change noise type, generator will pick up new type
            NoiseGenerator.reset()
            return
        }
        
        isPlaying = true
        NoiseGenerator.reset()
        
        // Create AudioTrack
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
                    .setChannelMask(CHANNEL_CONFIG)
                    .setEncoding(AUDIO_FORMAT)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        
        audioTrack?.play()
        
        // Start playback coroutine
        playbackJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ShortArray(bufferSize / 2) // Half buffer for smoother streaming
            
            while (isActive && isPlaying) {
                val samples = NoiseGenerator.generate(currentNoiseType, buffer.size)
                
                try {
                    audioTrack?.write(samples, 0, samples.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }
    
    /**
     * Stop noise playback
     */
    fun stop() {
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
     * Change noise type while playing
     */
    fun setNoiseType(noiseType: NoiseType) {
        if (noiseType == NoiseType.OFF) {
            stop()
            return
        }
        
        currentNoiseType = noiseType
        NoiseGenerator.reset()
        
        if (!isPlaying) {
            play(noiseType)
        }
    }
    
    /**
     * Get current noise type
     */
    fun getCurrentNoiseType(): NoiseType = currentNoiseType
    
    /**
     * Check if currently playing
     */
    fun isPlaying(): Boolean = isPlaying
    
    /**
     * Release all resources
     */
    fun release() {
        stop()
    }
}
