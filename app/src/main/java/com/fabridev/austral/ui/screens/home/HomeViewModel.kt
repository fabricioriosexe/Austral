package com.fabridev.austral.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fabridev.austral.data.DolarRepository
import com.fabridev.austral.data.local.*
import com.fabridev.austral.data.remote.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val debtDao: DebtDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val dolarRepository = DolarRepository(RetrofitClient.api)
    private val _dolarPrice = MutableStateFlow(1150.0)
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchDolarPrice()
        createDummyGoal()

        viewModelScope.launch {
            // PASO 1: Combinamos los 4 datos financieros
            val financialDataFlow = combine(
                transactionDao.getAllTransactions(),
                transactionDao.getTotalBalance(),
                goalDao.getAllGoals(),
                debtDao.getAllDebts()
            ) { transactions, balance, goals, debts ->
                FinancialData(transactions, balance ?: 0.0, goals, debts)
            }

            // PASO 2: Combinamos con Dólar, Nombre y Moneda
            combine(
                financialDataFlow,
                _dolarPrice,
                userPreferences.userName,
                userPreferences.userCurrency
            ) { financial, dolarValue, name, currency ->
                HomeUiState(
                    transactions = financial.transactions,
                    totalBalance = financial.balance,
                    goals = financial.goals,
                    debts = financial.debts,
                    dolarBlue = dolarValue,
                    userName = name,
                    userCurrency = currency,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    // --- FUNCIONES DE PERFIL ---
    fun updateUserName(newName: String) {
        viewModelScope.launch { userPreferences.saveUserName(newName) }
    }

    fun updateUserCurrency(newCurrency: String) {
        viewModelScope.launch { userPreferences.saveUserCurrency(newCurrency) }
    }

    // --- FUNCIONES DE FINANZAS ---
    private fun createDummyGoal() {
        viewModelScope.launch {
            val goals = goalDao.getAllGoals().first()
            if (goals.isEmpty()) {
                goalDao.insertGoal(GoalEntity(name = "Auto para Brasil 🇧🇷", targetAmount = 5000.0, savedAmount = 2500.0, currencyCode = "USD"))
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
                TransactionEntity(amount = amount, description = description, isExpense = isExpense, currencyCode = "ARS", category = category)
            )
        }
    }

    // --- METAS ---
    fun addGoal(name: String, target: Double, saved: Double, currency: String) {
        viewModelScope.launch {
            goalDao.insertGoal(GoalEntity(name = name, targetAmount = target, savedAmount = saved, currencyCode = currency))
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

    // --- DEUDAS ---
    fun addDebt(name: String, amount: Double) {
        viewModelScope.launch {
            debtDao.insertDebt(DebtEntity(name = name, totalAmount = amount, remainingAmount = amount))
        }
    }

    // 🔥 ESTA ES LA FUNCIÓN CORREGIDA
    fun payDebt(debt: DebtEntity, amountToPay: Double) {
        viewModelScope.launch {
            // 1. Actualizamos la Deuda (La bajamos)
            val newRemaining = (debt.remainingAmount - amountToPay).coerceAtLeast(0.0)
            val updatedDebt = debt.copy(remainingAmount = newRemaining)
            debtDao.updateDebt(updatedDebt)

            // 2. Insertamos el Gasto (Restamos la plata de tu billetera)
            transactionDao.insertTransaction(
                TransactionEntity(
                    amount = amountToPay,
                    description = "Pago Deuda: ${debt.name}",
                    isExpense = true, // ¡Es un gasto!
                    currencyCode = "ARS",
                    category = "OTROS" // O "DEUDAS" si ya creaste esa categoría
                )
            )
        }
    }
}

// CLASE AUXILIAR
data class FinancialData(
    val transactions: List<TransactionEntity>,
    val balance: Double,
    val goals: List<GoalEntity>,
    val debts: List<DebtEntity>
)

// FACTORY
class HomeViewModelFactory(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val debtDao: DebtDao,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(transactionDao, goalDao, debtDao, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}