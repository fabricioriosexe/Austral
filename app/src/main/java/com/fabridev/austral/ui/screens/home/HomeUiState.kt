package com.fabridev.austral.ui.screens.home

import com.fabridev.austral.data.local.TransactionEntity

data class HomeUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val dolarBlue: Double = 1150.0 // <--- Valor por defecto inicial
)