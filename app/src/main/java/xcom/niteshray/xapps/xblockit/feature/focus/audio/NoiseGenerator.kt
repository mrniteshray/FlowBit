package xcom.niteshray.xapps.xblockit.feature.focus.audio

import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.AmbientGenerator
import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.BinauralGenerator
import xcom.niteshray.xapps.xblockit.feature.focus.audio.generators.NatureGenerator
import kotlin.random.Random

/**
 * Central audio generation facade.
 * 
 * Routes generation requests to specialized generators
 * based on sound type. Handles both mono and stereo output.
 * 
 * Architecture:
 * - Classic noise (white, brown, pink): Generated locally
 * - Brainwave: Delegated to BinauralGenerator (stereo)
 * - Nature: Delegated to NatureGenerator
 * - Ambient: Delegated to AmbientGenerator
 */
object NoiseGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val AMPLITUDE = 0.3f
    private const val MAX_16BIT = 32767
    
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
     * 
     * @param type The noise type to generate
     * @param bufferSize Number of samples to generate
     * @return ShortArray of PCM samples (mono or stereo depending on type)
     */
    fun generate(type: NoiseType, bufferSize: Int): ShortArray {
        return when (type) {
            // Classic noise types (mono)
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
     * Binaural beats require headphones and stereo for effect.
     */
    fun requiresStereo(type: NoiseType): Boolean {
        return type.requiresStereo
    }
    
    /**
     * Reset all generator states.
     * Call when changing noise type for clean transition.
     */
    fun reset() {
        // Local state
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
        brownLastOutput = 0.0
        
        // Delegated generators
        BinauralGenerator.reset()
        NatureGenerator.reset()
        AmbientGenerator.reset()
    }
    
    // ═══════════════════════════════════════════════════════════════
    // Classic Noise Generators (Mono)
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * White noise: Random samples with uniform distribution.
     * Sounds like static or hissing.
     */
    private fun generateWhiteNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val sample = (Random.nextFloat() * 2f - 1f) * AMPLITUDE
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Brown noise: Integrated white noise (random walk).
     * Sounds like a waterfall or rumbling wind.
     * Heavy on bass frequencies.
     */
    private fun generateBrownNoise(bufferSize: Int): ShortArray {
        return ShortArray(bufferSize) {
            val white = Random.nextDouble() * 2.0 - 1.0
            brownLastOutput = (brownLastOutput + (0.02 * white)) / 1.02
            
            val sample = (brownLastOutput * 3.5 * AMPLITUDE)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Pink noise: 1/f spectrum using Voss-McCartney algorithm.
     * Balanced between white (hissy) and brown (rumbly).
     * Often considered most natural and easy to listen to.
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
            
            val sample = (pinkSample * AMPLITUDE * 0.5)
                .coerceIn(-1.0, 1.0)
            
            (sample * MAX_16BIT).toInt().toShort()
        }
    }
}
