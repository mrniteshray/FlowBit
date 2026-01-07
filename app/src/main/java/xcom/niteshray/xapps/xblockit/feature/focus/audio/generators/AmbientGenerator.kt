package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates ambient musical drones and tones.
 * 
 * These are warm, sustained sounds designed to provide
 * a consistent audio bed without being distracting.
 * Perfect for deep work sessions.
 */
object AmbientGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val AMPLITUDE = 0.25f
    private const val MAX_16BIT = 32767
    
    // === Lo-Fi Drone State ===
    // Multiple oscillators at musical intervals
    private var dronePhase1 = 0.0  // Root note
    private var dronePhase2 = 0.0  // Fifth
    private var dronePhase3 = 0.0  // Octave
    private var dronePhase4 = 0.0  // Sub-octave
    private var droneWobble = 0.0  // Slow pitch variation
    
    // === Deep Hum State ===
    private var humPhase = 0.0
    private var humHarmonic2 = 0.0
    private var humHarmonic3 = 0.0
    private var humBreathPhase = 0.0
    
    // Base frequencies
    private const val DRONE_ROOT = 110.0  // A2 - warm bass frequency
    private const val HUM_FREQ = 55.0     // A1 - deep bass
    
    /**
     * Generate a lo-fi style drone.
     * 
     * Multiple detuned sine waves at consonant intervals
     * with subtle pitch wobble for analog warmth.
     * Think: Ambient music, synthesizer pads
     */
    fun generateLoFiDrone(sampleCount: Int): ShortArray {
        val root = DRONE_ROOT
        val fifth = root * 1.5       // Perfect fifth
        val octave = root * 2.0      // Octave
        val subOctave = root * 0.5   // Sub bass
        
        // Subtle detuning for warmth (in cents)
        val detune1 = 1.0 + (sin(droneWobble) * 0.002)
        val detune2 = 1.0 + (sin(droneWobble * 1.3) * 0.003)
        val detune3 = 1.0 + (sin(droneWobble * 0.7) * 0.002)
        
        val wobbleIncrement = 2.0 * PI * 0.05 / SAMPLE_RATE // Very slow
        
        return ShortArray(sampleCount) {
            // Update wobble
            droneWobble += wobbleIncrement
            if (droneWobble > 2.0 * PI) droneWobble -= 2.0 * PI
            
            // Generate layered sine waves
            val wave1 = sin(dronePhase1) * 0.35  // Root - loudest
            val wave2 = sin(dronePhase2) * 0.25  // Fifth
            val wave3 = sin(dronePhase3) * 0.20  // Octave
            val wave4 = sin(dronePhase4) * 0.20  // Sub-octave
            
            // Advance phases with detuning
            dronePhase1 += 2.0 * PI * root * detune1 / SAMPLE_RATE
            dronePhase2 += 2.0 * PI * fifth * detune2 / SAMPLE_RATE
            dronePhase3 += 2.0 * PI * octave * detune3 / SAMPLE_RATE
            dronePhase4 += 2.0 * PI * subOctave / SAMPLE_RATE
            
            // Keep phases bounded
            if (dronePhase1 > 2.0 * PI) dronePhase1 -= 2.0 * PI
            if (dronePhase2 > 2.0 * PI) dronePhase2 -= 2.0 * PI
            if (dronePhase3 > 2.0 * PI) dronePhase3 -= 2.0 * PI
            if (dronePhase4 > 2.0 * PI) dronePhase4 -= 2.0 * PI
            
            // Mix with soft saturation
            val mix = (wave1 + wave2 + wave3 + wave4)
            val saturated = softSaturate(mix)
            
            (saturated * AMPLITUDE * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Generate a deep hum.
     * 
     * Low bass frequency with subtle harmonics.
     * Similar to electrical hum but musical and warm.
     * Great for grounding and focus.
     */
    fun generateDeepHum(sampleCount: Int): ShortArray {
        val fundamental = HUM_FREQ
        val harmonic2Freq = fundamental * 2.0  // First harmonic
        val harmonic3Freq = fundamental * 3.0  // Second harmonic
        
        val breathRate = 0.08 // Very slow "breathing" modulation
        val breathIncrement = 2.0 * PI * breathRate / SAMPLE_RATE
        
        return ShortArray(sampleCount) {
            // Slow amplitude "breathing" for organic feel
            humBreathPhase += breathIncrement
            if (humBreathPhase > 2.0 * PI) humBreathPhase -= 2.0 * PI
            val breath = (sin(humBreathPhase) * 0.15 + 0.85) // 70-100% amplitude
            
            // Generate fundamental and harmonics
            val fund = sin(humPhase) * 0.6
            val harm2 = sin(humHarmonic2) * 0.25
            val harm3 = sin(humHarmonic3) * 0.15
            
            // Advance phases
            humPhase += 2.0 * PI * fundamental / SAMPLE_RATE
            humHarmonic2 += 2.0 * PI * harmonic2Freq / SAMPLE_RATE
            humHarmonic3 += 2.0 * PI * harmonic3Freq / SAMPLE_RATE
            
            // Keep phases bounded
            if (humPhase > 2.0 * PI) humPhase -= 2.0 * PI
            if (humHarmonic2 > 2.0 * PI) humHarmonic2 -= 2.0 * PI
            if (humHarmonic3 > 2.0 * PI) humHarmonic3 -= 2.0 * PI
            
            // Add very subtle noise for texture
            val noise = (Random.nextDouble() - 0.5) * 0.02
            
            val sample = (fund + harm2 + harm3 + noise) * breath * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().toShort()
        }
    }
    
    /**
     * Soft saturation for analog warmth
     * Gently compresses peaks for a warmer sound
     */
    private fun softSaturate(x: Double): Double {
        return when {
            x > 1.0 -> 1.0
            x < -1.0 -> -1.0
            else -> x - (x * x * x) / 3.0 // Soft clip curve
        }
    }
    
    /**
     * Reset all internal state
     */
    fun reset() {
        dronePhase1 = 0.0
        dronePhase2 = 0.0
        dronePhase3 = 0.0
        dronePhase4 = 0.0
        droneWobble = 0.0
        humPhase = 0.0
        humHarmonic2 = 0.0
        humHarmonic3 = 0.0
        humBreathPhase = 0.0
    }
}
