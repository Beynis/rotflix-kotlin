package com.example.rotflix

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rotflix.data.model.MediaType
import com.example.rotflix.navigation.Routes
import com.example.rotflix.ui.browse.BrowseScreen
import com.example.rotflix.ui.browse.BrowseViewModel
import com.example.rotflix.ui.detail.DetailScreen
import com.example.rotflix.ui.detail.DetailViewModel
import com.example.rotflix.ui.results.ResultsScreen
import com.example.rotflix.ui.results.ResultsViewModel
import com.example.rotflix.ui.welcome.WelcomeScreen

/**
 * Root composable for the Rotflix application.
 * Sets up the navigation graph and defines all navigation routes and their destinations.
 *
 * Navigation Flow:
 * 1. WelcomeScreen - User picks Movies or TV Shows
 * 2. BrowseScreen - User sets filter criteria (genre, rating, year, provider)
 * 3. ResultsScreen - Displays filtered media items
 * 4. DetailScreen - Shows full details of a selected media item
 *
 * Key Architecture Patterns:
 * - Shared ViewModel: BrowseViewModel is shared between Browse and Results screens
 *   using viewModelStoreOwner to maintain filter state across navigation
 * - LaunchedEffect: Automatically reloads results when filters change
 * - Navigation arguments: Routes include type and id parameters in the URL
 */
@Composable
fun RotflixApp() {
    val nav = rememberNavController()
    MaterialTheme {
        NavHost(navController = nav, startDestination = Routes.Welcome) {
            // Route 1: Welcome/Landing Screen
            composable(Routes.Welcome) {
                WelcomeScreen(
                    onPick = { type -> nav.navigate("browse/$type") }
                )
            }
            // Route 2: Browse Screen with Filters
            // URL pattern: browse/MOVIE or browse/TV
            composable("browse/{type}") { backStack ->
                val type = MediaType.valueOf(backStack.arguments?.getString("type")!!)
                val vm: BrowseViewModel = viewModel()
                BrowseScreen(
                    type = type,
                    state = vm.filters,
                    onQuery = vm::updateQuery,
                    onToggleGenre = vm::toggleGenre,
                    onSetRating = vm::setMinRating,
                    onSetYear = vm::setReleaseYear,
                    onToggleProvider = vm::toggleProvider,
                    onSetRegion = vm::setRegion,
                    onGoResults = { nav.navigate("results/$type") }
                )
            }
            // Route 3: Results Screen
            // URL pattern: results/MOVIE or results/TV
            // Shares BrowseViewModel with Browse screen to access filter state
            composable("results/{type}") { backStack ->
                val type = MediaType.valueOf(backStack.arguments?.getString("type")!!)
                val browseVm: BrowseViewModel = viewModel(viewModelStoreOwner = nav.getBackStackEntry("browse/$type"))
                val vm = remember { ResultsViewModel(type) }
                LaunchedEffect(browseVm.filters) { vm.load(browseVm.filters) }
                ResultsScreen(
                    items = vm.results,
                    onOpen = { id -> nav.navigate("detail/$type/$id") },
                    isLoading = vm.isLoading,
                    error = vm.error
                )
            }
            // Route 4: Detail Screen
            // URL pattern: detail/MOVIE/m1 or detail/TV/t1
            composable("detail/{type}/{id}") { backStack ->
                val type = MediaType.valueOf(backStack.arguments?.getString("type")!!)
                val id = backStack.arguments?.getString("id")!!
                val vm = remember { DetailViewModel(type) }
                LaunchedEffect(id) { vm.load(id) }
                DetailScreen(item = vm.item)
            }
        }
    }
}
