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
    val requiresStereo: Boolean = false,
    val isPremium: Boolean = false
) {
    // === Noise Category === (FREE)
    WHITE("White Noise", SoundCategory.NOISE, isPremium = false),
    BROWN("Brown Noise", SoundCategory.NOISE, isPremium = false),
    PINK("Pink Noise", SoundCategory.NOISE, isPremium = false),
    
    // === Brainwave Category === (PREMIUM)
    BINAURAL_ALPHA("Alpha Waves", SoundCategory.BRAINWAVE, requiresStereo = true, isPremium = true),
    BINAURAL_BETA("Beta Waves", SoundCategory.BRAINWAVE, requiresStereo = true, isPremium = true),
    
    // === Nature Category === (PREMIUM)
    SOFT_RAIN("Soft Rain", SoundCategory.NATURE, isPremium = true),
    OCEAN_WAVES("Ocean Waves", SoundCategory.NATURE, isPremium = true),
    WIND("Wind", SoundCategory.NATURE, isPremium = true),
    
    // === Ambient Category === (PREMIUM)
    LO_FI_DRONE("Lo-Fi Drone", SoundCategory.AMBIENT, isPremium = true),
    DEEP_HUM("Deep Hum", SoundCategory.AMBIENT, isPremium = true),
    
    // === Off ===
    OFF("Off", SoundCategory.OFF, isPremium = false);
    
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
