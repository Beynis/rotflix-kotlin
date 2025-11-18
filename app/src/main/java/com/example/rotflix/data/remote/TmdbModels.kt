package com.example.rotflix.data.remote

data class TmdbMovieResponse(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?,
    val release_date: String?,
    val genre_ids: List<Int>? = emptyList()
)

data class TmdbTvResponse(
    val id: Int,
    val name: String, // TV uses 'name' instead of 'title'
    val overview: String?,
    val poster_path: String?,
    val vote_average: Double?,
    val first_air_date: String?,
    val genre_ids: List<Int>? = emptyList()
)

data class TmdbSearchResponse<T>(
    val results: List<T>,
    val total_results: Int,
    val page: Int
)

data class TmdbCreditsResponse(
    val cast: List<TmdbCastMember>
)

data class TmdbCastMember(
    val name: String,
    val profile_path: String?
)

data class TmdbWatchProvidersResponse(
    val results: Map<String, TmdbCountryProviders>
)

data class TmdbCountryProviders(
    val link: String?,
    val flatrate: List<TmdbProvider>? = emptyList(),
    val buy: List<TmdbProvider>? = emptyList(),
    val rent: List<TmdbProvider>? = emptyList()
)

data class TmdbProvider(
    val provider_name: String,
    val provider_id: Int,
    val logo_path: String?
)