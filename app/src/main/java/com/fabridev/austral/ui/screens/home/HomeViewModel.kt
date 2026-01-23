package com.fabridev.austral.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fabridev.austral.data.DolarRepository
import com.fabridev.austral.data.local.TransactionDao
import com.fabridev.austral.data.local.TransactionEntity
import com.fabridev.austral.data.remote.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: TransactionDao) : ViewModel() {

    // 1. Instanciamos el Repositorio (nuestro intermediario con internet)
    private val dolarRepository = DolarRepository(RetrofitClient.api)

    // 2. Creamos un flujo separado para el precio del Dólar (arranca en 1150 por defecto)
    private val _dolarPrice = MutableStateFlow(1150.0)

    // Estado público para la UI
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Apenas arranca la pantalla, salimos a buscar el precio real
        fetchDolarPrice()

        // 3. LA MAGIA: Combinamos 3 flujos de datos a la vez
        viewModelScope.launch {
            combine(
                dao.getAllTransactions(), // Flujo 1: Lista de gastos
                dao.getTotalBalance(),    // Flujo 2: Balance total
                _dolarPrice               // Flujo 3: Precio del Dólar
            ) { transactions, balance, dolarValue ->
                // Cuando cualquiera de los 3 cambie, se crea un nuevo estado
                HomeUiState(
                    transactions = transactions,
                    totalBalance = balance ?: 0.0,
                    dolarBlue = dolarValue, // Usamos el valor del flujo del dólar
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    // Función para pedir el precio a la API
    private fun fetchDolarPrice() {
        viewModelScope.launch {
            // El repositorio se encarga de probar si hay internet
            val price = dolarRepository.getBluePrice()

            // Si trajo un precio válido (no es null), actualizamos nuestro flujo
            if (price != null) {
                _dolarPrice.value = price
            }
        }
    }

    // Función para agregar transacciones (igual que antes)
    fun addTransaction(amount: Double, description: String, isExpense: Boolean) {
        viewModelScope.launch {
            dao.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    description = description,
                    isExpense = isExpense,
                    currencyCode = "ARS"
                )
            )
        }
    }
}

// La Fábrica queda igual
class HomeViewModelFactory(private val dao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}