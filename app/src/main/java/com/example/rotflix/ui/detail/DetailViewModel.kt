package com.example.rotflix.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.rotflix.data.model.FakeMediaRepository
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.MediaRepository
import com.example.rotflix.data.model.MediaType

/**
 * ViewModel for the Detail screen that displays full information about a single media item.
 * Loads and holds the complete media item data for display.
 *
 * @property type The media type (MOVIE or TV) to help with lookup
 * @property repo Repository for media data access (defaults to FakeMediaRepository)
 */
class DetailViewModel(
    private val type: MediaType,
    private val repo: MediaRepository = FakeMediaRepository()
) : ViewModel() {
    /**
     * The currently loaded media item.
     * Will be null while loading or if the item is not found.
     * Exposed as read-only to the UI. Call load() to fetch an item.
     */
    var item by mutableStateOf<MediaItem?>(null)
        private set

    /**
     * Loads a specific media item by its ID.
     * Updates the item property with the found item, or null if not found.
     *
     * @param id The unique identifier of the media item to load
     */
    fun load(id: String) { item = repo.getById(type, id) }
}