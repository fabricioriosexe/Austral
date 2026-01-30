package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fabridev.austral.ui.theme.AustralTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAddGoal: () -> Unit,
    onNavigateToGoalDetail: (Int) -> Unit,
    onNavigateToDebts: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    HomeContent(
        state = state,
        onAddTransaction = onNavigateToAdd,
        onViewHistory = onNavigateToHistory,
        onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
        onAddGoalClick = onNavigateToAddGoal,
        onGoalClick = { goalId -> onNavigateToGoalDetail(goalId) },
        onDebtsClick = onNavigateToDebts
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
    onDebtsClick: () -> Unit
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
                onDebtsClick = onDebtsClick
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
                    // ACÁ USA AUTOMÁTICAMENTE EL COMPONENTE NUEVO QUE ESTÁ EN HomeComponents.kt
                    TransactionItem(transaction)
                }
            }
        }
    }
}

// ⚠️ NOTA: Borré el TransactionItem viejo de acá para que use el nuevo con íconos

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
            onDebtsClick = {}
        )
    }
}