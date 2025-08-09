package li.auna.korusmusic.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import li.auna.korusmusic.data.util.LrcParser
import li.auna.korusmusic.domain.model.Lyrics
import li.auna.korusmusic.domain.model.LyricsType
import li.auna.korusmusic.domain.model.SynchronizedLyricsData
import li.auna.korusmusic.ui.theme.DynamicColorScheme

@Composable
fun LyricsDisplay(
    lyrics: List<Lyrics>,
    currentPositionMs: Long = 0L,
    colorScheme: DynamicColorScheme,
    preferredLanguage: String = "eng",
    onLanguageSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember(lyrics) { 
        mutableStateOf(
            if (lyrics.any { it.language == preferredLanguage }) preferredLanguage
            else lyrics.firstOrNull()?.language ?: "eng"
        )
    }
    
    val availableLanguages = remember(lyrics) {
        lyrics.map { it.language }.distinct().sorted()
    }
    
    val currentLyrics = remember(lyrics, selectedLanguage) {
        LrcParser.getPreferredLyrics(
            lyrics.filter { it.language == selectedLanguage },
            selectedLanguage,
            preferSynced = true
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Language selector (only show if multiple languages available)
        if (availableLanguages.size > 1) {
            LanguageSelector(
                availableLanguages = availableLanguages,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { language ->
                    selectedLanguage = language
                    onLanguageSelected(language)
                },
                colorScheme = colorScheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Lyrics content
        when {
            currentLyrics == null -> {
                NoLyricsMessage(colorScheme = colorScheme)
            }
            currentLyrics.type == LyricsType.SYNCED -> {
                SynchronizedLyricsDisplay(
                    lyrics = currentLyrics,
                    currentPositionMs = currentPositionMs,
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                UnsynchronizedLyricsDisplay(
                    lyrics = currentLyrics,
                    colorScheme = colorScheme,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    availableLanguages: List<String>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    colorScheme: DynamicColorScheme,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = colorScheme.onSurfaceColor
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(colorScheme.outlineVariantColor)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = getLanguageDisplayName(selectedLanguage),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(
                    colorScheme.surfaceColor,
                    RoundedCornerShape(8.dp)
                )
        ) {
            availableLanguages.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = getLanguageDisplayName(language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (language == selectedLanguage) {
                                colorScheme.primaryColor
                            } else {
                                colorScheme.onSurfaceColor
                            },
                            fontWeight = if (language == selectedLanguage) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    onClick = {
                        onLanguageSelected(language)
                        isExpanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = colorScheme.onSurfaceColor
                    )
                )
            }
        }
    }
}

@Composable
private fun SynchronizedLyricsDisplay(
    lyrics: Lyrics,
    currentPositionMs: Long,
    colorScheme: DynamicColorScheme,
    modifier: Modifier = Modifier
) {
    val synchronizedData = remember(lyrics) {
        LrcParser.parseSynchronizedLyrics(lyrics)
    }
    
    if (synchronizedData == null) {
        ErrorMessage(
            message = "Unable to parse synchronized lyrics",
            colorScheme = colorScheme
        )
        return
    }
    
    var currentLineIndex by remember { mutableStateOf<Int?>(null) }
    
    val listState = rememberLazyListState()
    
    // Update current line index based on position
    LaunchedEffect(currentPositionMs) {
        val newIndex = LrcParser.getCurrentLyricsLine(synchronizedData, currentPositionMs)
        if (newIndex != currentLineIndex) {
            currentLineIndex = newIndex
        }
    }
    
    // Auto-scroll to current line
    LaunchedEffect(currentLineIndex) {
        currentLineIndex?.let { index ->
            if (index >= 0) {
                delay(100) // Small delay to ensure smooth scrolling
                listState.animateScrollToItem(
                    index = maxOf(0, index - 2),
                    scrollOffset = 0
                )
            }
        }
    }
    
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = synchronizedData.lines,
            key = { line -> "${line.time}_${line.text}" }
        ) { line ->
            val lineIndex = synchronizedData.lines.indexOf(line)
            val currentIndex = currentLineIndex
            val isCurrentLine = lineIndex == currentIndex
            val isPastLine = currentIndex?.let { lineIndex < it } ?: false
            
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrentLine -> colorScheme.primaryColor
                        isPastLine -> colorScheme.onSurfaceVariantColor
                        else -> colorScheme.onSurfaceColor
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(
                            when {
                                isCurrentLine -> 1.0f
                                isPastLine -> 0.5f
                                else -> 0.8f
                            }
                        )
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
private fun UnsynchronizedLyricsDisplay(
    lyrics: Lyrics,
    colorScheme: DynamicColorScheme,
    modifier: Modifier = Modifier
) {
    val lyricsLines = remember(lyrics) {
        lyrics.content.lines().filter { it.isNotBlank() }
    }
    
    LazyColumn(
        contentPadding = PaddingValues(vertical = 32.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(lyricsLines) { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NoLyricsMessage(
    colorScheme: DynamicColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lyrics,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colorScheme.onSurfaceVariantColor
        )
        Text(
            text = "No lyrics available",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurfaceColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Lyrics will appear here when available",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariantColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    colorScheme: DynamicColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = colorScheme.onSurfaceVariantColor
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariantColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

private fun getLanguageDisplayName(language: String): String = when (language) {
    "eng" -> "English"
    "ara" -> "Arabic"
    "urd" -> "Urdu"
    "hin" -> "Hindi"
    "spa" -> "Spanish"
    "fre" -> "French"
    "ger" -> "German"
    "jpn" -> "Japanese"
    "kor" -> "Korean"
    "chi" -> "Chinese"
    "por" -> "Portuguese"
    "ita" -> "Italian"
    "rus" -> "Russian"
    else -> language.uppercase()
}