package com.example.rotflix.ui.browse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.rotflix.data.model.BrowseFilters
import com.example.rotflix.data.model.FakeMediaRepository
import com.example.rotflix.data.model.MediaRepository

/**
 * ViewModel for the Browse screen where users select filter criteria.
 * Manages the current state of all user-selected filters.
 *
 * This ViewModel survives configuration changes (like screen rotation) and provides
 * a single source of truth for the filter state that can be shared with the Results screen.
 *
 * @property repo Repository for media data access (defaults to FakeMediaRepository)
 */
class BrowseViewModel(private val repo: MediaRepository = FakeMediaRepository()) : ViewModel() {
    /**
     * Current filter state. Exposed as read-only to the UI.
     * Use the provided update methods to modify filters.
     */
    var filters by mutableStateOf(BrowseFilters())
        private set

    /** Updates the text search query */
    fun updateQuery(q: String) { filters = filters.copy(query = q) }

    /**
     * Toggles a genre in the filter set.
     * If the genre is already selected, it will be removed. If not, it will be added.
     */
    fun toggleGenre(g: String) { filters = filters.copy(
        genres = filters.genres.toMutableSet().apply { if (!add(g)) remove(g) }.toSet()
    ) }

    /** Sets the minimum rating filter. Pass null to clear the rating filter */
    fun setMinRating(r: Double?) { filters = filters.copy(minRating = r) }

    /** Sets the release year filter. Pass null to clear the year filter */
    fun setReleaseYear(y: Int?) { filters = filters.copy(releaseYear = y) }

    /**
     * Toggles a streaming provider in the filter set.
     * If the provider is already selected, it will be removed. If not, it will be added.
     */
    fun toggleProvider(p: String) { filters = filters.copy(
        providers = filters.providers.toMutableSet().apply { if (!add(p)) remove(p) }.toSet()
    ) }
}