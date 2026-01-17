# How to add animations when changing content in individual panes of ListDetailSceneStrategy in Jetpack Compose Navigation 3?

## Tags
`android`, `kotlin`, `android-jetpack-compose`, `navigation-compose`, `material3-adaptive`

---

## Question

I'm using Jetpack Compose with Navigation 3 and Material 3 Adaptive library (`androidx.compose.material3.adaptive.navigation3` version 1.3.0-alpha06) to create an adaptive list-detail layout in my Android app.

I have working navigation with `ListDetailSceneStrategy` that correctly displays:
- **Single-pane** mode on small devices
- **Two-pane** mode (list + detail) on medium devices  
- **Three-pane** mode (list + detail + extra OR detail + extra) on large devices

**Problem:** I need to add animations when changing content **within individual panes**, not just when transitioning between single-pane and multi-pane modes.

### Specific scenarios where I want animations:

#### Scenario 1: Change in detail pane (P2) from list pane (P1)
When I have two panes displayed side by side (P1 list + P2 detail) and I select a different movie in the list pane (P1), I want the content in the detail pane (P2) to animate (e.g., slide animation from right).

```
┌─────────────┬─────────────────────────┐
│   LIST (P1) │      DETAIL (P2)        │
│             │                         │
│  > Movie A  │   Movie A detail        │
│    Movie B  │                         │
│    Movie C  │   [animated change      │
│             │    to Movie B]          │
└─────────────┴─────────────────────────┘
```

#### Scenario 2: Change in detail pane (P2) from detail pane (P2)
When I'm in the detail pane (P2) and click on "Similar Movie" or "Similar TV Show", I want the content in the detail pane (P2) to animate.

```
┌─────────────┬─────────────────────────┐
│   LIST (P1) │      DETAIL (P2)        │
│             │                         │
│    Movie A  │   Movie A detail        │
│    Movie B  │                         │
│    Movie C  │   Similar: [Movie X]    │
│             │   → click → animate →   │
│             │   Movie X detail        │
└─────────────┴─────────────────────────┘
```

#### Scenario 3: Change in detail pane (P2) from extra pane (P3)
When I have P2 + P3 panes displayed (detail + extra) and I click on a movie from the filmography in the extra pane (P3) with person detail, I want the detail pane (P2) to animate.

```
┌─────────────────────────┬─────────────────────────┐
│      DETAIL (P2)        │       EXTRA (P3)        │
│                         │                         │
│   Movie A detail        │   Actor X detail        │
│                         │                         │
│   [animated change      │   Filmography:          │
│    to Movie Y]          │   [Movie Y] ← click     │
└─────────────────────────┴─────────────────────────┘
```

### My current code:

**NavigationRoot.kt:**
```kotlin
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(startRoute = Route.Home)
    val navigator = remember { Navigator(state = navigationState) }
    
    // Official ListDetailSceneStrategy
    val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

    AdaptiveNavigationScaffold(
        selectedRoute = navigationState.topLevelRoute,
        onNavigate = { route -> navigator.navigate(route) }
    ) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.goBack() },
            sceneStrategy = sceneStrategy,
            entries = navigationState.toEntries(
                entryProvider {
                    // Top-level destinations (list panes - P1)
                    entry<Route.Home>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        HomeScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            }
                        )
                    }

                    // Detail destinations (detail pane - P2)
                    entry<Route.MovieDetail>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { route ->
                        MovieDetailScreen(
                            movieId = route.movieId,
                            onBackClick = { navigator.goBack() },
                            onPersonClick = { personId ->
                                navigator.navigate(Route.PersonDetail(personId))
                            },
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            }
                        )
                    }

                    // Extra destinations (extra pane - P3)
                    entry<Route.PersonDetail>(
                        metadata = ListDetailSceneStrategy.extraPane()
                    ) { route ->
                        PersonDetailScreen(
                            personId = route.personId,
                            onBackClick = { navigator.goBack() },
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            }
                        )
                    }
                }
            )
        )
    }
}
```

**Route.kt:**
```kotlin
sealed interface Route : NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data class MovieDetail(
        val movieId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route

    @Serializable
    data class PersonDetail(
        val personId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route
}
```

### What I've tried:

#### 1. Custom AnimatedListDetailSceneStrategy

I tried to create a custom `SceneStrategy` that uses `AnimatedContent` for the detail pane:

```kotlin
class AnimatedListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
    val extraEntry: NavEntry<T>? = null,
    val showExtraPane: Boolean = false
) : Scene<T> {

    override val entries: List<NavEntry<T>> = buildList {
        add(listEntry)
        add(detailEntry)
        if (extraEntry != null && showExtraPane) {
            add(extraEntry)
        }
    }

    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            // List Pane (P1)
            Column(modifier = Modifier.weight(0.35f)) {
                listEntry.Content()
            }

            // Detail Pane (P2) - with AnimatedContent for animations
            Column(modifier = Modifier.weight(0.65f)) {
                AnimatedContent(
                    targetState = detailEntry,
                    contentKey = { entry -> entry.contentKey },
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { -it })
                    },
                    label = "DetailPaneAnimation"
                ) { entry ->
                    entry.Content()
                }
            }
        }
    }
}
```

**Problem:** This implementation doesn't work correctly:
- It displays all 3 panes at once instead of properly hiding the list pane in medium mode
- It doesn't support direct navigation from list to extra (P1 → P3)
- It's missing proper `ThreePaneScaffold` logic

#### 2. Using AnimatedContent inside individual screens

I tried adding `AnimatedContent` directly inside `MovieDetailScreen`, but it doesn't work because:
- The screen doesn't know when its content changes
- `contentKey` would have to be controlled from outside

### Questions:

1. **Is there a way to add animations for content changes in individual panes when using official `ListDetailSceneStrategy`?**

2. **If not, what is the recommended approach for implementing custom SceneStrategy with animations that would work properly with `ThreePaneScaffold` logic?**

3. **Is it possible to somehow "wrap" official `ListDetailSceneStrategy` and add animations only for content changes in detail/extra panes?**

### Environment:
- Kotlin: 2.2.21
- Jetpack Compose BOM: 2026.01.00
- Navigation 3: 1.1.0-alpha02
- Material 3 Adaptive: 1.3.0-alpha06
- AGP: 8.13.2

### Relevant dependencies:
```toml
[versions]
navigation3 = "1.1.0-alpha02"
androidx-compose-material3-adaptive = "1.3.0-alpha06"

[libraries]
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "navigation3" }
material3-adaptive-navigation3 = { group = "androidx.compose.material3.adaptive", name = "adaptive-navigation3", version.ref = "androidx-compose-material3-adaptive" }
```

---

## Expected behavior:

When content changes in a pane, there should be a smooth animation (slide, fade, or other) that clearly indicates to the user that the pane content has changed, while other panes remain in place.

## Actual behavior:

Content in panes changes instantly without any animation. Animations work only when transitioning between single-pane and multi-pane modes, not when changing content within a pane.

---

Thanks for any suggestions or solutions!
