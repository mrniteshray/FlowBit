package xcom.niteshray.xapps.xblockit.feature.focus.audio.generators

import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates soft, warm ambient drones and tones.
 * 
 * Design principles:
 * - Very soft and warm
 * - Slow, meditative evolution
 * - No harsh attack or decay
 * - Perfect for long focus sessions
 */
object AmbientGenerator {
    
    private const val SAMPLE_RATE = 44100
    private const val MAX_16BIT = 32767
    
    // Very soft amplitude
    private const val AMPLITUDE = 0.10f
    
    // === Lo-Fi Drone State ===
    private var dronePhase1 = 0.0  // Root
    private var dronePhase2 = 0.0  // Fifth
    private var dronePhase3 = 0.0  // Octave
    private var dronePhase4 = 0.0  // Sub
    private var droneWobble = 0.0
    private var droneSmoothOutput = 0.0
    
    // === Deep Hum State ===
    private var humPhase = 0.0
    private var humHarmonic2 = 0.0
    private var humHarmonic3 = 0.0
    private var humBreathPhase = 0.0
    private var humSmoothOutput = 0.0
    
    // Warm, low base frequencies
    private const val DRONE_ROOT = 85.0   // Low, warm (was 110)
    private const val HUM_FREQ = 45.0     // Deep bass (was 55)
    
    /**
     * Generate a warm, lo-fi style drone.
     * Soft, sustained, meditative.
     */
    fun generateLoFiDrone(sampleCount: Int): ShortArray {
        val root = DRONE_ROOT
        val fifth = root * 1.5
        val octave = root * 2.0
        val subOctave = root * 0.5
        
        // Very slow wobble for organic feel
        val wobbleIncrement = 2.0 * PI * 0.02 / SAMPLE_RATE
        
        return ShortArray(sampleCount) {
            // Update wobble (very subtle pitch variation)
            droneWobble += wobbleIncrement
            if (droneWobble > 2.0 * PI) droneWobble -= 2.0 * PI
            
            // Subtle detuning for warmth
            val detune1 = 1.0 + sin(droneWobble) * 0.001
            val detune2 = 1.0 + sin(droneWobble * 1.3) * 0.0015
            val detune3 = 1.0 + sin(droneWobble * 0.7) * 0.001
            
            // Generate soft layered sine waves
            val wave1 = sin(dronePhase1) * 0.4   // Root - prominent
            val wave2 = sin(dronePhase2) * 0.25  // Fifth - supporting
            val wave3 = sin(dronePhase3) * 0.15  // Octave - color
            val wave4 = sin(dronePhase4) * 0.2   // Sub - warmth
            
            // Advance phases
            dronePhase1 += 2.0 * PI * root * detune1 / SAMPLE_RATE
            dronePhase2 += 2.0 * PI * fifth * detune2 / SAMPLE_RATE
            dronePhase3 += 2.0 * PI * octave * detune3 / SAMPLE_RATE
            dronePhase4 += 2.0 * PI * subOctave / SAMPLE_RATE
            
            // Keep phases bounded
            if (dronePhase1 > 2.0 * PI) dronePhase1 -= 2.0 * PI
            if (dronePhase2 > 2.0 * PI) dronePhase2 -= 2.0 * PI
            if (dronePhase3 > 2.0 * PI) dronePhase3 -= 2.0 * PI
            if (dronePhase4 > 2.0 * PI) dronePhase4 -= 2.0 * PI
            
            // Mix and smooth
            val rawMix = wave1 + wave2 + wave3 + wave4
            
            // Heavy smoothing for warm, soft sound
            droneSmoothOutput = droneSmoothOutput * 0.95 + rawMix * 0.05
            
            val sample = softSaturate(droneSmoothOutput) * AMPLITUDE
            (sample * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Generate a deep, warm hum.
     * Like a gentle bass meditation tone.
     */
    fun generateDeepHum(sampleCount: Int): ShortArray {
        val fundamental = HUM_FREQ
        val harmonic2Freq = fundamental * 2.0
        val harmonic3Freq = fundamental * 3.0
        
        // Very slow breathing modulation
        val breathRate = 0.04 // ~25 second cycle
        val breathIncrement = 2.0 * PI * breathRate / SAMPLE_RATE
        
        return ShortArray(sampleCount) {
            // Slow amplitude breathing for organic feel
            humBreathPhase += breathIncrement
            if (humBreathPhase > 2.0 * PI) humBreathPhase -= 2.0 * PI
            
            // Gentle breathing curve (80-100% amplitude)
            val breath = sin(humBreathPhase) * 0.1 + 0.9
            
            // Generate fundamental and soft harmonics
            val fund = sin(humPhase) * 0.6
            val harm2 = sin(humHarmonic2) * 0.25
            val harm3 = sin(humHarmonic3) * 0.1
            
            // Advance phases
            humPhase += 2.0 * PI * fundamental / SAMPLE_RATE
            humHarmonic2 += 2.0 * PI * harmonic2Freq / SAMPLE_RATE
            humHarmonic3 += 2.0 * PI * harmonic3Freq / SAMPLE_RATE
            
            // Keep phases bounded
            if (humPhase > 2.0 * PI) humPhase -= 2.0 * PI
            if (humHarmonic2 > 2.0 * PI) humHarmonic2 -= 2.0 * PI
            if (humHarmonic3 > 2.0 * PI) humHarmonic3 -= 2.0 * PI
            
            // Tiny bit of noise for analog warmth
            val noise = (Random.nextDouble() - 0.5) * 0.008
            
            val rawMix = (fund + harm2 + harm3 + noise) * breath
            
            // Smooth the output
            humSmoothOutput = humSmoothOutput * 0.9 + rawMix * 0.1
            
            val sample = humSmoothOutput * AMPLITUDE
            (sample.coerceIn(-1.0, 1.0) * MAX_16BIT).toInt().coerceIn(-32768, 32767).toShort()
        }
    }
    
    /**
     * Soft saturation for warm, rounded sound
     */
    private fun softSaturate(x: Double): Double {
        // Gentle soft clipping that rounds off peaks
        return when {
            x > 0.8 -> 0.8 + (x - 0.8) * 0.2
            x < -0.8 -> -0.8 + (x + 0.8) * 0.2
            else -> x
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
        droneSmoothOutput = 0.0
        humPhase = 0.0
        humHarmonic2 = 0.0
        humHarmonic3 = 0.0
        humBreathPhase = 0.0
        humSmoothOutput = 0.0
    }
}
