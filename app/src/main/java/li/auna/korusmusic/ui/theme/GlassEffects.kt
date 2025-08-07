package li.auna.korusmusic.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun glassCardColors() = CardDefaults.cardColors(
    containerColor = GlassSurface,
    contentColor = TextPrimary
)

@Composable
fun glassCardElevation(): CardElevation = CardDefaults.cardElevation(
    defaultElevation = 0.dp,
    pressedElevation = 2.dp,
    focusedElevation = 2.dp,
    hoveredElevation = 4.dp
)

fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(12.dp),
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(GlassSurface)
    .border(
        width = borderWidth,
        color = GlassBorder,
        shape = shape
    )

fun Modifier.glassSurfaceVariant(
    shape: Shape = RoundedCornerShape(8.dp),
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(GlassSurfaceVariant)
    .border(
        width = borderWidth,
        color = GlassBorderLight,
        shape = shape
    )

fun Modifier.glassTopAppBar(): Modifier = this
    .background(GlassSurface)
    .border(
        width = 1.dp,
        color = GlassBorder
    )

fun Modifier.glassBottomBar(): Modifier = this
    .background(GlassSurface)
    .border(
        width = 1.dp,
        color = GlassBorder
    )

fun Modifier.subtleGradientOverlay(): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Zinc950.copy(alpha = 0.8f)
            )
        )
    )