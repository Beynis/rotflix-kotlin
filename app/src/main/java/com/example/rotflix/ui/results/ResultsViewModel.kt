package com.example.rotflix.ui.results

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotflix.data.model.BrowseFilters
//import com.example.rotflix.data.model.FakeMediaRepository
import com.example.rotflix.data.model.MediaItem
import com.example.rotflix.data.model.MediaRepository
import com.example.rotflix.data.model.MediaType
import com.example.rotflix.data.remote.TmdbMediaRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Results screen that displays filtered media items.
 * Takes the user's selected filters and queries the repository to get matching items.
 *
 * @property type The media type to search for (MOVIE or TV)
 * @property repo Repository for media data access (defaults to FakeMediaRepository)
 */
class ResultsViewModel(
    private val type: MediaType,
    private val repo: MediaRepository = TmdbMediaRepository()
) : ViewModel() {
    /**
     * List of media items matching the current filter criteria.
     * Exposed as read-only to the UI. Call load() to update results.
     */
    var results by mutableStateOf<List<MediaItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set


    /**
     * Loads media items from the repository that match the given filters.
     * Updates the results property with the matching items.
     *
     * @param filters The filter criteria to apply
     */
    fun load(filters: BrowseFilters) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                results = repo.search(type, filters)
                android.util.Log.d("ResultsViewModel", "Loaded ${results.size} results for type=$type")
            } catch (e: Exception) {
                error = e.message
                android.util.Log.e("ResultsViewModel", "Error loading results", e)
            } finally {
                isLoading = false
            }
        }
    }
}