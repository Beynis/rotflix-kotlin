package com.example.rotflix.ui.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.MediaRepository
import com.example.rotflix.data.model.MediaType
import com.example.rotflix.data.remote.TmdbMediaRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Detail screen that displays full information about a single media item.
 * Loads and holds the complete media item data for display.
 *
 * @property type The media type (MOVIE or TV) to help with lookup
 * @property repo Repository for media data access (defaults to TmdbMediaRepository)
 */
class DetailViewModel(
    private val type: MediaType,
    private val repo: MediaRepository = TmdbMediaRepository()
) : ViewModel() {
    /**
     * The currently loaded media item.
     * Will be null while loading or if the item is not found.
     * Exposed as read-only to the UI. Call load() to fetch an item.
     */
    var item by mutableStateOf<MediaItem?>(null)
        private set

    /**
     * Loading state indicator
     */
    var isLoading by mutableStateOf(false)
        private set

    /**
     * Error message if loading failed
     */
    var error by mutableStateOf<String?>(null)
        private set

    /**
     * Loads a specific media item by its ID.
     * Updates the item property with the found item, or null if not found.
     *
     * @param id The unique identifier of the media item to load
     * @param region Optional region code to check provider availability
     */
    fun load(id: String, region: String? = null) {
        viewModelScope.launch {
            isLoading = true
            error = null
            Log.d("DetailViewModel", "Loading $type with ID: $id, region: $region")
            item = repo.getById(type, id, region)
            isLoading = false
            if (item == null) {
                error = "Failed to load details. Please try again."
                Log.e("DetailViewModel", "Failed to load $type with ID: $id")
            } else {
                Log.d("DetailViewModel", "Successfully loaded: ${item?.title}")
            }
        }
    }
}