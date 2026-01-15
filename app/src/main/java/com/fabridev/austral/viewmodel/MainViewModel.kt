package com.fabridev.austral.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fabridev.austral.data.local.TransactionDao
import com.fabridev.austral.data.local.TransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val dao: TransactionDao) : ViewModel() {

    // 1. ESTADO: La lista de movimientos que ve la pantalla.
    // 'stateIn' convierte el flujo de la base de datos en un estado siempre listo para la UI.
    val transactions: StateFlow<List<TransactionEntity>> = dao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. ESTADO: El Balance Total (La suma de plata).
    val totalBalance: StateFlow<Double?> = dao.getTotalBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 3. ACCIONES: Funciones que la pantalla puede llamar.

    fun addTransaction(amount: Double, description: String, isExpense: Boolean, currency: String = "ARS") {
        viewModelScope.launch {
            val newTransaction = TransactionEntity(
                amount = amount,
                description = description,
                isExpense = isExpense,
                currencyCode = currency
            )
            dao.insertTransaction(newTransaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }
}

// --- LA FÁBRICA ---
// Esto es código "boilerplate" (aburrido pero necesario) para poder pasarle el DAO al ViewModel.
class MainViewModelFactory(private val dao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}