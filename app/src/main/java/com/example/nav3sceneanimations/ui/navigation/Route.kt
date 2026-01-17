package com.example.nav3sceneanimations.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Sealed interface representing all navigation destinations in the sample app.
 * Implements NavKey for Navigation 3 compatibility.
 */
@Serializable
sealed interface Route : NavKey {

    // ============================================
    // Top-level destinations (BottomBar / NavigationRail)
    // ============================================

    /** Home screen with recommended content */
    @Serializable
    data object Home : Route

    /** Search screen */
    @Serializable
    data object Search : Route

    /** My List - saved content */
    @Serializable
    data object MyList : Route

    /** Downloads screen */
    @Serializable
    data object Downloads : Route

    /** User profile */
    @Serializable
    data object Profile : Route

    // ============================================
    // Detail destinations
    // ============================================

    /**
     * Movie detail
     * @param movieId Movie ID
     * @param instanceId Unique identifier to ensure animation on repeated navigation
     */
    @Serializable
    data class MovieDetail(
        val movieId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route

    /**
     * TV show detail
     * @param tvShowId TV show ID
     * @param instanceId Unique identifier to ensure animation on repeated navigation
     */
    @Serializable
    data class TvShowDetail(
        val tvShowId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route

    /**
     * Person detail (actor/director)
     * @param personId Person ID
     * @param instanceId Unique identifier to ensure animation on repeated navigation
     */
    @Serializable
    data class PersonDetail(
        val personId: String,
        val instanceId: String = UUID.randomUUID().toString()
    ) : Route
}
