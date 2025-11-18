package com.example.rotflix.data.remote

import android.util.Log
import com.example.rotflix.BuildConfig
import com.example.rotflix.data.model.BrowseFilters
import com.example.rotflix.data.model.CastMember
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.MediaRepository
import com.example.rotflix.data.model.MediaType
import com.example.rotflix.data.model.Provider

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

    override suspend fun getById(type: MediaType, id: String, region: String?): MediaItem? {
        return try {
            Log.d("TmdbMediaRepository", "Fetching details for $type with ID: $id")
            when (type) {
                MediaType.MOVIE -> {
                    // Fetch movie details, credits, AND watch providers
                    val movie = api.getMovieDetails(id.toInt(), apiKey)
                    val credits = api.getMovieCredits(id.toInt(), apiKey)
                    val providers = try {
                        api.getMovieWatchProviders(id.toInt(), apiKey)
                    } catch (e: Exception) {
                        Log.w("TmdbMediaRepository", "Failed to fetch watch providers", e)
                        null
                    }

                    // Combine all into a MediaItem
                    movie.toMediaItem(credits, providers, region)
                }
                MediaType.TV -> {
                    // Fetch TV details, credits, AND watch providers
                    val tv = api.getTvDetails(id.toInt(), apiKey)
                    val credits = api.getTvCredits(id.toInt(), apiKey)
                    val providers = try {
                        api.getTvWatchProviders(id.toInt(), apiKey)
                    } catch (e: Exception) {
                        Log.w("TmdbMediaRepository", "Failed to fetch watch providers", e)
                        null
                    }

                    // Combine all into a MediaItem
                    tv.toMediaItem(credits, providers, region)
                }
            }
        } catch (e: Exception) {
            Log.e("TmdbMediaRepository", "Error fetching details for $type ID $id", e)
            null // Return null if item not found or error occurs
        }
    }


    // Helper function to extract providers with logos from watch providers response
    private fun extractProviders(providersResponse: TmdbWatchProvidersResponse?, region: String?): List<Provider> {
        if (providersResponse == null) return emptyList()

        // Use selected region, fall back to US, then any available country
        val selectedRegion = region ?: "US"
        val countryData = providersResponse.results[selectedRegion]
            ?: if (region != null) null else providersResponse.results["US"]
            ?: providersResponse.results.values.firstOrNull()

        // If no providers for the selected region, return special "not available" provider
        if (region != null && countryData == null) {
            val countryName = getCountryName(region)
            return listOf(Provider(name = "Not available in $countryName", logoUrl = null))
        }

        // Prioritize streaming (flatrate), then buy, then rent
        val tmdbProviders = countryData?.flatrate
            ?: countryData?.buy
            ?: countryData?.rent

        // Convert to Provider objects with logo URLs
        return tmdbProviders?.take(3)?.map { tmdbProvider ->
            Provider(
                name = tmdbProvider.provider_name,
                logoUrl = tmdbProvider.logo_path?.let { imageBaseUrl + it }
            )
        } ?: emptyList()
    }

    // Helper function to convert country code to country name
    private fun getCountryName(code: String): String {
        return when (code) {
            "AU" -> "Australia"
            "BR" -> "Brazil"
            "CA" -> "Canada"
            "CN" -> "China"
            "DK" -> "Denmark"
            "FR" -> "France"
            "DE" -> "Germany"
            "IN" -> "India"
            "IT" -> "Italy"
            "JP" -> "Japan"
            "MX" -> "Mexico"
            "NL" -> "Netherlands"
            "NO" -> "Norway"
            "PL" -> "Poland"
            "RU" -> "Russia"
            "KR" -> "South Korea"
            "ES" -> "Spain"
            "SE" -> "Sweden"
            "CH" -> "Switzerland"
            "GB" -> "United Kingdom"
            "US" -> "United States"
            else -> code
        }
    }

    // Extension function to convert TMDB response to MediaItem
    private fun TmdbMovieResponse.toMediaItem(
        credits: TmdbCreditsResponse? = null,
        watchProviders: TmdbWatchProvidersResponse? = null,
        region: String? = null
    ) = MediaItem(
        id = id.toString(),
        type = MediaType.MOVIE,
        title = title,
        description = overview ?: "No description available",
        posterUrl = poster_path?.let { imageBaseUrl + it },
        imdbRating = vote_average,
        providers = extractProviders(watchProviders, region),
        releaseYear = release_date?.take(4)?.toIntOrNull() ?: 0,
        genres = emptyList(), // Convert genre_ids to names
        cast = credits?.cast?.take(5)?.map {
            CastMember(
                name = it.name,
                profileUrl = it.profile_path?.let { path -> imageBaseUrl + path }
            )
        } ?: emptyList() // Top 5 cast members
    )

    private fun TmdbTvResponse.toMediaItem(
        credits: TmdbCreditsResponse? = null,
        watchProviders: TmdbWatchProvidersResponse? = null,
        region: String? = null
    ) = MediaItem(
        id = id.toString(),
        type = MediaType.TV,
        title = name,
        description = overview ?: "No description available",
        posterUrl = poster_path?.let { imageBaseUrl + it },
        imdbRating = vote_average,
        providers = extractProviders(watchProviders, region),
        releaseYear = first_air_date?.take(4)?.toIntOrNull() ?: 0,
        genres = emptyList(),
        cast = credits?.cast?.take(5)?.map {
            CastMember(
                name = it.name,
                profileUrl = it.profile_path?.let { path -> imageBaseUrl + path }
            )
        } ?: emptyList()

    )



}