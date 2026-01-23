package com.fabridev.austral.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fabridev.austral.ui.screens.add.AddTransactionScreen
import com.fabridev.austral.ui.screens.goals.AddGoalScreen // <--- Importante
import com.fabridev.austral.ui.screens.history.HistoryScreen
import com.fabridev.austral.ui.screens.home.HomeScreen
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation(viewModel: HomeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        // 1. SPLASH
        composable("splash") {
            SplashScreen(onNavigateToHome = {
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
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToAddGoal = { navController.navigate("add_goal") } // <--- ¡ESTA ES LA LÍNEA QUE FALTABA!
            )
        }

        // 3. AGREGAR GASTO
        composable("add") {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 4. HISTORIAL
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 5. AGREGAR META (GOAL)
        composable("add_goal") {
            AddGoalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}