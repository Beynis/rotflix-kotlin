package com.example.rotflix.ui // Or your preferred package for ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rotflix.network.RetrofitInstance
import com.example.rotflix.network.TmdbConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException // For Retrofit error handling
import java.io.IOException // For network errors

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String, val cause: Exception? = null) : ApiResult<Nothing>
    object Loading : ApiResult<Nothing>
}

class MainViewModel : ViewModel() {

    private val _configurationState = MutableStateFlow<ApiResult<TmdbConfiguration>>(ApiResult.Loading)
    val configurationState: StateFlow<ApiResult<TmdbConfiguration>> = _configurationState.asStateFlow()

    init {
        fetchTmdbConfiguration()
    }

    fun fetchTmdbConfiguration() {
        viewModelScope.launch {
            _configurationState.value = ApiResult.Loading
            try {
                val response = RetrofitInstance.api.getConfiguration()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _configurationState.value = ApiResult.Success(it)
                    } ?: run {
                        _configurationState.value = ApiResult.Error("Response body is null")
                    }
                } else {
                    _configurationState.value = ApiResult.Error("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: IOException) { // For network errors
                _configurationState.value = ApiResult.Error("Network error: ${e.localizedMessage}", e)
            } catch (e: HttpException) { // For non-2xx responses
                _configurationState.value = ApiResult.Error("HTTP error: ${e.code()} ${e.message()}", e)
            } catch (e: Exception) { // For other errors, like parsing issues
                _configurationState.value = ApiResult.Error("An unexpected error occurred: ${e.localizedMessage}", e)
            }
        }
    }
}
