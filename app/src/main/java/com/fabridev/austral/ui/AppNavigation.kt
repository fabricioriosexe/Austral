package com.fabridev.austral.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fabridev.austral.ui.screens.add.AddTransactionScreen
import com.fabridev.austral.ui.screens.goals.AddGoalScreen
import com.fabridev.austral.ui.screens.goals.GoalDetailScreen
import com.fabridev.austral.ui.screens.history.HistoryScreen
import com.fabridev.austral.ui.screens.home.HomeScreen
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.splash.SplashScreen
import com.fabridev.austral.ui.screens.debts.DebtScreen

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
                onNavigateToAddGoal = { navController.navigate("add_goal") },
                onNavigateToGoalDetail = { goalId -> navController.navigate("goal_detail/$goalId") },
                onNavigateToDebts = { navController.navigate("debts") } // <--- ¡ESTO FALTABA!
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

        // 5. AGREGAR META
        composable("add_goal") {
            AddGoalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 6. DETALLE DE META
        composable(
            route = "goal_detail/{goalId}",
            arguments = listOf(navArgument("goalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0

            GoalDetailScreen(
                goalId = goalId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 7. DEUDAS
        composable("debts") {
            DebtScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}