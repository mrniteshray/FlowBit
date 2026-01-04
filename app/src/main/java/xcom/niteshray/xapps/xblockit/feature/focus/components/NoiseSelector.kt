package xcom.niteshray.xapps.xblockit.feature.focus.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import xcom.niteshray.xapps.xblockit.ui.theme.*

/**
 * Bottom sheet content for selecting focus noise type
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoiseSelector(
    selectedNoise: NoiseType,
    onNoiseSelected: (NoiseType) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
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
        
        // Noise options
        NoiseType.entries.forEach { noiseType ->
            NoiseOption(
                noiseType = noiseType,
                isSelected = selectedNoise == noiseType,
                onClick = {
                    onNoiseSelected(noiseType)
                    onDismiss()
                }
            )
            
            if (noiseType != NoiseType.entries.last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun NoiseOption(
    noiseType: NoiseType,
    isSelected: Boolean,
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
    
    val description = when (noiseType) {
        NoiseType.WHITE -> "Balanced, static-like sound"
        NoiseType.BROWN -> "Deep, rumbling sound like wind"
        NoiseType.PINK -> "Softer, balanced frequencies"
        NoiseType.OFF -> "No background sound"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) GlowWhiteMedium else BorderGlow,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = noiseType.displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
