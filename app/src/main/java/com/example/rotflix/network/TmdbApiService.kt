package com.example.rotflix.network

import com.example.rotflix.BuildConfig // Import your BuildConfig
import retrofit2.Response // Import Retrofit's Response
import retrofit2.http.GET
import retrofit2.http.Header

interface TmdbApiService {

    @GET("configuration")
    suspend fun getConfiguration(
        @Header("accept") accept: String = "application/json",
        // The Bearer token will be added by an Interceptor
    ): Response<TmdbConfiguration> // Use Retrofit's Response for more control

    // You can add more API calls here later
    // e.g., @GET("movie/popular")
    // suspend fun getPopularMovies(...): Response<PopularMoviesResponse>
}