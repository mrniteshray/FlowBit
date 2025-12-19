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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextInverse,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextInverse,
    
    secondary = Secondary,
    onSecondary = TextInverse,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = TextInverse,
    
    tertiary = Success,
    onTertiary = TextInverse,
    tertiaryContainer = SuccessLight,
    onTertiaryContainer = Success,
    
    error = Error,
    onError = TextInverse,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    
    background = Background,
    onBackground = TextPrimary,
    
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = Border,
    outlineVariant = Divider,
    
    inverseSurface = TextPrimary,
    inverseOnSurface = TextInverse,
    inversePrimary = PrimaryLight
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextInverse,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryVariant,
    
    secondary = Secondary,
    onSecondary = TextInverse,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryVariant,
    
    tertiary = Success,
    onTertiary = TextInverse,
    tertiaryContainer = SuccessLight,
    onTertiaryContainer = Success,
    
    error = Error,
    onError = TextInverse,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
    
    background = Background,
    onBackground = TextPrimary,
    
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = Border,
    outlineVariant = Divider,
    
    inverseSurface = TextPrimary,
    inverseOnSurface = TextInverse,
    inversePrimary = PrimaryLight
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