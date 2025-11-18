package com.example.rotflix.ui.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rotflix.data.model.MediaItem

/**
 * Results screen that displays a scrollable list of media items matching the user's filters.
 * Shows a message if no results are found.
 *
 * Each result is displayed as a card showing the title, description, rating, and provider.
 *
 * @param items List of media items to display (from ResultsViewModel)
 * @param onOpen Callback when user taps a result card. Receives the media item's ID
 */
@Composable
fun ResultsScreen(
    items: List<MediaItem>,
    onOpen: (String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
        error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
        }
        items.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results. Try adjusting filters or search terms.")
            }
        }
        else -> {
            ResultsList(items, onOpen)
        }
    }
}

@Composable
private fun ResultsList(items: List<MediaItem>, onOpen: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ResultCard(item = item, onClick = { onOpen(item.id) })
        }
    }
}

/**
 * Card component displaying a single media item in the results list.
 * Shows a placeholder poster, title, description snippet, rating, and provider.
 *
 * @param item The media item to display
 * @param onClick Callback when the card is tapped
 */
@Composable
fun ResultCard(item: MediaItem, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Poster (if you have URLs, load with Coil)
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("IMDb ${item.imdbRating ?: "-"}", style = MaterialTheme.typography.labelLarge)
                // Show provider logo or text
                if (item.providers.isNotEmpty()) {
                    val provider = item.providers.first()
                    if (provider.logoUrl != null) {
                        AsyncImage(
                            model = provider.logoUrl,
                            contentDescription = provider.name,
                            modifier = Modifier.size(30.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(provider.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                    }
                } else {
                    Text("-", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
