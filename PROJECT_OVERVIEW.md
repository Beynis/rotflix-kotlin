# Rotflix - Project Overview

A Kotlin/Jetpack Compose application for browsing movies and TV shows, designed to integrate with the TMDB API.

## Project Structure

```
app/src/main/java/com/example/rotflix/
├── MainActivity.kt              # Main entry point, hosts Compose UI
├── RotflixApp.kt               # Root composable with navigation setup
│
├── data/
│   └── model/
│       ├── MediaType.kt        # Enum: MOVIE or TV
│       ├── MediaItem.kt        # Data class for media content
│       ├── BrowseFilters.kt    # Filter criteria data class
│       ├── MediaRepository.kt  # Repository interface (in MediaItem.kt)
│       └── FakeMediaRepository.kt  # Mock data implementation
│
├── navigation/
│   └── Routes.kt               # Navigation route constants
│
└── ui/
    ├── welcome/
    │   └── WelcomeScreen.kt    # Landing screen (Movies/TV choice)
    │
    ├── browse/
    │   ├── BrowseScreen.kt     # Filter selection UI
    │   └── BrowseViewModel.kt  # Manages filter state
    │
    ├── results/
    │   ├── ResultsScreen.kt    # Displays filtered results
    │   └── ResultsViewModel.kt # Manages results list
    │
    └── detail/
        ├── DetailScreen.kt     # Full media item details
        └── DetailViewModel.kt  # Manages single item state
```

## Navigation Flow

```
WelcomeScreen
    ↓ (user picks MOVIE or TV)
BrowseScreen
    ↓ (user sets filters and taps "Show Results")
ResultsScreen
    ↓ (user taps a media item)
DetailScreen
```

## Key Components

### Data Layer

- **MediaType**: Enum distinguishing between movies and TV shows
- **MediaItem**: Contains all information about a piece of content (title, description, rating, cast, etc.)
- **BrowseFilters**: User's selected filter criteria (query, genres, rating, year, providers)
- **MediaRepository**: Interface for data access
- **FakeMediaRepository**: Mock implementation with 3 sample items for testing

### UI Layer

#### WelcomeScreen
- Entry point of the app
- Large buttons for choosing Movies or TV Shows
- Navigates to BrowseScreen with selected type

#### BrowseScreen
- Scrollable form with filter options
- Text search field
- Genre chips (multi-select)
- Rating and year quick filters
- Streaming provider chips (multi-select)
- "Show Results" button to proceed

#### ResultsScreen
- Scrollable list of media items (LazyColumn)
- Each item shows: poster placeholder, title, description, rating, provider
- Tapping an item navigates to DetailScreen
- Shows "No results" message when filter returns empty

#### DetailScreen
- Full information about selected media item
- Poster placeholder, title, description
- Rating and provider
- Cast members displayed as chips
- Shows loading indicator while fetching data

### ViewModels

#### BrowseViewModel
- Holds current filter state (BrowseFilters)
- Provides methods to update each filter type
- Shared between Browse and Results screens for state consistency

#### ResultsViewModel
- Takes MediaType and filters as input
- Queries repository and exposes results list
- Called automatically when filters change (via LaunchedEffect)

#### DetailViewModel
- Takes MediaType and item ID
- Fetches and holds single media item
- Exposes nullable MediaItem (null while loading)

## Architecture Patterns

### State Management
- ViewModels use `mutableStateOf` for reactive UI updates
- State is exposed as read-only to UI (private setters)
- Compose automatically recomposes when state changes

### Shared ViewModel
```kotlin
// BrowseViewModel is shared between Browse and Results screens
val browseVm: BrowseViewModel = viewModel(
    viewModelStoreOwner = nav.getBackStackEntry("browse/$type")
)
```
This pattern ensures Results screen always has access to current filter state.

### Automatic Reload
```kotlin
LaunchedEffect(browseVm.filters) {
    vm.load(browseVm.filters)
}
```
Results automatically reload whenever filters change in Browse screen.

### Navigation Arguments
Routes use string interpolation for passing data:
- `browse/{type}` → `browse/MOVIE` or `browse/TV`
- `detail/{type}/{id}` → `detail/MOVIE/m1`

## Current State

### What Works
✅ All screens compile and render
✅ Navigation between screens
✅ Filter state management
✅ Mock data display
✅ Fully documented codebase

### What's Next (TMDB Integration)
- Replace FakeMediaRepository with real TMDB API calls
- Implement image loading with Coil (posterUrl)
- Map TMDB API responses to MediaItem
- Handle API errors and loading states
- Add pagination for results

## TMDB API Integration Points

### Files to Modify for TMDB
1. **MediaRepository implementation**: Replace FakeMediaRepository with TmdbRepository
2. **MediaItem mapping**: Map TMDB JSON responses to MediaItem data class
3. **Image URLs**: Use Coil to load images from posterUrl
4. **API configuration**: TMDB API key is already set up in build.gradle.kts

### Existing TMDB Setup
- `app/build.gradle.kts`: Reads TMDB_API_KEY from local.properties
- Retrofit dependencies already included
- Network layer skeleton exists in `network/` package

## Building the Project

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Dependencies

- **Jetpack Compose**: Modern declarative UI
- **Material 3**: Material Design components
- **Navigation Compose**: Type-safe navigation
- **Lifecycle ViewModel**: State management
- **Retrofit**: HTTP client (for TMDB API)
- **Moshi**: JSON parsing
- **Coil**: Image loading
- **Material Icons Extended**: Icon resources

## Code Style

All code includes KDoc documentation explaining:
- What each class/function does
- Parameter meanings
- Return value descriptions
- Usage notes and patterns
