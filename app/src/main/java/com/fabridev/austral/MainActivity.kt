package com.fabridev.austral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.fabridev.austral.data.local.AppDatabase
import com.fabridev.austral.ui.AppNavigation // <--- IMPORTANTE
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.screens.home.HomeViewModelFactory
import com.fabridev.austral.ui.theme.AustralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val dao = database.transactionDao()
        val viewModelFactory = HomeViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setContent {
            AustralTheme {
                // CORRECCIÓN: Llamamos a la navegación, no directo al Home
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}