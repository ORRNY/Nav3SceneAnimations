package com.example.nav3sceneanimations.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nav3sceneanimations.ui.navigation.Route
import com.example.nav3sceneanimations.ui.navigation.TOP_LEVEL_DESTINATIONS

/**
 * Person detail screen (actor/director/crew).
 *
 * @param personId Person ID
 * @param onBackClick Callback for back navigation
 * @param onMovieClick Callback for movie click
 * @param onTvShowClick Callback for TV show click
 * @param currentTopLevelRoute Current top-level route (to highlight current destination)
 * @param onNavigateToTopLevel Callback for navigating to top-level destinations
 * @param onCloseClick Callback for closing detail and returning to root of current tab
 * @param showBackButton Whether to show back button (false on tablet/two-pane layout)
 * @param modifier Modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonDetailScreen(
    personId: String,
    onBackClick: () -> Unit,
    onMovieClick: (String) -> Unit,
    onTvShowClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentTopLevelRoute: Route? = null,
    onNavigateToTopLevel: ((Route) -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null,
    showBackButton: Boolean = true
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Person Detail") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (onCloseClick != null) {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close and return to list"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Person Detail",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Person ID: $personId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "You are on the person detail screen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Placeholder buttons for testing navigation to filmography
            Row {
                TextButton(onClick = { onMovieClick("person-movie-777") }) {
                    Text("Movie from filmography")
                }
                TextButton(onClick = { onTvShowClick("person-tvshow-888") }) {
                    Text("TV show from filmography")
                }
            }

            // Top-level navigation buttons
            if (onNavigateToTopLevel != null || onCloseClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quick Navigation",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TOP_LEVEL_DESTINATIONS.forEach { (route, destination) ->
                        val isCurrentRoute = route == currentTopLevelRoute
                        FilledTonalButton(
                            onClick = {
                                if (isCurrentRoute) {
                                    // Same destination - close details and return to root
                                    onCloseClick?.invoke()
                                } else {
                                    // Different destination - navigate to it
                                    onNavigateToTopLevel?.invoke(route)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isCurrentRoute) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(text = stringResource(destination.labelRes))
                        }
                    }
                }
            }
        }
    }
}
