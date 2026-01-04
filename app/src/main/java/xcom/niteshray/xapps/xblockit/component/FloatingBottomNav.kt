package xcom.niteshray.xapps.xblockit.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xcom.niteshray.xapps.xblockit.model.Screen
import xcom.niteshray.xapps.xblockit.ui.theme.*

/**
 * Modern floating bottom navigation bar with glow effects.
 * Optimized for 2 navigation items with smooth transitions.
 */
@Composable
fun FloatingBottomNav(
    screens: List<Screen>,
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating container
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = GlowWhite.copy(alpha = 0.5f),
                    ambientColor = GlowWhite.copy(alpha = 0.3f)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(GlowWhiteMedium, BorderGlow)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            shape = RoundedCornerShape(28.dp),
            color = CardDark
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                screens.forEach { screen ->
                    FloatingNavItem(
                        icon = screen.icon,
                        label = screen.title,
                        isSelected = currentRoute == screen.route,
                        onClick = { onNavigate(screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Fast color animations
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PureWhite else Color.Transparent,
        animationSpec = tween(150),
        label = "bg"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) PureBlack else MediumGray,
        animationSpec = tween(150),
        label = "content"
    )
    
    // Simple glow (only when selected)
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.3f else 0f,
        animationSpec = tween(200),
        label = "glow"
    )
    
    Box(
        modifier = Modifier
            .then(
                if (glowAlpha > 0f) {
                    Modifier.drawBehind {
                        drawCircle(
                            color = PureWhite.copy(alpha = glowAlpha),
                            radius = size.minDimension * 0.6f
                        )
                    }
                } else Modifier
            )
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            
            if (isSelected) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
