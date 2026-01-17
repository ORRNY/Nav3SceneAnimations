package com.example.nav3sceneanimations.ui.screens.mylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * My List screen with saved content.
 *
 * @param onMovieClick Callback for movie click
 * @param onTvShowClick Callback for TV show click
 * @param modifier Modifier for customization
 */
@Composable
fun MyListScreen(
    onMovieClick: (String) -> Unit,
    onTvShowClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.PlaylistPlay,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "My List",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "You are on the My List screen",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Placeholder buttons for testing navigation
        TextButton(onClick = { onMovieClick("mylist-movie-222") }) {
            Text("Open movie detail")
        }
        TextButton(onClick = { onTvShowClick("mylist-tvshow-333") }) {
            Text("Open TV show detail")
        }
    }
}
