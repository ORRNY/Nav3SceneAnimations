package com.example.nav3sceneanimations.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Home screen with recommended content.
 *
 * @param onMovieClick Callback for movie click
 * @param onTvShowClick Callback for TV show click
 * @param modifier Modifier for customization
 */
@Composable
fun HomeScreen(
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
            imageVector = Icons.Outlined.Home,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "You are on the home screen",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Placeholder buttons for testing navigation
        TextButton(
            onClick = { onMovieClick("movie-123") }
        ) {
            Text("Open movie detail")
        }
        TextButton(
            onClick = { onTvShowClick("tvshow-456") }
        ) {
            Text("Open TV show detail")
        }
    }
}
