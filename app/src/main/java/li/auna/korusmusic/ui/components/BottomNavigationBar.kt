package li.auna.korusmusic.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import li.auna.korusmusic.navigation.KorusDestination
import li.auna.korusmusic.ui.theme.*

@Composable
fun BottomNavigationBar(
    currentDestination: KorusDestination,
    onNavigateToDestination: (KorusDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigationItems = listOf(
        BottomNavItem(
            destination = KorusDestination.Home,
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            destination = KorusDestination.Search,
            icon = Icons.Default.Search,
            label = "Search"
        ),
        BottomNavItem(
            destination = KorusDestination.Library,
            icon = Icons.Default.LibraryMusic,
            label = "Library"
        ),
        BottomNavItem(
            destination = KorusDestination.Settings,
            icon = Icons.Default.Settings,
            label = "Profile"
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .glassSurface()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navigationItems.forEach { item ->
                BottomNavItemContent(
                    item = item,
                    isSelected = when (currentDestination) {
                        is KorusDestination.Home -> item.destination is KorusDestination.Home
                        is KorusDestination.Search -> item.destination is KorusDestination.Search
                        is KorusDestination.Library -> item.destination is KorusDestination.Library
                        is KorusDestination.Settings -> item.destination is KorusDestination.Settings
                        else -> false
                    },
                    onClick = { onNavigateToDestination(item.destination) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemContent(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = if (isSelected) {
                Modifier
                    .size(40.dp)
                    .glassSurfaceVariant(shape = RoundedCornerShape(10.dp))
            } else {
                Modifier.size(40.dp)
            }
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) AccentBlue else TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TextPrimary else TextTertiary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private data class BottomNavItem(
    val destination: KorusDestination,
    val icon: ImageVector,
    val label: String
)