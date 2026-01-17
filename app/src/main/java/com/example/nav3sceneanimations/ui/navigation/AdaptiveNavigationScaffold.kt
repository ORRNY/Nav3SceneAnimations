package com.example.nav3sceneanimations.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarArrangement
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * Adaptive navigation type based on window size.
 *
 * According to Material 3 guidelines:
 * - Compact (< 600dp): ShortNavigationBar with vertical items (icon on top)
 * - Medium (600-840dp): ShortNavigationBar with horizontal items (icon on left)
 * - Expanded (> 840dp): NavigationRail
 *
 * @see <a href="https://m3.material.io/components/navigation-bar/guidelines">Navigation Bar Guidelines</a>
 * @see <a href="https://m3.material.io/foundations/layout/applying-layout/window-size-classes">Window Size Classes</a>
 */
enum class AdaptiveNavigationType {
    /** ShortNavigationBar with vertical items (for compact windows - phones) */
    SHORT_BAR_VERTICAL,

    /** ShortNavigationBar with horizontal items (for medium windows - tablets) */
    SHORT_BAR_HORIZONTAL,

    /** NavigationRail (for large windows - tablets in landscape, desktop) */
    RAIL
}

/**
 * Determines adaptive navigation type based on current window size.
 *
 * @return Navigation type corresponding to current window size
 */
@Composable
fun calculateAdaptiveNavigationType(): AdaptiveNavigationType {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass

    return when {
        // Expanded (> 840dp) -> NavigationRail
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            AdaptiveNavigationType.RAIL
        }
        // Medium (600-840dp) -> ShortNavigationBar with horizontal items
        windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            AdaptiveNavigationType.SHORT_BAR_HORIZONTAL
        }
        // Compact (< 600dp) -> ShortNavigationBar with vertical items
        else -> {
            AdaptiveNavigationType.SHORT_BAR_VERTICAL
        }
    }
}

/**
 * Adaptive navigation scaffold that automatically adapts navigation UI
 * based on window size according to Material 3 guidelines.
 *
 * Behavior:
 * - **Compact (< 600dp)**: `ShortNavigationBar` with vertical items (icon on top, text below)
 * - **Medium (600-840dp)**: `ShortNavigationBar` with horizontal items (icon on left, text on right)
 * - **Expanded (> 840dp)**: `NavigationRail` (on the left)
 *
 * @param selectedRoute Currently selected top-level route
 * @param onNavigate Callback for navigating to a new route
 * @param modifier Modifier for customization
 * @param content Scaffold content
 *
 * @see <a href="https://m3.material.io/components/navigation-bar/guidelines">Navigation Bar Guidelines</a>
 */
@Composable
fun AdaptiveNavigationScaffold(
    selectedRoute: NavKey,
    onNavigate: (Route) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navigationType = calculateAdaptiveNavigationType()

    when (navigationType) {
        AdaptiveNavigationType.RAIL -> {
            // Expanded: NavigationRail on the left
            Row(modifier = modifier.fillMaxSize()) {
                NavigationRailContent(
                    selectedKey = selectedRoute,
                    onSelectKey = { route -> onNavigate(route as Route) }
                )
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
        else -> {
            // Compact/Medium: ShortNavigationBar at the bottom
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    ShortNavigationBarContent(
                        selectedKey = selectedRoute,
                        onSelectKey = { route -> onNavigate(route as Route) },
                        useHorizontalItems = navigationType == AdaptiveNavigationType.SHORT_BAR_HORIZONTAL
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    content()
                }
            }
        }
    }
}

/**
 * ShortNavigationBar with support for vertical/horizontal items.
 *
 * @param selectedKey Currently selected destination
 * @param onSelectKey Callback for destination selection
 * @param modifier Modifier for customization
 * @param useHorizontalItems Whether to use horizontal items (icon on left of text)
 */
@Composable
fun ShortNavigationBarContent(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    useHorizontalItems: Boolean = false
) {
    ShortNavigationBar(
        modifier = modifier,
        // For horizontal items use Centered arrangement for better distribution
        arrangement = if (useHorizontalItems) {
            ShortNavigationBarArrangement.Centered
        } else {
            ShortNavigationBarArrangement.EqualWeight
        }
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (route, navDestination) ->
            val isSelected = route == selectedKey
            ShortNavigationBarItem(
                selected = isSelected,
                onClick = { onSelectKey(route) },
                // For horizontal items: icon on left (Start), otherwise on top (Top - default)
                iconPosition = if (useHorizontalItems) {
                    NavigationItemIconPosition.Start
                } else {
                    NavigationItemIconPosition.Top
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) navDestination.selectedIcon else navDestination.unselectedIcon,
                        contentDescription = stringResource(navDestination.labelRes)
                    )
                },
                label = {
                    Text(text = stringResource(navDestination.labelRes))
                }
            )
        }
    }
}

/**
 * NavigationRail for large screens.
 *
 * @param selectedKey Currently selected destination
 * @param onSelectKey Callback for destination selection
 * @param modifier Modifier for customization
 */
@Composable
fun NavigationRailContent(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        TOP_LEVEL_DESTINATIONS.forEach { (route, navDestination) ->
            val isSelected = route == selectedKey
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectKey(route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) navDestination.selectedIcon else navDestination.unselectedIcon,
                        contentDescription = stringResource(navDestination.labelRes)
                    )
                },
                label = {
                    Text(text = stringResource(navDestination.labelRes))
                }
            )
        }
    }
}
