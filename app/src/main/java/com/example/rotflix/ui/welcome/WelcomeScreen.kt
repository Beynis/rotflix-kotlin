package com.example.rotflix.ui.welcome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rotflix.data.model.MediaType

/**
 * Welcome/Landing screen that serves as the entry point for the app.
 * Displays two large buttons allowing users to choose between browsing Movies or TV Shows.
 *
 * The screen is split vertically in half:
 * - Top half: Movies button
 * - Bottom half: TV Shows button
 *
 * @param onPick Callback invoked when user selects a media type. Receives the selected MediaType
 */
@Composable
fun WelcomeScreen(onPick: (MediaType) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier.weight(1f).fillMaxWidth().clickable { onPick(MediaType.MOVIE) },
            contentAlignment = Alignment.Center
        ) {
            Text("Movies", style = MaterialTheme.typography.headlineLarge)
        }
        HorizontalDivider()
        Box(
            Modifier.weight(1f).fillMaxWidth().clickable { onPick(MediaType.TV) },
            contentAlignment = Alignment.Center
        ) {
            Text("TV Shows", style = MaterialTheme.typography.headlineLarge)
        }
    }
}
