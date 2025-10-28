package com.example.rotflix.navigation

/**
 * Central definition of all navigation routes in the application.
 * These constants are used with Jetpack Compose Navigation to define and navigate between screens.
 *
 * Route patterns with curly braces (e.g., {type}) are navigation arguments that will be
 * replaced with actual values at runtime.
 */
object Routes {
    /** Landing screen where users choose between Movies and TV Shows */
    const val Welcome = "welcome"

    /** Browse screen with filters. {type} will be replaced with MOVIE or TV */
    const val Browse = "browse/{type}"

    /** Results screen showing filtered media items. {type} will be replaced with MOVIE or TV */
    const val Results = "results/{type}"

    /**
     * Detail screen showing full information about a specific media item.
     * {type} will be replaced with MOVIE or TV
     * {id} will be replaced with the media item's unique identifier
     */
    const val Detail = "detail/{type}/{id}"
}