// MainActivity.kt
package com.example.rotflix

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // Import for viewModels delegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rotflix.network.TmdbConfiguration
import com.example.rotflix.ui.ApiResult // Import your ApiResult
import com.example.rotflix.ui.MainViewModel // Import your ViewModel
import com.example.rotflix.ui.theme.RotflixTheme

class MainActivity : ComponentActivity() {
    // Instantiate the ViewModel using the viewModels delegate
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RotflixTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Collect the state from the ViewModel
                    val configResult by viewModel.configurationState.collectAsState()

                    ConfigurationScreen(
                        configResult = configResult,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigurationScreen(configResult: ApiResult<TmdbConfiguration>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use theme background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (configResult) {
                is ApiResult.Loading -> {
                    CircularProgressIndicator()
                    Text("Loading configuration...")
                }
                is ApiResult.Success -> {
                    val config = configResult.data
                    Text("TMDB Configuration Loaded!", style = MaterialTheme.typography.headlineSmall)
                    Text("Base URL: ${config.images.baseUrl}")
                    Text("Secure Base URL: ${config.images.secureBaseUrl}")
                    Text("Backdrop Sizes: ${config.images.backdropSizes.joinToString()}")
                    // Add more details as needed
                    Log.d("ConfigurationScreen", "Data: $config") // Log the full object
                }
                is ApiResult.Error -> {
                    Text("Error loading configuration: ${configResult.message}", color = Color.Red)
                    Log.e("ConfigurationScreen", "Error: ${configResult.message}", configResult.cause)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigurationScreenPreview_Loading() {
    RotflixTheme {
        ConfigurationScreen(ApiResult.Loading)
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigurationScreenPreview_Success() {
    RotflixTheme {
        val sampleConfig = TmdbConfiguration(
            images = com.example.rotflix.network.ImageConfiguration(
                baseUrl = "http://image.tmdb.org/t/p/",
                secureBaseUrl = "https://image.tmdb.org/t/p/",
                backdropSizes = listOf("w300", "w780"),
                logoSizes = emptyList(),
                posterSizes = emptyList(),
                profileSizes = emptyList(),
                stillSizes = emptyList()
            ),
            changeKeys = listOf("key1", "key2")
        )
        ConfigurationScreen(ApiResult.Success(sampleConfig))
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigurationScreenPreview_Error() {
    RotflixTheme {
        ConfigurationScreen(ApiResult.Error("Failed to load data"))
    }
}
