package xcom.niteshray.xapps.xblockit.feature.focus.audio

import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Generates PCM audio samples for different noise types.
 * 
 * Noise generation algorithms:
 * - White: Random samples (uniform distribution)
 * - Brown: Integrated white noise (random walk)
 * - Pink: Voss-McCartney algorithm for 1/f noise
 */
object NoiseGenerator {
    
    private const val AMPLITUDE = 0.3f // Volume level (0.0 - 1.0)
    private const val MAX_16BIT = 32767
    
    // Pink noise state (Voss-McCartney algorithm)
    private val pinkRows = IntArray(16) { 0 }
    private var pinkRunningSum = 0
    private var pinkIndex = 0
    
    // Brown noise state
    private var brownLastOutput = 0.0
    
    /**
     * Generate a buffer of white noise samples
     */
    fun generateWhiteNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val sample = (Random.nextFloat() * 2f - 1f) * AMPLITUDE
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate a buffer of brown (Brownian/red) noise samples
     * Brown noise has more bass, sounds like a waterfall or wind
     */
    fun generateBrownNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            // Random walk with leaky integration
            val white = Random.nextDouble() * 2.0 - 1.0
            brownLastOutput = (brownLastOutput + (0.02 * white)) / 1.02
            
            // Normalize and apply amplitude
            val sample = (brownLastOutput * 3.5 * AMPLITUDE)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate a buffer of pink noise samples
     * Pink noise (1/f) is balanced between white and brown
     * Uses Voss-McCartney algorithm for efficient generation
     */
    fun generatePinkNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            // Voss-McCartney algorithm
            val lastIndex = pinkIndex
            pinkIndex++
            
            if (pinkIndex >= 65536) pinkIndex = 0
            
            // Find which rows need updating
            var diff = lastIndex xor pinkIndex
            var row = 0
            
            while (diff > 0 && row < 16) {
                if (diff and 1 == 1) {
                    pinkRunningSum -= pinkRows[row]
                    val newValue = (Random.nextInt() shr 16)
                    pinkRunningSum += newValue
                    pinkRows[row] = newValue
                }
                diff = diff shr 1
                row++
            }
            
            // Add white noise for high frequencies
            val white = (Random.nextInt() shr 16)
            val pinkSample = (pinkRunningSum + white) / 65536.0
            
            val sample = (pinkSample * AMPLITUDE * 0.5)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate noise buffer for specified type
     */
    fun generate(type: NoiseType, bufferSize: Int): ShortArray {
        return when (type) {
            NoiseType.WHITE -> generateWhiteNoise(bufferSize)
            NoiseType.BROWN -> generateBrownNoise(bufferSize)
            NoiseType.PINK -> generatePinkNoise(bufferSize)
            NoiseType.OFF -> ShortArray(bufferSize) { 0 }
        }
    }
    
    /**
     * Reset internal state (call when changing noise type)
     */
    fun reset() {
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
        brownLastOutput = 0.0
    }
}
