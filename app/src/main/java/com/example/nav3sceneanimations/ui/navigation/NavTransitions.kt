package com.example.nav3sceneanimations.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween

/**
 * Object containing transition animation definitions for navigation.
 */
object NavTransitions {

    /** Duration of fade animation in ms */
    private const val FADE_DURATION = 300

    /** Duration of slide animation in ms */
    private const val SLIDE_DURATION = 350

    /**
     * Fade animation for top-level navigation (Home, Search, My List, Downloads, Profile).
     */
    val fadeTransitionSpec: ContentTransform
        get() = fadeIn(
            animationSpec = tween(FADE_DURATION)
        ) togetherWith fadeOut(
            animationSpec = tween(FADE_DURATION)
        )

    /**
     * Slide animation for detail screens (iOS-like transition).
     * Slide on X axis + fade animation for incoming screen.
     */
    val slideForwardTransitionSpec: ContentTransform
        get() = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(SLIDE_DURATION)
        ) + fadeIn(
            animationSpec = tween(SLIDE_DURATION)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(SLIDE_DURATION)
        ) + fadeOut(
            animationSpec = tween(SLIDE_DURATION / 2)
        )

    /**
     * Slide animation for going back from detail screen (iOS-like transition).
     */
    val slideBackTransitionSpec: ContentTransform
        get() = slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(SLIDE_DURATION)
        ) + fadeIn(
            animationSpec = tween(SLIDE_DURATION)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(SLIDE_DURATION)
        ) + fadeOut(
            animationSpec = tween(SLIDE_DURATION / 2)
        )

    /**
     * Predictive pop transition for back gesture.
     */
    val predictivePopTransitionSpec: ContentTransform
        get() = slideBackTransitionSpec
}