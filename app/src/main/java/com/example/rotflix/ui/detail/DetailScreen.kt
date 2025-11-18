package com.example.rotflix.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import coil.compose.AsyncImage
import com.example.rotflix.data.model.MediaItem

/**
 * Detail screen that displays full information about a specific media item.
 * Shows a loading indicator while the item is being fetched.
 *
 * Displays:
 * - Poster placeholder
 * - Title and description
 * - Rating and provider
 * - Cast members as chips
 *
 * @param item The media item to display. Null while loading or if not found
 * @param isLoading Loading state indicator
 * @param error Error message if loading failed
 */
@Composable
fun DetailScreen(item: MediaItem?, isLoading: Boolean = false, error: String? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item == null -> {
                Text("Item not found")
            }
            else -> {
                DetailContent(item)
            }
        }
    }
}

@Composable
private fun DetailContent(item: MediaItem) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.width(140.dp)) {
                // Poster image
                Box(
                    Modifier.height(200.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.posterUrl != null) {
                        AsyncImage(
                            model = item.posterUrl,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback to first letter if no poster
                        Text(item.title.take(1), style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("IMDb ${item.imdbRating ?: "-"}", style = MaterialTheme.typography.titleSmall)
                Text(item.provider ?: "-", style = MaterialTheme.typography.labelMedium)
            }
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(item.description)
            }
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))
        Text("Cast", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            item.cast.forEach { name ->
                AssistChip(onClick = {}, label = { Text(name) })
            }
        }
    }
}
