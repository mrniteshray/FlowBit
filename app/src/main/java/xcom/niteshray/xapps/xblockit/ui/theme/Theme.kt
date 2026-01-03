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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════
// 🌙 DARK MODE - Minimalistic Black & White (Default Theme)
// ═══════════════════════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    // Primary Colors - Pure White
    primary = DarkPrimary,
    onPrimary = DarkTextOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    
    // Secondary Colors - Gray tones
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
    
    // Background - Pure Black
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    
    // Surface - Near Black
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
// ☀️ LIGHT MODE - Inverted (Black on White) - Optional Fallback
// ═══════════════════════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    // Primary Colors - Pure Black
    primary = LightPrimary,
    onPrimary = LightTextOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightPrimaryVariant,
    
    // Secondary Colors
    secondary = LightSecondary,
    onSecondary = LightTextOnPrimary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightSecondaryVariant,
    
    // Tertiary
    tertiary = Success,
    onTertiary = LightTextOnPrimary,
    tertiaryContainer = SuccessLight,
    onTertiaryContainer = Success,
    
    // Error
    error = Error,
    onError = LightTextOnPrimary,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    
    // Background - Pure White
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
    // ALWAYS use dark theme - minimalistic black & white design
    darkTheme: Boolean = true,
    // Dynamic color disabled for consistent black & white design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use dark color scheme for minimalistic black & white look
    val colorScheme = DarkColorScheme
    
    // Set status bar to match our dark theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PureBlack.toArgb()
            window.navigationBarColor = PureBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}