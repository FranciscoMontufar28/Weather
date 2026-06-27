package com.francisco.weather.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.francisco.weather.feature.dashboard.presentation.DashboardScreen
import com.francisco.weather.feature.forecast.presentation.ForecastScreen
import com.francisco.weather.feature.search.presentation.SearchScreen
import com.francisco.weather.feature.splash.presentation.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun WeatherNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = WeatherDestinations.SPLASH,
        modifier = modifier,
    ) {
        composable(WeatherDestinations.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(WeatherDestinations.DASHBOARD) {
                        popUpTo(WeatherDestinations.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(WeatherDestinations.DASHBOARD) {
            DashboardScreen(
                onOpenSearch = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(WeatherDestinations.SEARCH)
                    }
                },
                onOpenForecast = { locationQuery ->
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(WeatherDestinations.forecastRoute(locationQuery))
                    }
                },
            )
        }

        composable(WeatherDestinations.SEARCH) {
            SearchScreen(
                onLocationSelected = { locationQuery ->
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.navigate(WeatherDestinations.forecastRoute(locationQuery))
                    }
                },
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(
            route = WeatherDestinations.FORECAST,
            arguments = listOf(
                navArgument("locationQuery") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("locationQuery") ?: ""
            val locationQuery = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            ForecastScreen(
                locationQuery = locationQuery,
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
            )
        }
    }
}
