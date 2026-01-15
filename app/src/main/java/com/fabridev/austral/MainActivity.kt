package com.fabridev.austral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.fabridev.austral.data.local.AppDatabase
import com.fabridev.austral.data.local.TransactionEntity
import com.fabridev.austral.ui.screens.HomeScreen
import com.fabridev.austral.ui.theme.AustralTheme
import com.fabridev.austral.viewmodel.MainViewModel
import com.fabridev.austral.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZAMOS LA BASE DE DATOS
        val database = AppDatabase.getDatabase(this)
        val dao = database.transactionDao()

        // 2. CREAMOS EL VIEWMODEL CON LA FÁBRICA
        val viewModelFactory = MainViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            AustralTheme {
                // 3. MOSTRAMOS LA PANTALLA
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}