package com.example.rotflix.data.remote

data class TmdbMovieResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val vote_average: Double?,
    val release_date: String,
    val genre_ids: List<Int>
)

data class TmdbTvResponse(
    val id: Int,
    val name: String, // TV uses 'name' instead of 'title'
    val overview: String,
    val poster_path: String?,
    val vote_average: Double?,
    val first_air_date: String,
    val genre_ids: List<Int>
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
    val name: String
)