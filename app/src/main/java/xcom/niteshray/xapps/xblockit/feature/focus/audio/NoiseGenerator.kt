package xcom.niteshray.xapps.xblockit.feature.focus.audio

import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.AmbientGenerator
import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.BinauralGenerator
import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.NatureGenerator
import kotlin.random.Random

/**
 * Central audio generation facade with built-in smoothing.
 * 
 * All sounds are designed to be:
 * - Soft and non-harsh
 * - Natural sounding
 * - Suitable for long listening sessions
 * - Low-pass filtered to remove harsh high frequencies
 */
object NoiseGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val MAX_16BIT = 32767
    
    // Reduced amplitude for softer sound (was 0.3, now 0.15)
    private const val AMPLITUDE = 0.15f
    
    // ═══════════════════════════════════════════════════════════════
    // Smoothing Filter State (Simple low-pass filter)
    // ═══════════════════════════════════════════════════════════════
    private var lastSample = 0.0
    private const val SMOOTHING_FACTOR = 0.7 // Higher = smoother/darker sound
    
    // ═══════════════════════════════════════════════════════════════
    // Pink Noise State (Voss-McCartney algorithm)
    // ═══════════════════════════════════════════════════════════════
    private val pinkRows = IntArray(16) { 0 }
    private var pinkRunningSum = 0
    private var pinkIndex = 0
    
    // ═══════════════════════════════════════════════════════════════
    // Brown Noise State
    // ═══════════════════════════════════════════════════════════════
    private var brownLastOutput = 0.0
    
    // ═══════════════════════════════════════════════════════════════
    // Public API
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * Generate audio buffer for specified noise type.
     * All outputs are smoothed for natural sound.
     */
    fun generate(type: NoiseType, bufferSize: Int): ShortArray {
        return when (type) {
            // Classic noise types (mono) - with smoothing
            NoiseType.WHITE -> generateWhiteNoise(bufferSize)
            NoiseType.BROWN -> generateBrownNoise(bufferSize)
            NoiseType.PINK -> generatePinkNoise(bufferSize)
            
            // Brainwave types (stereo - 2x buffer size)
            NoiseType.BINAURAL_ALPHA -> BinauralGenerator.generateAlpha(bufferSize)
            NoiseType.BINAURAL_BETA -> BinauralGenerator.generateBeta(bufferSize)
            
            // Nature types (mono)
            NoiseType.SOFT_RAIN -> NatureGenerator.generateSoftRain(bufferSize)
            NoiseType.OCEAN_WAVES -> NatureGenerator.generateOceanWaves(bufferSize)
            NoiseType.WIND -> NatureGenerator.generateWind(bufferSize)
            
            // Ambient types (mono)
            NoiseType.LO_FI_DRONE -> AmbientGenerator.generateLoFiDrone(bufferSize)
            NoiseType.DEEP_HUM -> AmbientGenerator.generateDeepHum(bufferSize)
            
            // Off - silence
            NoiseType.OFF -> ShortArray(bufferSize) { 0 }
        }
    }
    
    /**
     * Check if the given noise type requires stereo output.
     */
    fun requiresStereo(type: NoiseType): Boolean = type.requiresStereo
    
    /**
     * Reset all generator states.
     */
    fun reset() {
        // Local state
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
        brownLastOutput = 0.0
        lastSample = 0.0
        
        // White noise smoothing state
        whiteSmooth1 = 0.0
        whiteSmooth2 = 0.0
        whiteSmooth3 = 0.0
        
        // Delegated generators
        BinauralGenerator.reset()
        NatureGenerator.reset()
        AmbientGenerator.reset()
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Smoothed Noise Generators
    // ═══════════════════════════════════════════════════════════════
    
    // Extra state for improved white noise
    private var whiteSmooth1 = 0.0
    private var whiteSmooth2 = 0.0
    private var whiteSmooth3 = 0.0
    
    /**
     * Premium soft white noise with multi-stage filtering.
     * 
     * Much gentler than raw white noise - sounds like soft,
     * distant static or gentle air flow. Three-stage smoothing
     * removes harshness while retaining pleasant texture.
     */
    private fun generateWhiteNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val raw = Random.nextDouble() * 2.0 - 1.0
            
            // Stage 1: Initial heavy smoothing (removes sharp transients)
            whiteSmooth1 = whiteSmooth1 * 0.85 + raw * 0.15
            
            // Stage 2: Medium smoothing (shapes frequency curve)
            whiteSmooth2 = whiteSmooth2 * 0.75 + whiteSmooth1 * 0.25
            
            // Stage 3: Final polish (extra softness)
            whiteSmooth3 = whiteSmooth3 * 0.6 + whiteSmooth2 * 0.4
            
            // Store for legacy smoothing reference
            lastSample = whiteSmooth3
            
            // Slightly boost to compensate for filtering loss, but keep soft
            val sample = whiteSmooth3 * AMPLITUDE * 1.3
            (sample * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Smooth brown noise - naturally bass-heavy and warm.
     * Already smooth due to integration, but softened further.
     */
    private fun generateBrownNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val white = Random.nextDouble() * 2.0 - 1.0
            
            // Slower integration for smoother sound (was 0.02, now 0.015)
            brownLastOutput = (brownLastOutput + (0.015 * white)) / 1.015
            
            // Gentler normalization (was 3.5, now 2.5)
            val sample = (brownLastOutput * 2.5 * AMPLITUDE)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Smooth pink noise - balanced and natural.
     * The most pleasant for long listening.
     */
    private fun generatePinkNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val lastIndex = pinkIndex
            pinkIndex++
            
            if (pinkIndex >= 65536) pinkIndex = 0
            
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
            
            val white = (Random.nextInt() shr 16)
            val pinkSample = (pinkRunningSum + white) / 65536.0
            
            // Softer amplitude (was 0.5, now 0.35)
            val sample = (pinkSample * AMPLITUDE * 0.35)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
}
