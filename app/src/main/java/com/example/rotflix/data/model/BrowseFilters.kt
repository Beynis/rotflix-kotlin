package com.example.rotflix.data.model

/**
 * Data class representing the user's filter criteria when browsing media content.
 * All filters are optional and can be combined to narrow down search results.
 *
 * @property query Text search query to match against media titles (case-insensitive)
 * @property genres Set of genre names to filter by (e.g., "Drama", "Sci-Fi", "Action")
 * @property minRating Minimum IMDb rating threshold (0.0 to 10.0). Null means no rating filter
 * @property releaseYear Specific year to filter by. Null means any year
 * @property providers Set of streaming provider names (e.g., "Netflix", "Prime Video")
 * @property region ISO 3166-1 country code to filter by release availability (e.g., "US", "GB", "FR"). Null means no region filter
 */
data class BrowseFilters(
    val query: String = "",
    val genres: Set<String> = emptySet(),
    val minRating: Double? = null,
    val releaseYear: Int? = null,
    val providers: Set<String> = emptySet(),
    val region: String? = null
)