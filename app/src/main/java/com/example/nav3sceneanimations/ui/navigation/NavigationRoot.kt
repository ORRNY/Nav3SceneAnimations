package com.example.nav3sceneanimations.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nav3sceneanimations.ui.screens.downloads.DownloadsScreen
import com.example.nav3sceneanimations.ui.screens.home.HomeScreen
import com.example.nav3sceneanimations.ui.screens.mylist.MyListScreen
import com.example.nav3sceneanimations.ui.screens.profile.ProfileScreen
import com.example.nav3sceneanimations.ui.screens.search.SearchScreen
import com.example.nav3sceneanimations.ui.screens.detail.MovieDetailScreen
import com.example.nav3sceneanimations.ui.screens.detail.PersonDetailScreen
import com.example.nav3sceneanimations.ui.screens.detail.TvShowDetailScreen

/**
 * Main navigation root for the sample app.
 * Uses official Material 3 Adaptive Navigation 3 ListDetailSceneStrategy.
 *
 * This is a sample project to demonstrate two issues with Navigation 3:
 * 1. Transitions don't work with ListDetailSceneStrategy in adaptive layout
 * 2. No animations when changing content within individual panes
 */
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

    BackHandler(enabled = navigator.canGoBack()) {
        navigator.goBack()
    }

    AdaptiveNavigationScaffold(
        modifier = modifier,
        selectedRoute = navigationState.topLevelRoute,
        onNavigate = { route -> navigator.navigate(route) }
    ) {
        // Use official Material 3 ListDetailSceneStrategy
        // In single-pane mode returns null → NavDisplay uses slide animations
        // In two-pane/three-pane mode returns ListDetailScene
        val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

        val screenModifier = Modifier.fillMaxSize()

        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.goBack() },
            transitionSpec = { NavTransitions.slideForwardTransitionSpec },
            popTransitionSpec = { NavTransitions.slideBackTransitionSpec },
            predictivePopTransitionSpec = { NavTransitions.predictivePopTransitionSpec },
            sceneStrategy = sceneStrategy,
            entries = navigationState.toEntries(
                entryProvider {
                    // ============================================
                    // Top-level destinations (list panes - P1)
                    // In single-pane: slide animations (global)
                    // In two-pane: animations controlled by scene
                    // ============================================

                    entry<Route.Home>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        HomeScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            modifier = screenModifier
                        )
                    }

                    entry<Route.Search>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        SearchScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            onPersonClick = { personId ->
                                navigator.navigate(Route.PersonDetail(personId))
                            },
                            modifier = screenModifier
                        )
                    }

                    entry<Route.MyList>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        MyListScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            modifier = screenModifier
                        )
                    }

                    entry<Route.Downloads>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        DownloadsScreen(
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            modifier = screenModifier
                        )
                    }

                    // ProfileScreen - standalone screen (no listPane metadata)
                    entry<Route.Profile> {
                        ProfileScreen(
                            modifier = screenModifier
                        )
                    }

                    // ============================================
                    // Detail destinations (detail pane - P2)
                    // ============================================

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
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            currentTopLevelRoute = navigationState.topLevelRoute as? Route,
                            onNavigateToTopLevel = { topLevelRoute ->
                                navigator.navigate(topLevelRoute)
                            },
                            onCloseClick = { navigator.navigateToRoot() },
                            modifier = screenModifier,
                            showBackButton = true
                        )
                    }

                    entry<Route.TvShowDetail>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { route ->
                        TvShowDetailScreen(
                            tvShowId = route.tvShowId,
                            onBackClick = { navigator.goBack() },
                            onPersonClick = { personId ->
                                navigator.navigate(Route.PersonDetail(personId))
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(Route.TvShowDetail(tvShowId))
                            },
                            onMovieClick = { movieId ->
                                navigator.navigate(Route.MovieDetail(movieId))
                            },
                            currentTopLevelRoute = navigationState.topLevelRoute as? Route,
                            onNavigateToTopLevel = { topLevelRoute ->
                                navigator.navigate(topLevelRoute)
                            },
                            onCloseClick = { navigator.navigateToRoot() },
                            modifier = screenModifier,
                            showBackButton = true
                        )
                    }

                    // ============================================
                    // Extra destinations (extra pane - P3)
                    // ============================================

                    entry<Route.PersonDetail>(
                        metadata = ListDetailSceneStrategy.extraPane()
                    ) { route ->
                        PersonDetailScreen(
                            personId = route.personId,
                            onBackClick = { navigator.goBack() },
                            onMovieClick = { movieId ->
                                // Navigation to movie from PersonDetail
                                navigator.navigate(
                                    route = Route.MovieDetail(movieId),
                                    fromExtraPane = true
                                )
                            },
                            onTvShowClick = { tvShowId ->
                                navigator.navigate(
                                    route = Route.TvShowDetail(tvShowId),
                                    fromExtraPane = true
                                )
                            },
                            currentTopLevelRoute = navigationState.topLevelRoute as? Route,
                            onNavigateToTopLevel = { topLevelRoute ->
                                navigator.navigate(topLevelRoute)
                            },
                            onCloseClick = { navigator.navigateToRoot() },
                            modifier = screenModifier,
                            showBackButton = true
                        )
                    }
                }
            )
        )
    }
}
