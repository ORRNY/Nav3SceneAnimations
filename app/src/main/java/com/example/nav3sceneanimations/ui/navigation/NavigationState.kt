package com.example.nav3sceneanimations.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.collections.associateWith
import kotlin.collections.flatMap
import kotlin.collections.mapValues

/**
 * Navigation state for the sample app.
 * Manages multiple back stacks - one for each top-level destination.
 *
 * @param startRoute Default route on app start
 * @param topLevelRoute Currently selected top-level route
 * @param backStacks Map of back stacks for each top-level destination
 */
class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    /** Currently selected top-level destination */
    var topLevelRoute by topLevelRoute

    /**
     * List of stacks currently in use.
     *
     * IMPORTANT: Returns only current top-level stack, not a combination.
     * This ensures that detail from another tab is not displayed.
     */
    val stacksInUse: List<NavKey>
        get() = listOf(topLevelRoute)
}

/**
 * Serialization configuration for saving navigation state
 */
val serializersConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            // Top-level destinations
            subclass(Route.Home::class, Route.Home.serializer())
            subclass(Route.Search::class, Route.Search.serializer())
            subclass(Route.MyList::class, Route.MyList.serializer())
            subclass(Route.Downloads::class, Route.Downloads.serializer())
            subclass(Route.Profile::class, Route.Profile.serializer())
            // Detail destinations
            subclass(Route.MovieDetail::class, Route.MovieDetail.serializer())
            subclass(Route.TvShowDetail::class, Route.TvShowDetail.serializer())
            subclass(Route.PersonDetail::class, Route.PersonDetail.serializer())
        }
    }
}

/**
 * Creates and remembers NavigationState.
 *
 * @param startRoute Default route on app start
 * @param topLevelRoutes Set of all top-level routes
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        configuration = serializersConfig,
        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class))
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = serializersConfig,
            key
        )
    }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

/**
 * Converts NavigationState to a list of decorated NavEntry for NavDisplay.
 *
 * @param entryProvider Provider for creating NavEntry
 */
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
