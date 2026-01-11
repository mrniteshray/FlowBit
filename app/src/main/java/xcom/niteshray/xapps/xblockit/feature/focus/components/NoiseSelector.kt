package xcom.niteshray.xapps.xblockit.feature.focus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xcom.niteshray.xapps.xblockit.feature.focus.audio.NoiseType
import xcom.niteshray.xapps.xblockit.feature.focus.audio.SoundCategory
import xcom.niteshray.xapps.xblockit.ui.theme.*

/**
 * Bottom sheet content for selecting focus sound type.
 * 
 * Displays all 10 focus sounds organized by category:
 * - Noise (White, Brown, Pink)
 * - Brainwave (Alpha, Beta)
 * - Nature (Rain, Ocean, Wind)
 * - Ambient (Lo-Fi Drone, Deep Hum)
 */
@Composable
fun NoiseSelector(
    selectedNoise: NoiseType,
    isUserPremium: Boolean,
    onNoiseSelected: (NoiseType) -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val groupedSounds = remember { NoiseType.groupedByCategory() }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Text(
            text = "Focus Sounds",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Choose a sound to help you focus",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Grouped sound options
        groupedSounds.forEach { (category, sounds) ->
            // Category header
            Text(
                text = category.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )
            
            // Sound options in this category
            sounds.forEach { noiseType ->
                SoundOption(
                    noiseType = noiseType,
                    isSelected = selectedNoise == noiseType,
                    isLocked = noiseType.isPremium && !isUserPremium,
                    onClick = {
                        onNoiseSelected(noiseType)
                        // Dismiss only if allowed (not handled here, parent decides if we dismiss or show paywall)
                        if (!noiseType.isPremium || isUserPremium) {
                            onDismiss()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Off option at the bottom
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        
        SoundOption(
            noiseType = NoiseType.OFF,
            isSelected = selectedNoise == NoiseType.OFF,
            isLocked = false,
            onClick = {
                onNoiseSelected(NoiseType.OFF)
                onDismiss()
            }
        )
    }
}

/**
 * Individual sound option row
 */
@Composable
private fun SoundOption(
    noiseType: NoiseType,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "bg_color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) 
            MaterialTheme.colorScheme.onPrimary 
        else 
            MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(200),
        label = "content_color"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) GlowWhiteMedium else BorderGlow,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = noiseType.displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                
                // Headphone indicator for binaural beats
                if (noiseType.requiresStereo) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Headphones recommended",
                        tint = if (isSelected) 
                            contentColor.copy(alpha = 0.8f) 
                        else 
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            
            Text(
                text = getSoundDescription(noiseType),
                fontSize = 12.sp,
                color = contentColor.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
             if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Premium",
                    tint = MaterialTheme.colorScheme.tertiary, // Gold-ish usually, or just tertiary
                    modifier = Modifier.size(18.dp).padding(end = 8.dp)
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Get human-readable description for each sound type
 */
private fun getSoundDescription(noiseType: NoiseType): String {
    return when (noiseType) {
        // Noise
        NoiseType.WHITE -> "Balanced, static-like sound"
        NoiseType.BROWN -> "Deep, rumbling like wind"
        NoiseType.PINK -> "Softer, natural balance"
        
        // Brainwave
        NoiseType.BINAURAL_ALPHA -> "10 Hz • Relaxed focus & creativity"
        NoiseType.BINAURAL_BETA -> "15 Hz • Active concentration"
        
        // Nature
        NoiseType.SOFT_RAIN -> "Gentle rain with droplets"
        NoiseType.OCEAN_WAVES -> "Rhythmic wave cycles"
        NoiseType.WIND -> "Subtle gusting breeze"
        
        // Ambient
        NoiseType.LO_FI_DRONE -> "Warm, sustained tones"
        NoiseType.DEEP_HUM -> "Grounding bass frequencies"
        
        // Off
        NoiseType.OFF -> "No background sound"
    }
}
