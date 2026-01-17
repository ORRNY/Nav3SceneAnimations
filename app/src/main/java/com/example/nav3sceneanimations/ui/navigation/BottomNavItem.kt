package com.example.nav3sceneanimations.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nav3sceneanimations.R

/**
 * Data class representing a top-level navigation destination.
 * Contains icons for selected/unselected state and string resource for label.
 *
 * @param selectedIcon Icon for selected state (filled)
 * @param unselectedIcon Icon for unselected state (outlined)
 * @param labelRes String resource ID for label
 */
data class NavDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val labelRes: Int
)

/**
 * Map of top-level destinations with their icons and string resource labels.
 * Used for NavigationSuiteScaffold (BottomBar / NavigationRail).
 */
val TOP_LEVEL_DESTINATIONS: Map<Route, NavDestination> = mapOf(
    Route.Home to NavDestination(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        labelRes = R.string.nav_home
    ),
    Route.Search to NavDestination(
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        labelRes = R.string.nav_search
    ),
    Route.MyList to NavDestination(
        selectedIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
        unselectedIcon = Icons.AutoMirrored.Outlined.PlaylistPlay,
        labelRes = R.string.nav_my_list
    ),
    Route.Downloads to NavDestination(
        selectedIcon = Icons.Filled.Download,
        unselectedIcon = Icons.Outlined.Download,
        labelRes = R.string.nav_downloads
    ),
    Route.Profile to NavDestination(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        labelRes = R.string.nav_profile
    )
)
