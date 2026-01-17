# Jetpack Compose Navigation 3 - Transition animations don't work with ListDetailSceneStrategy in adaptive layout

## Problem

I'm developing an Android app in Jetpack Compose with adaptive layout using **Navigation 3** and **ListDetailSceneStrategy**. I have an issue with transition animations between screens.

### Expected behavior:
- **Single-pane mode** (phone < 600dp): Slide animations when transitioning between all screens (Home → Downloads, Home → MovieDetail)
- **Two-pane mode** (tablet ≥ 600dp): Animations when changing top-level destination in list pane (Home → Downloads) + slide animations for detail pane

### Actual behavior:
- **Single-pane mode**: Slide animations work correctly ✅
- **Two-pane mode**: Transition animations between top-level destinations (Home → Downloads) **don't work** ❌ - screen just "jumps" without animation

### Observations:
- Animation works only when navigating to `ProfileScreen`, which doesn't have `listPane()` metadata (it's a standalone screen)
- The issue occurs only when two-pane layout with `ListDetailSceneStrategy` is active

---

## App Structure

### Screen types (metadata):
| Panel | Metadata | Screens |
|-------|----------|---------|
| P1 (List) | `ListDetailSceneStrategy.listPane()` | Home, Search, MyList, Downloads |
| P2 (Detail) | `ListDetailSceneStrategy.detailPane()` | MovieDetail, TvShowDetail |
| P3 (Extra) | `ListDetailSceneStrategy.extraPane()` | PersonDetail |
| - | none | Profile |

### Visual diagram:
```
Two-pane mode (≥ 600dp):
┌─────────────────────┬─────────────────────────────────────────────┐
│                     │                                             │
│       List          │           Movie Detail                      │
│       (P1)          │              (P2)                           │
│                     │                                             │
│   Home/Search/...   │        MovieDetail/TvShowDetail             │
│                     │                                             │
└─────────────────────┴─────────────────────────────────────────────┘

Single-pane mode (< 600dp):
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│                      Current Screen                                 │
│                                                                     │
│            Home → MovieDetail → PersonDetail → ...                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Code

### Route.kt
```kotlin
@Serializable
sealed interface Route : NavKey {
    // Top-level destinations
    @Serializable data object Home : Route
    @Serializable data object Search : Route
    @Serializable data object MyList : Route
    @Serializable data object Downloads : Route
    @Serializable data object Profile : Route

    // Detail destinations
    @Serializable
    data class MovieDetail(
        val movieId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route

    @Serializable
    data class TvShowDetail(
        val tvShowId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route

    @Serializable
    data class PersonDetail(
        val personId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route
}
```

### NavigationRoot.kt
```kotlin
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Home,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )

    val navigator = remember {
        Navigator(state = navigationState)
    }

    AdaptiveNavigationScaffold(
        selectedRoute = navigationState.topLevelRoute,
        onNavigate = { route -> navigator.navigate(route) }
    ) {
        // Official ListDetailSceneStrategy from Material 3 Adaptive
        val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.goBack() },
            // Global slide animations
            transitionSpec = { 
                slideInHorizontally { it } + fadeIn() togetherWith 
                slideOutHorizontally { -it / 4 } + fadeOut() 
            },
            popTransitionSpec = { 
                slideInHorizontally { -it / 4 } + fadeIn() togetherWith 
                slideOutHorizontally { it } + fadeOut() 
            },
            sceneStrategy = sceneStrategy,
            entries = navigationState.toEntries(
                entryProvider {
                    // Top-level destinations (P1 - list pane)
                    entry<Route.Home>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        HomeScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            }
                        )
                    }

                    entry<Route.Search>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        SearchScreen(...)
                    }

                    entry<Route.MyList>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        MyListScreen(...)
                    }

                    entry<Route.Downloads>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        DownloadsScreen(...)
                    }

                    // Profile - without listPane() metadata
                    entry<Route.Profile> {
                        ProfileScreen(...)
                    }

                    // Detail destinations (P2 - detail pane)
                    entry<Route.MovieDetail>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { route ->
                        MovieDetailScreen(movieId = route.movieId, ...)
                    }

                    entry<Route.TvShowDetail>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { route ->
                        TvShowDetailScreen(tvShowId = route.tvShowId, ...)
                    }

                    // Extra destinations (P3 - extra pane)
                    entry<Route.PersonDetail>(
                        metadata = ListDetailSceneStrategy.extraPane()
                    ) { route ->
                        PersonDetailScreen(personId = route.personId, ...)
                    }
                }
            )
        )
    }
}
```

### NavigationState.kt
```kotlin
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute by topLevelRoute

    val stacksInUse: List<NavKey>
        get() = listOf(topLevelRoute)
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
```

### Navigator.kt (relevant part)
```kotlin
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey, fromExtraPane: Boolean = false) {
        when {
            // Top-level navigation - switch tab
            route in state.backStacks.keys -> {
                state.topLevelRoute = route
            }
            // Detail navigation - add to stack
            else -> {
                state.backStacks[state.topLevelRoute]?.add(route)
            }
        }
    }
}
```

---

## What I've tried

### 1. Custom SceneStrategy with AnimatedContent
I tried to create a custom `SceneStrategy` that would contain `AnimatedContent` for list pane animations:

```kotlin
class CustomListDetailScene<T : Any>(
    private val listEntry: NavEntry<T>,
    private val detailEntry: NavEntry<T>,
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>
) : Scene<T> {
    override val entries: List<NavEntry<T>>
        get() = listOf(listEntry, detailEntry)

    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = listEntry,
                contentKey = { it.contentKey },
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { entry ->
                entry.Content()
            }

            AnimatedContent(
                targetState = detailEntry,
                contentKey = { it.contentKey },
                transitionSpec = { slideIn() togetherWith slideOut() }
            ) { entry ->
                entry.Content()
            }
        }
    }
}
```

**Result:** App crashes with error:
```
java.lang.IllegalArgumentException: Key Home was used multiple times
    at androidx.compose.runtime.saveable.SaveableStateHolderImpl...
```

### 2. Animation metadata for individual entries
I tried adding animation metadata directly to entries:

```kotlin
entry<Route.Home>(
    metadata = ListDetailSceneStrategy.listPane() +
        NavDisplay.transitionSpec { fadeIn() togetherWith fadeOut() } +
        NavDisplay.popTransitionSpec { fadeIn() togetherWith fadeOut() }
) {
    HomeScreen(...)
}
```

**Result:** Animations still don't work in two-pane mode because `ListDetailSceneStrategy` takes control over rendering.

---

## Problem Analysis

After examining `ListDetailSceneStrategy` source code, I found:

```kotlin
class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        // Returns null for single-pane (< 600dp)
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        // Returns scene ONLY when last entry is detailPane
        val detailEntry = entries.lastOrNull()?.takeIf { 
            it.metadata.containsKey(DETAIL_KEY) 
        } ?: return null
        
        val listEntry = entries.findLast { 
            it.metadata.containsKey(LIST_KEY) 
        } ?: return null

        // Scene key is based on listEntry.contentKey
        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            key = sceneKey,
            listEntry = listEntry,
            detailEntry = detailEntry,
            ...
        )
    }
}
```

**Key finding:**
- `ListDetailSceneStrategy` returns a scene only when there's a `detailPane` entry on the stack
- When switching between top-level destinations (Home → Downloads) **without an open detail**, the strategy returns `null` and `NavDisplay` should animate
- When a detail is open, the scene has a **stable key** (`sceneKey = listEntry.contentKey`) - this means that when list entry changes, the scene key changes, but animation doesn't happen inside the scene

---

## Questions

1. **How to properly implement animations for top-level destination changes in two-pane mode with `ListDetailSceneStrategy`?**

2. **Is it possible to use `AnimatedContent` inside a custom `Scene` without causing "Key was used multiple times" error?**

3. **Is there a recommended way to combine `ListDetailSceneStrategy` with custom animations for list pane?**

4. **Is this a limitation of current Navigation 3 version, or am I doing something wrong?**

---

## Environment

- **Kotlin**: 2.2.21
- **Compose BOM**: 2026.01.00
- **Navigation 3**: 1.1.0-alpha02
- **Material 3 Adaptive**: 1.3.0-alpha06
- **Android Gradle Plugin**: 8.13.2
- **Min SDK**: 24
- **Target SDK**: 36

---

## Tags

`android` `kotlin` `android-jetpack-compose` `navigation` `material-design-3` `adaptive-layout`
