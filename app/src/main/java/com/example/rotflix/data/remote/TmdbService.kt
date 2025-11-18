package com.example.rotflix.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    // Use discover endpoints instead of search - they support more filters
    @GET("discover/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String? = null,
        @Query("vote_average.gte") minRating: Double? = null,
        @Query("primary_release_year") year: Int? = null,
        @Query("region") region: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TmdbSearchResponse<TmdbMovieResponse>

    @GET("discover/tv")
    suspend fun searchTv(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String? = null,
        @Query("vote_average.gte") minRating: Double? = null,
        @Query("first_air_date_year") year: Int? = null,
        @Query("region") region: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TmdbSearchResponse<TmdbTvResponse>

    // Add separate search endpoints for text queries
    @GET("search/movie")
    suspend fun searchMoviesByQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbSearchResponse<TmdbMovieResponse>

    @GET("search/tv")
    suspend fun searchTvByQuery(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbSearchResponse<TmdbTvResponse>


    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbMovieResponse

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbTvResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbCreditsResponse

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbCreditsResponse

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbWatchProvidersResponse

    @GET("tv/{tv_id}/watch/providers")
    suspend fun getTvWatchProviders(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String
    ): TmdbWatchProvidersResponse

}