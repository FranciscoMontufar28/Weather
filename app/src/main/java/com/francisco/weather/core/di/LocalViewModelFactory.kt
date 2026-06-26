package com.francisco.weather.core.di

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider

/**
 * CompositionLocal that provides the Dagger-backed [ViewModelProvider.Factory]
 * to all composables in the tree without explicit prop drilling.
 *
 * Set in MainActivity before the nav host.
 */
val LocalViewModelFactory = compositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided. Wrap your composable tree with CompositionLocalProvider(LocalViewModelFactory provides factory).")
}
