package com.example.nav3sceneanimations.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

/**
 * Navigator for the sample app.
 *
 * Implements navigation logic according to Material 3 Adaptive guidelines:
 * - Hierarchical navigation (P1 -> P2 -> P3)
 * - Cross-reference navigation (update P2 from P3) - ONLY in three-pane mode
 * - Proper back navigation handling
 *
 * IMPORTANT:
 * - `enableThreePaneLayout` determines if three-pane is ENABLED (from config)
 * - `isThreePaneActive` determines if 3 panes are actually visible (depends on window width)
 * - Navigation from PersonDetail (fromExtraPane=true) always works
 *
 * @param state NavigationState instance
 * @param enableThreePaneLayout Whether three-pane layout is enabled
 */
class Navigator(
    val state: NavigationState,
    private val enableThreePaneLayout: Boolean = false
) {
    /**
     * Current display state - whether 3 panes are actually visible.
     * This must be updated from UI layer according to WindowSizeClass.
     *
     * IMPORTANT: Cross-reference (update P2 from P3) works ONLY when:
     * - enableThreePaneLayout == true (configuration)
     * - isThreePaneActive == true (actual window width)
     */
    var isThreePaneActive: Boolean by mutableStateOf(false)

    /**
     * Currently selected detail in P2 (Movie/TvShow).
     */
    var currentDetailId: String? by mutableStateOf(null)
        private set

    /**
     * Currently selected person.
     * Set whenever we navigate to PersonDetail, regardless of pane.
     */
    var currentPersonId: String? by mutableStateOf(null)
        private set

    /**
     * Detail history for back navigation.
     */
    private val detailHistory = mutableListOf<DetailHistoryRecord>()

    /**
     * Navigates to the specified route.
     *
     * @param route Target route
     * @param fromExtraPane Whether navigation originates from PersonDetail screen
     */
    fun navigate(route: NavKey, fromExtraPane: Boolean = false) {
        when {
            // Top-level navigation - switch tab, reset everything
            route in state.backStacks.keys -> {
                state.topLevelRoute = route
                currentDetailId = null
                currentPersonId = null
                detailHistory.clear()
            }

            // PersonDetail navigation
            route is Route.PersonDetail -> {
                currentPersonId = route.personId
                state.backStacks[state.topLevelRoute]?.add(route)
            }

            // MovieDetail or TvShowDetail navigation
            route is Route.MovieDetail || route is Route.TvShowDetail -> {
                val newDetailId = when (route) {
                    is Route.MovieDetail -> route.movieId
                    is Route.TvShowDetail -> route.tvShowId
                    else -> null
                }

                when {
                    // Cross-reference: Navigation from PersonDetail (P3) to movie/tv
                    // Updates P2, P3 remains (only in three-pane ACTIVE mode)
                    fromExtraPane && enableThreePaneLayout && isThreePaneActive -> {
                        // Save previous detail to history
                        currentDetailId?.let { prevId ->
                            detailHistory.add(DetailHistoryRecord(prevId, true))
                        }
                        currentDetailId = newDetailId

                        // Rebuild stack: top-level + new detail + PersonDetail
                        val currentStack = state.backStacks[state.topLevelRoute]
                        if (currentStack != null) {
                            val personEntry = currentStack.findLast { it is Route.PersonDetail }
                            val topLevelEntry = currentStack.first()

                            currentStack.clear()
                            currentStack.add(topLevelEntry)
                            currentStack.add(route) // route has new instanceId
                            personEntry?.let { currentStack.add(it) }
                        }
                    }

                    // COMPACT/SINGLE-PANE MODE: Linear navigation from PersonDetail
                    // PersonDetail stays in stack, new detail is added at the end
                    fromExtraPane -> {
                        currentDetailId = newDetailId
                        // In linear mode: PersonDetail stays, we add new detail after it
                        // Stack: [Home, MovieA, PersonX, MovieB] - user can go back to PersonX
                        // Each new navigation creates new instanceId automatically
                        state.backStacks[state.topLevelRoute]?.add(route)
                    }

                    // Regular navigation to detail (from P1 or P2, not from PersonDetail)
                    else -> {
                        currentDetailId = newDetailId

                        // Close P3 (PersonDetail) as we're navigating to new main context
                        currentPersonId = null
                        state.backStacks[state.topLevelRoute]?.removeAll { it is Route.PersonDetail }

                        // Add route with new instanceId - ensures animation on repeated navigation
                        state.backStacks[state.topLevelRoute]?.add(route)
                    }
                }
            }

            // Other navigation
            else -> {
                state.backStacks[state.topLevelRoute]?.add(route)
            }
        }
    }

    /**
     * Navigates back.
     *
     * Priority:
     * 1. If PersonDetail is last in stack -> remove it
     * 2. If detail is last -> remove it
     * 3. If we're on different tab -> switch to startRoute
     * 4. Otherwise -> system closes app
     *
     * @return true if navigation was performed
     */
    fun goBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        // Standard back navigation
        return when {
            currentStack.size > 1 -> {
                val removed = currentStack.removeAt(currentStack.lastIndex)

                // Update state based on removed item
                when (removed) {
                    is Route.PersonDetail -> {
                        // Find previous PersonDetail if exists (for nested navigation)
                        val prevPerson = currentStack.findLast { it is Route.PersonDetail }
                        currentPersonId = (prevPerson as? Route.PersonDetail)?.personId
                    }
                    is Route.MovieDetail, is Route.TvShowDetail -> {
                        // Find previous detail (ignore instanceId, compare only content ID)
                        val prevDetail = currentStack.findLast {
                            it is Route.MovieDetail || it is Route.TvShowDetail
                        }
                        currentDetailId = when (prevDetail) {
                            is Route.MovieDetail -> prevDetail.movieId
                            is Route.TvShowDetail -> prevDetail.tvShowId
                            else -> null
                        }

                        // If we're going back to PersonDetail, update currentPersonId
                        val lastEntry = currentStack.lastOrNull()
                        if (lastEntry is Route.PersonDetail) {
                            currentPersonId = lastEntry.personId
                        }
                    }
                    else -> {}
                }
                true
            }
            state.topLevelRoute != state.startRoute -> {
                state.topLevelRoute = state.startRoute
                true
            }
            else -> false
        }
    }

    /**
     * Checks if back navigation is possible.
     */
    fun canGoBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return false
        return currentStack.size > 1 || state.topLevelRoute != state.startRoute
    }

    /**
     * Navigates to the root of current top-level destination.
     * Clears all detail screens (MovieDetail, TvShowDetail, PersonDetail) from the stack.
     *
     * Use this when user wants to "close" detail views and return to the list screen.
     *
     * @return true if navigation was performed (there were screens to remove)
     */
    fun navigateToRoot(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        // Check if there are detail screens to remove
        if (currentStack.size <= 1) {
            return false
        }

        // Keep only the first entry (top-level route)
        val rootEntry = currentStack.first()
        currentStack.clear()
        currentStack.add(rootEntry)

        // Reset detail state
        currentDetailId = null
        currentPersonId = null
        detailHistory.clear()

        return true
    }

    /**
     * Checks if we can navigate to root (there are detail screens in the stack).
     */
    fun canNavigateToRoot(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute] ?: return false
        return currentStack.size > 1
    }

    private data class DetailHistoryRecord(
        val detailId: String,
        val wasFromCrossReference: Boolean
    )
}