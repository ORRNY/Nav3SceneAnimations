package com.example.nav3sceneanimations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nav3sceneanimations.ui.navigation.NavigationRoot

/**
 * Main content of the sample app.
 * Uses Navigation 3 + ListDetailSceneStrategy for adaptive navigation.
 *
 * This sample demonstrates two issues with Navigation 3:
 * 1. Transitions don't work with ListDetailSceneStrategy in adaptive layout
 * 2. No animations when changing content within individual panes
 */
@Composable
fun AppContent() {
    NavigationRoot(
        modifier = Modifier.fillMaxSize()
    )
}