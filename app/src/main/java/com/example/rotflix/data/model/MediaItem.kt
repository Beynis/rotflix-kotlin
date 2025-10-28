package com.example.rotflix.data.model

/**
 * Represents a single piece of media content (movie or TV show) in the application.
 * Contains all the information needed to display and filter media items.
 *
 * @property id Unique identifier for this media item
 * @property type The type of media (MOVIE or TV)
 * @property title Display title of the media
 * @property description Plot summary or synopsis
 * @property posterUrl URL to the poster image. Null if no poster is available
 * @property imdbRating IMDb rating score (0.0 to 10.0). Null if not rated
 * @property provider Streaming service where this content is available (e.g., "Netflix")
 * @property releaseYear Year the content was released
 * @property genres List of genre tags (e.g., ["Drama", "Sci-Fi"])
 * @property cast List of main cast member names
 */
data class MediaItem(
    val id: String,
    val type: MediaType,
    val title: String,
    val description: String,
    val posterUrl: String?,
    val imdbRating: Double?,
    val provider: String?,
    val releaseYear: Int,
    val genres: List<String>,
    val cast: List<String>
)

/**
 * Repository interface for accessing media content.
 * Provides methods to search and retrieve media items.
 * This abstraction allows for different implementations (fake data, API, database, etc.)
 */
interface MediaRepository {
    /**
     * Searches for media items matching the given filters.
     *
     * @param type The type of media to search for (MOVIE or TV)
     * @param filters Filter criteria to apply to the search
     * @return List of media items matching all filter criteria
     */
    fun search(type: MediaType, filters: BrowseFilters): List<MediaItem>

    /**
     * Retrieves a specific media item by its ID.
     *
     * @param type The type of media (MOVIE or TV)
     * @param id The unique identifier of the media item
     * @return The media item if found, null otherwise
     */
    fun getById(type: MediaType, id: String): MediaItem?
}

/**
 * Fake implementation of MediaRepository using hardcoded sample data.
 * Used for development and testing before connecting to the TMDB API.
 * Contains a small set of movies and TV shows with sample metadata.
 */
class FakeMediaRepository : MediaRepository {
    private val data = listOf(
        MediaItem("m1", MediaType.MOVIE, "Blade Runner 2049",
            "Young Blade Runner uncovers a secret...", null, 8.0, "Netflix", 2017,
            genres = listOf("Sci-Fi","Drama"), cast = listOf("Ryan Gosling","Harrison Ford","Ana de Armas")),
        MediaItem("m2", MediaType.MOVIE, "The Social Network",
            "How Facebook was built and broke friendships.", null, 7.8, "Prime Video", 2010,
            genres = listOf("Drama","Biography"), cast = listOf("Jesse Eisenberg","Andrew Garfield")),
        MediaItem("t1", MediaType.TV, "Dark",
            "A time-travel mystery in a German town.", null, 8.7, "Netflix", 2017,
            genres = listOf("Sci-Fi","Mystery"), cast = listOf("Louis Hofmann","Lisa Vicari"))
    )

    /**
     * Filters the hardcoded sample data based on the provided criteria.
     * All filters are applied as AND conditions (all must match).
     */
    override fun search(type: MediaType, filters: BrowseFilters): List<MediaItem> =
        data.filter { it.type == type }
            .filter { filters.query.isBlank() || it.title.contains(filters.query, ignoreCase = true) }
            .filter { filters.genres.isEmpty() || it.genres.any(filters.genres::contains) }
            .filter { filters.minRating == null || (it.imdbRating ?: 0.0) >= filters.minRating }
            .filter { filters.releaseYear == null || it.releaseYear == filters.releaseYear }
            .filter { filters.providers.isEmpty() || (it.provider != null && it.provider in filters.providers) }

    /**
     * Searches the hardcoded sample data for a media item with matching type and ID.
     */
    override fun getById(type: MediaType, id: String): MediaItem? =
        data.firstOrNull { it.type == type && it.id == id }
}
