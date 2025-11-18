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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import coil.compose.AsyncImage
import com.example.rotflix.data.model.CastMember
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.Provider

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
                // Provider logos
                if (item.providers.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        item.providers.take(2).forEach { provider ->
                            ProviderLogo(provider)
                        }
                    }
                }
            }
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(item.description)
            }
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        Text("Cast", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(item.cast) { castMember ->
                CastMemberCard(castMember)
            }
        }
    }
}

@Composable
private fun CastMemberCard(castMember: CastMember) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Portrait image
        Box(
            modifier = Modifier
                .size(100.dp, 140.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (castMember.profileUrl != null) {
                AsyncImage(
                    model = castMember.profileUrl,
                    contentDescription = castMember.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: show initials if no photo
                Text(
                    text = castMember.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        // Actor name
        Text(
            text = castMember.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProviderLogo(provider: Provider) {
    if (provider.logoUrl != null) {
        AsyncImage(
            model = provider.logoUrl,
            contentDescription = provider.name,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Fit
        )
    } else {
        // Fallback to text if no logo
        Text(
            text = provider.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
