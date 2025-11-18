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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * - Country/region selection
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
 * @param onSetRegion Callback when user selects a country/region. Null clears the region filter
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
    onSetRegion: (String?) -> Unit,
    onGoResults: () -> Unit
) {
    val allGenres = listOf("Drama","Sci-Fi","Action","Comedy","Mystery","Biography")
    val providers = listOf("Netflix","Prime Video","Disney+","Apple TV+")

    // Country list with ISO 3166-1 codes (alphabetically sorted)
    val countries = mapOf(
        "🌍 All Countries" to null,
        "🇦🇺 Australia" to "AU",
        "🇧🇷 Brazil" to "BR",
        "🇨🇦 Canada" to "CA",
        "🇨🇳 China" to "CN",
        "🇩🇰 Denmark" to "DK",
        "🇫🇷 France" to "FR",
        "🇩🇪 Germany" to "DE",
        "🇮🇳 India" to "IN",
        "🇮🇹 Italy" to "IT",
        "🇯🇵 Japan" to "JP",
        "🇲🇽 Mexico" to "MX",
        "🇳🇱 Netherlands" to "NL",
        "🇳🇴 Norway" to "NO",
        "🇵🇱 Poland" to "PL",
        "🇷🇺 Russia" to "RU",
        "🇰🇷 South Korea" to "KR",
        "🇪🇸 Spain" to "ES",
        "🇸🇪 Sweden" to "SE",
        "🇨🇭 Switzerland" to "CH",
        "🇬🇧 United Kingdom" to "GB",
        "🇺🇸 United States" to "US"
    )

    var countryDropdownExpanded by remember { mutableStateOf(false) }
    val selectedCountryName = countries.entries.find { it.value == state.region }?.key ?: "🌍 All Countries"

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("${if (type==MediaType.MOVIE) "Movies" else "TV Shows"}", style = MaterialTheme.typography.titleLarge)

        // Search
        TextField(
            value = state.query, onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search by title…") }
        )

        // Country/Region Selector
        Spacer(Modifier.height(16.dp))
        Text("Country/Region", style = MaterialTheme.typography.titleMedium)
        ExposedDropdownMenuBox(
            expanded = countryDropdownExpanded,
            onExpandedChange = { countryDropdownExpanded = it },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            TextField(
                value = selectedCountryName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryDropdownExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = countryDropdownExpanded,
                onDismissRequest = { countryDropdownExpanded = false }
            ) {
                countries.forEach { (countryName, countryCode) ->
                    DropdownMenuItem(
                        text = { Text(countryName) },
                        onClick = {
                            onSetRegion(countryCode)
                            countryDropdownExpanded = false
                        }
                    )
                }
            }
        }

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
