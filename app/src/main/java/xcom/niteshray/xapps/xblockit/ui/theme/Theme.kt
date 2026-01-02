package xcom.niteshray.xapps.xblockit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ═══════════════════════════════════════════════════════════════
// 🌙 DARK MODE - OLED Optimized, Battery Efficient, Eye Comfort
// ═══════════════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    // Primary Colors
    primary = DarkPrimary,
    onPrimary = DarkTextOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    
    // Secondary Colors
    secondary = DarkSecondary,
    onSecondary = DarkTextOnPrimary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkSecondary,
    
    // Tertiary (Success)
    tertiary = Success,
    onTertiary = DarkTextOnPrimary,
    tertiaryContainer = SuccessDark,
    onTertiaryContainer = Success,
    
    // Error
    error = Error,
    onError = DarkTextOnPrimary,
    errorContainer = ErrorDark,
    onErrorContainer = Error,
    
    // Background
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    
    // Surface
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    surfaceTint = DarkPrimary,
    
    // Outlines
    outline = DarkBorder,
    outlineVariant = DarkDivider,
    
    // Inverse
    inverseSurface = LightSurface,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = LightPrimary,
    
    // Scrim
    scrim = Color.Black.copy(alpha = 0.8f)
)

// ═══════════════════════════════════════════════════════════════
// ☀️ LIGHT MODE - Clean, Calm, Professional, Focus-Oriented
// ═══════════════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    // Primary Colors
    primary = LightPrimary,
    onPrimary = LightTextOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightPrimaryVariant,
    
    // Secondary Colors
    secondary = LightSecondary,
    onSecondary = LightTextOnPrimary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightSecondaryVariant,
    
    // Tertiary (Success)
    tertiary = Success,
    onTertiary = LightTextOnPrimary,
    tertiaryContainer = SuccessLight,
    onTertiaryContainer = Success,
    
    // Error
    error = Error,
    onError = LightTextOnPrimary,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    
    // Background
    background = LightBackground,
    onBackground = LightTextPrimary,
    
    // Surface
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    surfaceTint = LightPrimary,
    
    // Outlines
    outline = LightBorder,
    outlineVariant = LightDivider,
    
    // Inverse
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = DarkPrimary,
    
    // Scrim
    scrim = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun BlockitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}