package com.fabridev.austral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.fabridev.austral.data.local.AppDatabase
import com.fabridev.austral.ui.AppNavigation
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.home.HomeViewModelFactory
import com.fabridev.austral.ui.theme.AustralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtenemos la base de datos
        val database = AppDatabase.getDatabase(this)

        // 2. Sacamos los TRES DAOs necesarios
        val transactionDao = database.transactionDao()
        val goalDao = database.goalDao()
        val debtDao = database.debtDao() // <--- NUEVO: Obtenemos el DAO de deudas

        // 3. Se los pasamos a la Fábrica (AGREGAMOS debtDao AL FINAL)
        val viewModelFactory = HomeViewModelFactory(transactionDao, goalDao, debtDao)

        // 4. Creamos el ViewModel
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setContent {
            AustralTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}