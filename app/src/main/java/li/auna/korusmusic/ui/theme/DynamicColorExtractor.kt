package li.auna.korusmusic.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * Dynamic color scheme extracted from album art
 */
data class DynamicColorScheme(
    val primaryColor: Color = AccentBlue,
    val backgroundColor: Color = Zinc950,
    val surfaceColor: Color = GlassSurface,
    val surfaceVariantColor: Color = GlassSurfaceVariant,
    val onSurfaceColor: Color = TextPrimary,
    val onSurfaceVariantColor: Color = TextSecondary,
    val outlineColor: Color = GlassBorder,
    val outlineVariantColor: Color = GlassBorderLight
) {
    companion object {
        val Default = DynamicColorScheme()
    }
}

/**
 * Extracts dominant colors from album art and generates a dynamic color scheme
 */
object DynamicColorExtractor {
    
    /**
     * Extract dominant color from album art (placeholder implementation)
     * In a real implementation, this would analyze the actual bitmap
     */
    suspend fun extractColorScheme(albumArt: ImageBitmap?): DynamicColorScheme {
        return withContext(Dispatchers.Default) {
            if (albumArt == null) {
                return@withContext DynamicColorScheme.Default
            }
            
            // Placeholder: Generate color variations based on a simulated dominant color
            // In a real app, you would analyze the bitmap pixels here
            val dominantColor = simulateDominantColor(albumArt)
            
            generateColorScheme(dominantColor)
        }
    }
    
    /**
     * Generate color scheme from a dominant color
     */
    private fun generateColorScheme(dominantColor: Color): DynamicColorScheme {
        val dominantHsl = dominantColor.toHsl()
        
        // Create a darker, more saturated background
        val backgroundColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 0.6f, 0.4f),
            lightness = 0.05f,
            alpha = 1f
        )
        
        // Create glass surface colors with the dominant hue
        val surfaceColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 0.3f, 0.2f),
            lightness = 0.1f,
            alpha = 0.15f
        )
        
        val surfaceVariantColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 0.2f, 0.15f),
            lightness = 0.08f,
            alpha = 0.1f
        )
        
        // Create accent color (more vibrant version of dominant)
        val primaryColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 1.2f, 1f),
            lightness = maxOf(dominantHsl.lightness, 0.6f),
            alpha = 1f
        )
        
        // Create outline colors
        val outlineColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = dominantHsl.saturation * 0.3f,
            lightness = 0.2f,
            alpha = 0.3f
        )
        
        val outlineVariantColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = dominantHsl.saturation * 0.2f,
            lightness = 0.15f,
            alpha = 0.2f
        )
        
        return DynamicColorScheme(
            primaryColor = primaryColor,
            backgroundColor = backgroundColor,
            surfaceColor = surfaceColor,
            surfaceVariantColor = surfaceVariantColor,
            onSurfaceColor = TextPrimary,
            onSurfaceVariantColor = TextSecondary,
            outlineColor = outlineColor,
            outlineVariantColor = outlineVariantColor
        )
    }
    
    /**
     * Simulate dominant color extraction (placeholder)
     * In a real implementation, this would analyze pixel data
     */
    private fun simulateDominantColor(albumArt: ImageBitmap): Color {
        // Placeholder: Return different colors based on a hash of the bitmap
        val hash = albumArt.hashCode()
        val hue = (hash % 360).toFloat()
        
        return Color.hsl(
            hue = hue,
            saturation = 0.7f,
            lightness = 0.5f,
            alpha = 1f
        )
    }
}

/**
 * Extension function to convert Color to HSL
 */
private fun Color.toHsl(): HslColor {
    val argb = this.toArgb()
    val hsl = floatArrayOf(0f, 0f, 0f)
    ColorUtils.colorToHSL(argb, hsl)
    return HslColor(hsl[0], hsl[1], hsl[2])
}

/**
 * Data class for HSL color representation
 */
private data class HslColor(
    val hue: Float,
    val saturation: Float,
    val lightness: Float
)

/**
 * Composable function to extract and remember color scheme from album art
 */
@Composable
fun rememberDynamicColorScheme(albumArt: ImageBitmap?): DynamicColorScheme {
    var colorScheme by remember(albumArt) { mutableStateOf(DynamicColorScheme.Default) }
    
    LaunchedEffect(albumArt) {
        colorScheme = DynamicColorExtractor.extractColorScheme(albumArt)
    }
    
    return colorScheme
}