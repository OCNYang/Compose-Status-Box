package com.ocnyang.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main application composable - shared across Android, Desktop and Web
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        // Home Screen
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToStatusBox = {
                    navController.navigate(StatusBoxDemoRoute)
                },
                onNavigateToPagingAppend = {
                    navController.navigate(PagingAppendDemoRoute)
                },
                onNavigateToPagingPrepend = {
                    navController.navigate(PagingPrependDemoRoute)
                }
            )
        }

        // StatusBox Demo
        composable<StatusBoxDemoRoute> {
            StatusBoxDemoScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Paging Append Demo
        composable<PagingAppendDemoRoute> {
            PagingAppendDemoScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Paging Prepend Demo
        composable<PagingPrependDemoRoute> {
            PagingPrependDemoScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

/**
 * Custom UI state for demo
 */
sealed class DemoUIState : com.ocnyang.status_box.UIState {
    data class Success(val message: String) : DemoUIState()
}
