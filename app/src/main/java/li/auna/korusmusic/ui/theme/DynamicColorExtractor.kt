package li.auna.korusmusic.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.palette.graphics.Palette

/**
 * Dynamic color scheme extracted from album art
 */
data class DynamicColorScheme(
    val primaryColor: Color = AccentBlue,
    val backgroundColor: Color = Zinc950,
    val surfaceColor: Color = GlassSurface,
    val surfaceVariantColor: Color = GlassSurfaceVariant,
    val bottomSheetColor: Color = Zinc900,
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
     * Extract dominant color from album art by analyzing actual pixel data
     */
    suspend fun extractColorScheme(albumArt: ImageBitmap?): DynamicColorScheme {
        return withContext(Dispatchers.Default) {
            if (albumArt == null) {
                return@withContext DynamicColorScheme.Default
            }
            
            // Extract actual dominant color from bitmap using Palette library
            val dominantColor = extractDominantColor(albumArt)
            
            generateColorScheme(dominantColor)
        }
    }
    
    /**
     * Generate color scheme from a dominant color
     */
    private fun generateColorScheme(dominantColor: Color): DynamicColorScheme {
        val dominantHsl = dominantColor.toHsl()
        
        // Create a rich, saturated background based on cover
        val backgroundColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 1.2f, 0.85f),
            lightness = 0.12f,
            alpha = 1f
        )
        
        // Use neutral glass surface colors (no extracted color influence)
        val surfaceColor = GlassSurface
        val surfaceVariantColor = GlassSurfaceVariant
        
        // Use static accent color (no extracted color influence)
        val primaryColor = AccentBlue
        
        // Use neutral outline colors (no extracted color influence)
        val outlineColor = GlassBorder
        val outlineVariantColor = GlassBorderLight
        
        // Create bottom sheet as darker version of background color
        val bottomSheetColor = Color.hsl(
            hue = dominantHsl.hue,
            saturation = minOf(dominantHsl.saturation * 1.2f, 0.85f), // Same saturation as background
            lightness = 0.08f, // Darker than background (0.12f) but not black
            alpha = 1f
        )
        
        return DynamicColorScheme(
            primaryColor = primaryColor,
            backgroundColor = backgroundColor,
            surfaceColor = surfaceColor,
            surfaceVariantColor = surfaceVariantColor,
            bottomSheetColor = bottomSheetColor,
            onSurfaceColor = TextPrimary,
            onSurfaceVariantColor = TextSecondary,
            outlineColor = outlineColor,
            outlineVariantColor = outlineVariantColor
        )
    }
    
    /**
     * Extract dominant color from bitmap using Android's Palette library
     */
    private suspend fun extractDominantColor(albumArt: ImageBitmap): Color {
        return withContext(Dispatchers.Default) {
            val originalBitmap = albumArt.asAndroidBitmap()
            
            // Convert hardware bitmap to software bitmap if needed
            val bitmap = if (originalBitmap.config == Bitmap.Config.HARDWARE) {
                originalBitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                originalBitmap
            }
            
            // Generate palette from bitmap
            val palette = Palette.from(bitmap).generate()
            
            // Try to get the best color in order of preference:
            // 1. Vibrant (most saturated)
            // 2. Muted (less saturated but still colorful)
            // 3. Dark vibrant
            // 4. Light vibrant
            // 5. Dominant color
            val dominantColorInt = palette.vibrantSwatch?.rgb
                ?: palette.mutedSwatch?.rgb
                ?: palette.darkVibrantSwatch?.rgb
                ?: palette.lightVibrantSwatch?.rgb
                ?: palette.dominantSwatch?.rgb
                ?: android.graphics.Color.BLUE
            
            Color(dominantColorInt)
        }
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