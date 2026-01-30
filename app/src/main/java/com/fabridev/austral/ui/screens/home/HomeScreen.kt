package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.data.local.TransactionEntity
import com.fabridev.austral.ui.theme.AustralTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToGoalDetail: (Int) -> Unit,
    onNavigateToDebts: () -> Unit // <--- 1. NUEVO PARÁMETRO
) {
    val state by viewModel.uiState.collectAsState()

    HomeContent(
        state = state,
        onAddTransaction = onNavigateToAdd,
        onViewHistory = onNavigateToHistory,
        onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
        onAddGoalClick = onNavigateToAddGoal,
        onGoalClick = { goalId -> onNavigateToGoalDetail(goalId) },
        onDebtsClick = onNavigateToDebts // <--- 2. CONECTAMOS
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onAddTransaction: () -> Unit,
    onViewHistory: () -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit,
    onAddGoalClick: () -> Unit,
    onGoalClick: (Int) -> Unit,
    onDebtsClick: () -> Unit // <--- 3. RECIBIMOS
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Header
            WalletHeader()

            // 2. Balance
            NetWorthCard(
                totalBalanceARS = state.totalBalance,
                dolarPrice = state.dolarBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Ticker
            TickerRow(dolarBluePrice = state.dolarBlue)

            // 4. Botones
            ActionButtonsRow(
                onAddClick = onAddTransaction,
                onDebtsClick = onDebtsClick // <--- 4. FINALMENTE LO USAMOS
            )

            // 5. Metas
            GoalsSection(
                goals = state.goals,
                onDeleteGoal = onDeleteGoal,
                onGoalClick = { goal -> onGoalClick(goal.id) },
                onAddGoalClick = onAddGoalClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Cabecera Lista
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimos Movimientos",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewHistory) {
                    Text("Ver todo", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 7. Lista (Limitada a 4)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.transactions.take(4)) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.description, color = Color.White, fontWeight = FontWeight.Medium)
                val date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(transaction.date))
                Text(text = date, color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                text = (if (transaction.isExpense) "- $" else "+ $") + transaction.amount,
                color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    AustralTheme {
        HomeContent(
            state = HomeUiState(totalBalance = 150000.0),
            onAddTransaction = {},
            onViewHistory = {},
            onDeleteGoal = {},
            onAddGoalClick = {},
            onGoalClick = {},
            onDebtsClick = {} // <--- Fix para el Preview
        )
    }
}