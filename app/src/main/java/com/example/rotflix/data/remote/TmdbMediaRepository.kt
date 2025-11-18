package com.example.rotflix.data.remote

import com.example.rotflix.BuildConfig
import com.example.rotflix.data.model.BrowseFilters
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.MediaRepository
import com.example.rotflix.data.model.MediaType

class TmdbMediaRepository : MediaRepository {
    private val api = RetrofitInstance.api
    private val apiKey = BuildConfig.TMDB_API_KEY
    private val imageBaseUrl = "https://image.tmdb.org/t/p/w500"

    // Genre mapping as a companion object (shared across all instances)
    companion object {
        // TMDB Genre IDs - these are the official IDs from TMDB API
        private val GENRE_MAP = mapOf(
            // Movie & TV shared genres
            "Action" to 28,
            "Comedy" to 35,
            "Drama" to 18,
            "Sci-Fi" to 878,
            "Mystery" to 9648,
            "Biography" to 36,

            // Additional common genres
            "Adventure" to 12,
            "Animation" to 16,
            "Crime" to 80,
            "Documentary" to 99,
            "Family" to 10751,
            "Fantasy" to 14,
            "History" to 36,
            "Horror" to 27,
            "Music" to 10402,
            "Romance" to 10749,
            "Thriller" to 53,
            "War" to 10752,
            "Western" to 37
        )
    }
    // Helper function to convert genre names to TMDB IDs
    private fun mapGenresToIds(genreNames: Set<String>): List<Int> {
        return genreNames.mapNotNull { genreName ->
            GENRE_MAP[genreName]
        }
    }


    override suspend fun search(type: MediaType, filters: BrowseFilters) : List<MediaItem>{
        // Convert genre names to TMDB genre IDs
        val genreIds = mapGenresToIds(filters.genres)
        val genreString = genreIds.takeIf { it.isNotEmpty() }?.joinToString(",")

        return when (type) {
            MediaType.MOVIE -> {
                // If user entered a text query, use search endpoint
                // Otherwise use discover endpoint with filters
                val response = if (filters.query.isNotBlank()) {
                    api.searchMoviesByQuery(
                        apiKey = apiKey,
                        query = filters.query
                    )
                } else {
                    api.searchMovies(
                        apiKey = apiKey,
                        genres = genreString,
                        minRating = filters.minRating,
                        year = filters.releaseYear,
                        region = filters.region
                    )
                }
                response.results.map { it.toMediaItem() }
            }
            MediaType.TV -> {
                val response = if (filters.query.isNotBlank()) {
                    api.searchTvByQuery(
                        apiKey = apiKey,
                        query = filters.query
                    )
                } else {
                    api.searchTv(
                        apiKey = apiKey,
                        genres = genreString,
                        minRating = filters.minRating,
                        year = filters.releaseYear,
                        region = filters.region
                    )
                }
                response.results.map { it.toMediaItem() }
            }
        }
    }

    override suspend fun getById(type: MediaType, id: String): MediaItem? {
        return try {
            when (type) {
                MediaType.MOVIE -> {
                    // Fetch both movie details AND credits
                    val movie = api.getMovieDetails(id.toInt(), apiKey)
                    val credits = api.getMovieCredits(id.toInt(), apiKey)

                    // Combine both into a MediaItem
                    movie.toMediaItem(credits)
                }
                MediaType.TV -> {
                    // Fetch both TV details AND credits
                    val tv = api.getTvDetails(id.toInt(), apiKey)
                    val credits = api.getTvCredits(id.toInt(), apiKey)

                    // Combine both into a MediaItem
                    tv.toMediaItem(credits)
                }
            }
        } catch (e: Exception) {
            null // Return null if item not found or error occurs
        }
    }


    // Extension function to convert TMDB response to MediaItem
    private fun TmdbMovieResponse.toMediaItem(credits: TmdbCreditsResponse? = null) = MediaItem(
        id = id.toString(),
        type = MediaType.MOVIE,
        title = title,
        description = overview,
        posterUrl = poster_path?.let { imageBaseUrl + it },
        imdbRating = vote_average,
        provider = null, // TMDB doesn't include provider in basic search
        releaseYear = release_date.take(4).toIntOrNull() ?: 0,
        genres = emptyList(), // Convert genre_ids to names
        cast = credits?.cast?.take(5)?.map { it.name } ?: emptyList() // Top 5 cast members
    )

    private fun TmdbTvResponse.toMediaItem(credits: TmdbCreditsResponse? = null) = MediaItem(
        id = id.toString(),
        type = MediaType.TV,
        title = name,
        description = overview,
        posterUrl = poster_path?.let { imageBaseUrl + it },
        imdbRating = vote_average,
        provider = null,
        releaseYear = first_air_date.take(4).toIntOrNull() ?: 0,
        genres = emptyList(),
        cast = credits?.cast?.take(5)?.map { it.name } ?: emptyList()

    )



}