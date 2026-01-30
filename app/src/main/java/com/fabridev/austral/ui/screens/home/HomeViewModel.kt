package com.fabridev.austral.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fabridev.austral.data.DolarRepository
import com.fabridev.austral.data.local.* // Importa todos los DAOs y Entities
import com.fabridev.austral.data.remote.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val debtDao: DebtDao // <--- 1. NUEVO DAO INYECTADO
) : ViewModel() {

    private val dolarRepository = DolarRepository(RetrofitClient.api)
    private val _dolarPrice = MutableStateFlow(1150.0)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDolarPrice()
        createDummyGoal()

        viewModelScope.launch {
            // 2. AGREGAMOS LAS DEUDAS AL COMBINE
            combine(
                transactionDao.getAllTransactions(),
                transactionDao.getTotalBalance(),
                goalDao.getAllGoals(),
                debtDao.getAllDebts(), // <--- Escuchamos las deudas
                _dolarPrice
            ) { transactions, balance, goals, debts, dolarValue ->
                HomeUiState(
                    transactions = transactions,
                    totalBalance = balance ?: 0.0,
                    goals = goals,
                    debts = debts, // <--- Las guardamos en el estado
                    dolarBlue = dolarValue,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun createDummyGoal() {
        viewModelScope.launch {
            val goals = goalDao.getAllGoals().first()
            if (goals.isEmpty()) {
                goalDao.insertGoal(
                    GoalEntity(
                        name = "Auto para Brasil 🇧🇷",
                        targetAmount = 5000.0,
                        savedAmount = 2500.0,
                        currencyCode = "USD"
                    )
                )
            }
        }
    }

    private fun fetchDolarPrice() {
        viewModelScope.launch {
            val price = dolarRepository.getBluePrice()
            if (price != null) _dolarPrice.value = price
        }
    }


    fun addTransaction(amount: Double, description: String, isExpense: Boolean, category: String) {
        viewModelScope.launch {
            transactionDao.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    description = description,
                    isExpense = isExpense,
                    currencyCode = "ARS",
                    category = category // <--- GUARDAMOS LA CATEGORÍA
                )
            )
        }
    }

    // --- FUNCIONES PARA METAS ---
    fun addGoal(name: String, target: Double, saved: Double, currency: String) {
        viewModelScope.launch {
            goalDao.insertGoal(
                GoalEntity(name = name, targetAmount = target, savedAmount = saved, currencyCode = currency)
            )
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch { goalDao.deleteGoal(goal) }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch { goalDao.updateGoal(goal) }
    }

    fun addFundsToGoal(goal: GoalEntity, amountToAdd: Double) {
        val updatedGoal = goal.copy(savedAmount = goal.savedAmount + amountToAdd)
        viewModelScope.launch { goalDao.updateGoal(updatedGoal) }
    }

    // --- 3. NUEVAS FUNCIONES PARA DEUDAS ---
    fun addDebt(name: String, amount: Double) {
        viewModelScope.launch {
            debtDao.insertDebt(
                DebtEntity(name = name, totalAmount = amount, remainingAmount = amount)
            )
        }
    }

    fun payDebt(debt: DebtEntity, amountToPay: Double) {
        // Calculamos cuánto falta (no dejamos que baje de 0)
        val newRemaining = (debt.remainingAmount - amountToPay).coerceAtLeast(0.0)
        val updatedDebt = debt.copy(remainingAmount = newRemaining)

        viewModelScope.launch {
            debtDao.updateDebt(updatedDebt)
        }
    }
}

// 4. FACTORY ACTUALIZADA PARA RECIBIR 3 DAOS
class HomeViewModelFactory(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val debtDao: DebtDao // <--- NUEVO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(transactionDao, goalDao, debtDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}