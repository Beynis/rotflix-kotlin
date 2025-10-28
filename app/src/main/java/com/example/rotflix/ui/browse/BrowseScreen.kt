package com.example.rotflix.ui.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import com.example.rotflix.data.model.BrowseFilters
import com.example.rotflix.data.model.MediaType

/**
 * Browse screen where users can set filter criteria for searching media content.
 * Displays various filtering options including:
 * - Text search by title
 * - Genre selection (multiple)
 * - Minimum rating filter
 * - Release year filter
 * - Streaming provider selection (multiple)
 *
 * This is a stateless composable - all state is managed by BrowseViewModel and passed in.
 *
 * @param type The media type being browsed (MOVIE or TV) - used for screen title
 * @param state Current filter state from the ViewModel
 * @param onQuery Callback when user types in the search field
 * @param onToggleGenre Callback when user taps a genre chip (adds/removes from filter)
 * @param onSetRating Callback when user sets a rating filter. Null clears the filter
 * @param onSetYear Callback when user sets a year filter. Null clears the filter
 * @param onToggleProvider Callback when user taps a provider chip (adds/removes from filter)
 * @param onGoResults Callback when user taps "Show Results" button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    type: MediaType,
    state: BrowseFilters,
    onQuery: (String) -> Unit,
    onToggleGenre: (String) -> Unit,
    onSetRating: (Double?) -> Unit,
    onSetYear: (Int?) -> Unit,
    onToggleProvider: (String) -> Unit,
    onGoResults: () -> Unit
) {
    val allGenres = listOf("Drama","Sci-Fi","Action","Comedy","Mystery","Biography")
    val providers = listOf("Netflix","Prime Video","Disney+","Apple TV+")

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("${if (type==MediaType.MOVIE) "Movies" else "TV Shows"}", style = MaterialTheme.typography.titleLarge)

        // Search
        TextField(
            value = state.query, onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search by title…") }
        )

        // Filters: Genres / Rating / Year (as chips; you can turn these into BottomSheets later)
        Spacer(Modifier.height(16.dp))
        Text("Filter by", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            allGenres.forEach { g ->
                FilterChip(
                    selected = g in state.genres,
                    onClick = { onToggleGenre(g) },
                    label = { Text(g) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onSetRating(7.5) }, label = { Text("Rating ≥ 7.5") })
            AssistChip(onClick = { onSetRating(null) }, label = { Text("Any Rating") })
            AssistChip(onClick = { onSetYear(2017) }, label = { Text("Year: 2017") })
            AssistChip(onClick = { onSetYear(null) }, label = { Text("Any Year") })
        }

        Spacer(Modifier.height(16.dp))
        Text("Streaming providers", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            providers.forEach { p ->
                FilterChip(
                    selected = p in state.providers,
                    onClick = { onToggleProvider(p) },
                    label = { Text(p) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onGoResults, modifier = Modifier.fillMaxWidth()) {
            Text("Show Results")
        }
    }
}
