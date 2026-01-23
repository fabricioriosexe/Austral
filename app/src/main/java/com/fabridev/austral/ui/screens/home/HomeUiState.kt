package com.fabridev.austral.ui.screens.home

import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.data.local.TransactionEntity

data class HomeUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val goals: List<GoalEntity> = emptyList(), // <--- AGREGAR ESTO}
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val dolarBlue: Double = 1150.0
)