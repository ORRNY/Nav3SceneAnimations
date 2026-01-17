package com.example.nav3sceneanimations.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp

/**
 * Movie detail screen.
 *
 * @param movieId Movie ID
 * @param onBackClick Callback for back navigation
 * @param onPersonClick Callback for person click (actor/director)
 * @param onMovieClick Callback for similar movie click
 * @param onTvShowClick Callback for similar TV show click
 * @param showBackButton Whether to show back button (false on tablet/two-pane layout)
 * @param modifier Modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: String,
    onBackClick: () -> Unit,
    onPersonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onMovieClick: ((String) -> Unit)? = null,
    onTvShowClick: ((String) -> Unit)? = null,
    showBackButton: Boolean = true
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Movie Detail") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
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
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Movie Detail",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Movie ID: $movieId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "You are on the movie detail screen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Cast - navigation to P3 (PersonDetail)
            Text(
                text = "Cast",
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                TextButton(onClick = { onPersonClick("actor-001") }) {
                    Text("Actor detail")
                }
                TextButton(onClick = { onPersonClick("director-002") }) {
                    Text("Director detail")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Similar movies/TV shows - navigation in P2
            Text(
                text = "Similar titles",
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                if (onMovieClick != null) {
                    TextButton(onClick = { onMovieClick("similar-movie-001") }) {
                        Text("Similar movie 1")
                    }
                    TextButton(onClick = { onMovieClick("similar-movie-002") }) {
                        Text("Similar movie 2")
                    }
                }
            }
            Row {
                if (onTvShowClick != null) {
                    TextButton(onClick = { onTvShowClick("similar-tv-001") }) {
                        Text("Similar TV show")
                    }
                }
            }
        }
    }
}
