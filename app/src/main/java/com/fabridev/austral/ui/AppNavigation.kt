package com.fabridev.austral.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fabridev.austral.ui.screens.add.AddTransactionScreen
import com.fabridev.austral.ui.screens.home.HomeScreen
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.history.HistoryScreen // <--- IMPORT NUEVO
import com.fabridev.austral.ui.screens.splash.SplashScreen // <--- IMPORT NUEVO

@Composable
fun AppNavigation(viewModel: HomeViewModel) {
    val navController = rememberNavController()

    // CAMBIO IMPORTANTE: startDestination ahora es "splash"
    NavHost(navController = navController, startDestination = "splash") {

        // 1. SPLASH
        composable("splash") {
            SplashScreen(onNavigateToHome = {
                // Navegamos al home y borramos el splash del historial para no volver atrás
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 2. HOME
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate("add") },
                onNavigateToHistory = { navController.navigate("history") } // <--- NUEVO
            )
        }

        // 3. AGREGAR
        composable("add") {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 4. HISTORIAL (NUEVO)
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}