package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates soft binaural beat audio for brainwave entrainment.
 * 
 * Designed for comfortable long-term listening:
 * - Very soft volume
 * - Pure, clean sine waves
 * - No harsh frequencies
 * 
 * Requirements:
 * - MUST be listened to with headphones for effect
 * - Stereo output (left and right channels differ)
 */
object BinauralGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val MAX_16BIT = 32767
    
    // Very soft amplitude for comfortable background listening
    private const val AMPLITUDE = 0.12f
    
    // Lower base frequency for warmer, less harsh sound
    private const val BASE_FREQUENCY = 150.0  // Was 200, now warmer
    
    // Beat frequencies
    private const val ALPHA_BEAT = 10.0  // 10 Hz for relaxed focus
    private const val BETA_BEAT = 14.0   // 14 Hz for concentration (was 15)
    
    // Phase tracking
    private var leftPhase = 0.0
    private var rightPhase = 0.0
    
    // Smoothing for gentle start
    private var sampleCount = 0L
    
    /**
     * Generate alpha wave binaural beat (10 Hz)
     * Warm, relaxing, creativity-enhancing
     */
    fun generateAlpha(sampleCount: Int): ShortArray {
        return generateBinaural(sampleCount, ALPHA_BEAT)
    }
    
    /**
     * Generate beta wave binaural beat (14 Hz)
     * Focused, alert, but not harsh
     */
    fun generateBeta(sampleCount: Int): ShortArray {
        return generateBinaural(sampleCount, BETA_BEAT)
    }
    
    /**
     * Core binaural generation with smooth, warm sound
     */
    private fun generateBinaural(bufferSampleCount: Int, beatFrequency: Double): ShortArray {
        val leftFreq = BASE_FREQUENCY
        val rightFreq = BASE_FREQUENCY + beatFrequency
        
        val leftIncrement = 2.0 * PI * leftFreq / SAMPLE_RATE
        val rightIncrement = 2.0 * PI * rightFreq / SAMPLE_RATE
        
        // Stereo requires 2x samples (interleaved L/R)
        val stereoBuffer = ShortArray(bufferSampleCount * 2)
        
        for (i in 0 until bufferSampleCount) {
            // Generate pure sine waves with slight harmonic warmth
            val leftBase = sin(leftPhase)
            val rightBase = sin(rightPhase)
            
            // Add very subtle second harmonic for warmth (barely audible)
            val leftWarm = leftBase * 0.95 + sin(leftPhase * 2) * 0.05
            val rightWarm = rightBase * 0.95 + sin(rightPhase * 2) * 0.05
            
            val leftSample = (leftWarm * AMPLITUDE * MAX_16BIT).toInt()
                .coerceIn(-32768, 32767).toShort()
            val rightSample = (rightWarm * AMPLITUDE * MAX_16BIT).toInt()
                .coerceIn(-32768, 32767).toShort()
            
            // Interleave: [Left, Right, Left, Right, ...]
            stereoBuffer[i * 2] = leftSample
            stereoBuffer[i * 2 + 1] = rightSample
            
            // Advance phases
            leftPhase += leftIncrement
            rightPhase += rightIncrement
            
            // Keep phases in reasonable range
            if (leftPhase > 2.0 * PI) leftPhase -= 2.0 * PI
            if (rightPhase > 2.0 * PI) rightPhase -= 2.0 * PI
            
            sampleCount++
        }
        
        return stereoBuffer
    }
    
    /**
     * Reset phase for fresh start
     */
    fun reset() {
        leftPhase = 0.0
        rightPhase = 0.0
        sampleCount = 0L
    }
}
