package xcom.niteshray.xapps.xblockit.feature.focus.audio

/**
 * Available focus sound types organized by category.
 * 
 * Categories:
 * - Noise: Classic noise generators (white, brown, pink)
 * - Brainwave: Binaural beats for neural entrainment
 * - Nature: Simulated natural ambience
 * - Ambient: Musical drones and tones
 */
enum class NoiseType(
    val displayName: String,
    val category: SoundCategory,
    val requiresStereo: Boolean = false
) {
    // === Noise Category ===
    WHITE("White Noise", SoundCategory.NOISE),
    BROWN("Brown Noise", SoundCategory.NOISE),
    PINK("Pink Noise", SoundCategory.NOISE),
    
    // === Brainwave Category ===
    BINAURAL_ALPHA("Alpha Waves", SoundCategory.BRAINWAVE, requiresStereo = true),
    BINAURAL_BETA("Beta Waves", SoundCategory.BRAINWAVE, requiresStereo = true),
    
    // === Nature Category ===
    SOFT_RAIN("Soft Rain", SoundCategory.NATURE),
    OCEAN_WAVES("Ocean Waves", SoundCategory.NATURE),
    WIND("Wind", SoundCategory.NATURE),
    
    // === Ambient Category ===
    LO_FI_DRONE("Lo-Fi Drone", SoundCategory.AMBIENT),
    DEEP_HUM("Deep Hum", SoundCategory.AMBIENT),
    
    // === Off ===
    OFF("Off", SoundCategory.OFF);
    
    companion object {
        /**
         * Get all sound types grouped by category (excluding OFF)
         */
        fun groupedByCategory(): Map<SoundCategory, List<NoiseType>> {
            return entries
                .filter { it != OFF }
                .groupBy { it.category }
        }
    }
}

/**
 * Sound categories for organizing the UI
 */
enum class SoundCategory(val displayName: String) {
    NOISE("Noise"),
    BRAINWAVE("Brainwave"),
    NATURE("Nature"),
    AMBIENT("Ambient"),
    OFF("Off")
}
