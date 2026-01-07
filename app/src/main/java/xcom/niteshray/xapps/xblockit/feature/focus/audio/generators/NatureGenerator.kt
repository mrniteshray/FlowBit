package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates nature-inspired ambient sounds.
 * 
 * These are synthesized approximations designed for focus,
 * not field recordings. They provide consistent, loopable
 * ambient backgrounds that mask distractions.
 */
object NatureGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val AMPLITUDE = 0.3f
    private const val MAX_16BIT = 32767
    
    // === Rain State ===
    private var rainPhase = 0.0
    private val rainDroplets = mutableListOf<Droplet>()
    
    // === Ocean State ===
    private var oceanPhase = 0.0
    private var waveEnvelope = 0.0
    private var brownState = 0.0
    
    // === Wind State ===
    private var windPhase = 0.0
    private var windLfoPhase = 0.0
    private var windBrownState = 0.0
    
    // Pink noise state (shared)
    private val pinkRows = IntArray(16) { 0 }
    private var pinkRunningSum = 0
    private var pinkIndex = 0
    
    /**
     * Generate soft rain ambience.
     * 
     * Combines filtered pink noise (steady rain) with
     * occasional random droplet impulses for realism.
     */
    fun generateSoftRain(sampleCount: Int): ShortArray {
        return ShortArray(sampleCount) { i ->
            // Base: Soft pink noise (rain bed)
            val pinkSample = generatePinkSample() * 0.4
            
            // Add random droplet sounds
            if (Random.nextFloat() < 0.0003f) {
                rainDroplets.add(Droplet(
                    amplitude = Random.nextFloat() * 0.3f + 0.1f,
                    frequency = Random.nextFloat() * 2000f + 3000f,
                    decay = Random.nextFloat() * 0.0005f + 0.0002f,
                    phase = 0.0
                ))
            }
            
            // Process active droplets
            var dropletSum = 0.0
            val iterator = rainDroplets.iterator()
            while (iterator.hasNext()) {
                val droplet = iterator.next()
                dropletSum += sin(droplet.phase) * droplet.amplitude
                droplet.phase += 2.0 * PI * droplet.frequency / SAMPLE_RATE
                droplet.amplitude *= (1.0f - droplet.decay)
                if (droplet.amplitude < 0.001f) iterator.remove()
            }
            
            // Limit droplet count to avoid memory issues
            while (rainDroplets.size > 20) {
                rainDroplets.removeAt(0)
            }
            
            val sample = (pinkSample + dropletSum * 0.3) * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate ocean waves.
     * 
     * Brown noise modulated by a slow sine wave envelope
     * to create the rhythmic rise and fall of waves.
     * Wave period: ~8-12 seconds for realistic feel.
     */
    fun generateOceanWaves(sampleCount: Int): ShortArray {
        val wavePeriod = 10.0 // seconds per wave cycle
        val waveIncrement = 1.0 / (SAMPLE_RATE * wavePeriod)
        
        return ShortArray(sampleCount) {
            // Generate brown noise base
            val white = Random.nextDouble() * 2.0 - 1.0
            brownState = (brownState + 0.02 * white) / 1.02
            val brownSample = brownState * 3.5
            
            // Apply wave envelope (smooth rise and fall)
            waveEnvelope += waveIncrement
            if (waveEnvelope > 1.0) waveEnvelope -= 1.0
            
            // Create asymmetric wave shape (longer retreat than approach)
            val envelope = (sin(waveEnvelope * 2.0 * PI) * 0.4 + 0.6)
            
            val sample = brownSample * envelope * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate wind ambience.
     * 
     * Filtered brown noise with a slow LFO (Low Frequency Oscillator)
     * modulating the intensity to create gusting effect.
     */
    fun generateWind(sampleCount: Int): ShortArray {
        val lfoFrequency = 0.1 // Very slow oscillation (10 sec period)
        val lfoIncrement = 2.0 * PI * lfoFrequency / SAMPLE_RATE
        
        return ShortArray(sampleCount) {
            // Brown noise base
            val white = Random.nextDouble() * 2.0 - 1.0
            windBrownState = (windBrownState + 0.015 * white) / 1.015
            
            // LFO for gusting effect
            windLfoPhase += lfoIncrement
            if (windLfoPhase > 2.0 * PI) windLfoPhase -= 2.0 * PI
            
            // Multi-layered LFO for more natural variation
            val lfo1 = sin(windLfoPhase) * 0.3
            val lfo2 = sin(windLfoPhase * 0.37) * 0.2 // Slower, offset
            val lfo3 = sin(windLfoPhase * 2.1) * 0.1  // Faster flutter
            val combinedLfo = (lfo1 + lfo2 + lfo3 + 0.7).coerceIn(0.3, 1.0)
            
            val sample = windBrownState * 3.0 * combinedLfo * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate a single pink noise sample using Voss-McCartney algorithm
     */
    private fun generatePinkSample(): Double {
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
        return (pinkRunningSum + white) / 65536.0
    }
    
    /**
     * Reset all internal state
     */
    fun reset() {
        rainPhase = 0.0
        rainDroplets.clear()
        oceanPhase = 0.0
        waveEnvelope = 0.0
        brownState = 0.0
        windPhase = 0.0
        windLfoPhase = 0.0
        windBrownState = 0.0
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
    }
    
    /**
     * Internal class to track individual rain droplet sounds
     */
    private data class Droplet(
        var amplitude: Float,
        val frequency: Float,
        val decay: Float,
        var phase: Double
    )
}
