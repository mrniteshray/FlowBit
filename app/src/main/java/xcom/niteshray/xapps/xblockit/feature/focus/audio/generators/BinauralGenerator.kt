package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates binaural beat audio for brainwave entrainment.
 * 
 * Binaural beats work by playing slightly different frequencies
 * in each ear, causing the brain to perceive a third "beat" at
 * the difference frequency.
 * 
 * Requirements:
 * - MUST be listened to with headphones for effect
 * - Stereo output (left and right channels differ)
 * 
 * Frequency ranges:
 * - Alpha (8-12 Hz): Relaxed focus, creativity, light meditation
 * - Beta (13-30 Hz): Active concentration, alertness, problem-solving
 */
object BinauralGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val AMPLITUDE = 0.25f // Softer volume for background
    private const val MAX_16BIT = 32767
    
    // Base carrier frequency (comfortable for most people)
    private const val BASE_FREQUENCY = 200.0
    
    // Beat frequencies for different states
    private const val ALPHA_BEAT = 10.0  // 10 Hz for relaxed focus
    private const val BETA_BEAT = 15.0   // 15 Hz for active concentration
    
    // Phase tracking for continuous playback
    private var leftPhase = 0.0
    private var rightPhase = 0.0
    
    /**
     * Generate alpha wave binaural beat (10 Hz)
     * Good for: relaxed focus, creativity, reduced anxiety
     * 
     * @return Stereo interleaved samples [L, R, L, R, ...]
     */
    fun generateAlpha(sampleCount: Int): ShortArray {
        return generateBinaural(sampleCount, ALPHA_BEAT)
    }
    
    /**
     * Generate beta wave binaural beat (15 Hz)
     * Good for: active concentration, mental alertness
     * 
     * @return Stereo interleaved samples [L, R, L, R, ...]
     */
    fun generateBeta(sampleCount: Int): ShortArray {
        return generateBinaural(sampleCount, BETA_BEAT)
    }
    
    /**
     * Core binaural generation with specified beat frequency
     */
    private fun generateBinaural(sampleCount: Int, beatFrequency: Double): ShortArray {
        val leftFreq = BASE_FREQUENCY
        val rightFreq = BASE_FREQUENCY + beatFrequency
        
        val leftIncrement = 2.0 * PI * leftFreq / SAMPLE_RATE
        val rightIncrement = 2.0 * PI * rightFreq / SAMPLE_RATE
        
        // Stereo requires 2x samples (interleaved L/R)
        val stereoBuffer = ShortArray(sampleCount * 2)
        
        for (i in 0 until sampleCount) {
            // Generate pure sine waves for each ear
            val leftSample = (sin(leftPhase) * AMPLITUDE * MAX_16BIT).toInt().toShort()
            val rightSample = (sin(rightPhase) * AMPLITUDE * MAX_16BIT).toInt().toShort()
            
            // Interleave: [Left, Right, Left, Right, ...]
            stereoBuffer[i * 2] = leftSample
            stereoBuffer[i * 2 + 1] = rightSample
            
            // Advance phases
            leftPhase += leftIncrement
            rightPhase += rightIncrement
            
            // Keep phases in reasonable range
            if (leftPhase > 2.0 * PI) leftPhase -= 2.0 * PI
            if (rightPhase > 2.0 * PI) rightPhase -= 2.0 * PI
        }
        
        return stereoBuffer
    }
    
    /**
     * Reset phase for fresh start
     */
    fun reset() {
        leftPhase = 0.0
        rightPhase = 0.0
    }
}
