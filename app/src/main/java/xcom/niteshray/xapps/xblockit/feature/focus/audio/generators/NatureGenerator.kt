package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates soft, natural-sounding ambient sounds.
 * 
 * Design principles:
 * - Extremely soft and non-intrusive
 * - Natural, organic flow
 * - No harsh frequencies
 * - Suitable for hours of listening
 */
object NatureGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val MAX_16BIT = 32767
    
    // Very soft amplitude for background listening
    private const val AMPLITUDE = 0.12f
    
    // === Rain State ===
    private val rainDroplets = mutableListOf<SoftDroplet>()
    private var rainBaseNoise = 0.0
    
    // === Ocean State ===
    private var wavePhase = 0.0
    private var oceanBrownState = 0.0
    private var oceanSmoothState = 0.0
    
    // === Wind State ===
    private var windLfoPhase = 0.0
    private var windBrownState = 0.0
    private var windSmoothState = 0.0
    
    // Shared pink noise state
    private val pinkRows = IntArray(16) { 0 }
    private var pinkRunningSum = 0
    private var pinkIndex = 0
    private var pinkSmoothState = 0.0
    
    /**
     * Generate soft rain ambience.
     * Very gentle, like distant rain on a window.
     */
    fun generateSoftRain(sampleCount: Int): ShortArray {
        return ShortArray(sampleCount) { i ->
            // Very soft pink noise base (distant rain sound)
            val pinkSample = generateSmoothPinkSample() * 0.6
            
            // Occasional very soft droplet
            if (Random.nextFloat() < 0.0001f) { // Much rarer droplets
                rainDroplets.add(SoftDroplet(
                    amplitude = Random.nextFloat() * 0.15f + 0.05f, // Softer
                    frequency = Random.nextFloat() * 1500f + 2000f, // Lower freq
                    decay = 0.9985f, // Slower decay for softer sound
                    phase = 0.0
                ))
            }
            
            // Process droplets with soft envelope
            var dropletSum = 0.0
            val iterator = rainDroplets.iterator()
            while (iterator.hasNext()) {
                val droplet = iterator.next()
                // Soft sine for droplet (not harsh)
                val dropSound = sin(droplet.phase) * droplet.amplitude
                dropletSum += dropSound * smoothEnvelope(droplet.amplitude)
                
                droplet.phase += 2.0 * PI * droplet.frequency / SAMPLE_RATE
                droplet.amplitude *= droplet.decay
                
                if (droplet.amplitude < 0.001f) iterator.remove()
            }
            
            // Limit droplets
            while (rainDroplets.size > 10) {
                rainDroplets.removeAt(0)
            }
            
            // Smooth the final mix
            rainBaseNoise = rainBaseNoise * 0.8 + (pinkSample + dropletSum * 0.2) * 0.2
            
            val sample = rainBaseNoise * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Generate gentle ocean waves.
     * Slow, rhythmic, deeply calming.
     */
    fun generateOceanWaves(sampleCount: Int): ShortArray {
        // Very slow wave period for natural feel
        val wavePeriodSec = 12.0 // 12 seconds per wave
        val waveIncrement = 1.0 / (SAMPLE_RATE * wavePeriodSec)
        
        return ShortArray(sampleCount) {
            // Smooth brown noise base
            val white = Random.nextDouble() * 2.0 - 1.0
            oceanBrownState = (oceanBrownState + 0.01 * white) / 1.01
            
            // Extra smoothing
            oceanSmoothState = oceanSmoothState * 0.9 + oceanBrownState * 0.1
            
            // Smooth wave envelope with long tail
            wavePhase += waveIncrement
            if (wavePhase > 1.0) wavePhase -= 1.0
            
            // Asymmetric, natural wave shape
            // Slow build, gentle crest, long fade
            val waveShape = sin(wavePhase * PI) // Half sine for natural wave shape
            val envelope = waveShape * waveShape * 0.5 + 0.3 // Soft dynamics
            
            val sample = oceanSmoothState * 2.0 * envelope * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Generate gentle wind ambience.
     * Soft, natural, barely there.
     */
    fun generateWind(sampleCount: Int): ShortArray {
        // Very slow LFO for gentle gusting
        val lfoFrequency = 0.05 // 20 second period
        val lfoIncrement = 2.0 * PI * lfoFrequency / SAMPLE_RATE
        
        return ShortArray(sampleCount) {
            // Very smooth brown noise
            val white = Random.nextDouble() * 2.0 - 1.0
            windBrownState = (windBrownState + 0.008 * white) / 1.008
            
            // Extra smoothing layer
            windSmoothState = windSmoothState * 0.95 + windBrownState * 0.05
            
            // Multi-layered slow LFO for natural variation
            windLfoPhase += lfoIncrement
            if (windLfoPhase > 2.0 * PI) windLfoPhase -= 2.0 * PI
            
            val lfo1 = sin(windLfoPhase) * 0.2
            val lfo2 = sin(windLfoPhase * 0.3) * 0.15
            val lfo3 = sin(windLfoPhase * 0.7) * 0.1
            
            // Combined envelope - always stays soft
            val combinedLfo = (lfo1 + lfo2 + lfo3 + 0.55).coerceIn(0.2, 0.8)
            
            val sample = windSmoothState * 2.0 * combinedLfo * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Generate smoothed pink noise sample
     */
    private fun generateSmoothPinkSample(): Double {
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
        val rawPink = (pinkRunningSum + white) / 65536.0
        
        // Smooth the pink noise
        pinkSmoothState = pinkSmoothState * 0.7 + rawPink * 0.3
        return pinkSmoothState
    }
    
    /**
     * Soft envelope for natural attack/decay
     */
    private fun smoothEnvelope(amplitude: Float): Double {
        // Soft curve that makes sounds feel natural
        return (1.0 - exp(-amplitude.toDouble() * 10.0))
    }
    
    /**
     * Reset all internal state
     */
    fun reset() {
        rainDroplets.clear()
        rainBaseNoise = 0.0
        wavePhase = 0.0
        oceanBrownState = 0.0
        oceanSmoothState = 0.0
        windLfoPhase = 0.0
        windBrownState = 0.0
        windSmoothState = 0.0
        pinkRows.fill(0)
        pinkRunningSum = 0
        pinkIndex = 0
        pinkSmoothState = 0.0
    }
    
    /**
     * Soft droplet for rain sound
     */
    private data class SoftDroplet(
        var amplitude: Float,
        val frequency: Float,
        val decay: Float,
        var phase: Double
    )
}
