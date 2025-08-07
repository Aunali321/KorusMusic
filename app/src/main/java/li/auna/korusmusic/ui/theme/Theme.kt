package li.auna.korusmusic.ui.theme

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

private val GlassmorphismColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = Zinc600,
    onSecondary = TextSecondary,
    tertiary = Zinc500,
    onTertiary = TextTertiary,
    background = Zinc950,
    onBackground = TextPrimary,
    surface = GlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceVariant,
    onSurfaceVariant = TextTertiary,
    outline = GlassBorder,
    outlineVariant = GlassBorderLight,
    primaryContainer = GlassSurface,
    onPrimaryContainer = TextPrimary,
    secondaryContainer = GlassSurfaceVariant,
    onSecondaryContainer = TextSecondary,
    error = AccentRed,
    onError = TextPrimary
)

@Composable
fun KorusMusicTheme(
    darkTheme: Boolean = true, // Always use dark theme for glassmorphism
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GlassmorphismColorScheme,
        typography = Typography,
        content = content
    )
}