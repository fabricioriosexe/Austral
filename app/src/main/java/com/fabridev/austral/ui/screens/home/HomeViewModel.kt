package com.fabridev.austral.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fabridev.austral.data.DolarRepository
import com.fabridev.austral.data.local.GoalDao
import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.data.local.TransactionDao
import com.fabridev.austral.data.local.TransactionEntity
import com.fabridev.austral.data.remote.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : ViewModel() {

    private val dolarRepository = DolarRepository(RetrofitClient.api)
    private val _dolarPrice = MutableStateFlow(1150.0)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDolarPrice()
        createDummyGoal()

        viewModelScope.launch {
            combine(
                transactionDao.getAllTransactions(),
                transactionDao.getTotalBalance(),
                goalDao.getAllGoals(),
                _dolarPrice
            ) { transactions, balance, goals, dolarValue ->
                HomeUiState(
                    transactions = transactions,
                    totalBalance = balance ?: 0.0,
                    goals = goals,
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

    fun addTransaction(amount: Double, description: String, isExpense: Boolean) {
        viewModelScope.launch {
            transactionDao.insertTransaction(
                TransactionEntity(amount = amount, description = description, isExpense = isExpense, currencyCode = "ARS")
            )
        }
    }

    // --- NUEVAS FUNCIONES PARA METAS ---

    fun addGoal(name: String, target: Double, saved: Double, currency: String) {
        viewModelScope.launch {
            goalDao.insertGoal(
                GoalEntity(
                    name = name,
                    targetAmount = target,
                    savedAmount = saved,
                    currencyCode = currency
                )
            )
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal)
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalDao.updateGoal(goal)
        }
    }
}

class HomeViewModelFactory(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(transactionDao, goalDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}