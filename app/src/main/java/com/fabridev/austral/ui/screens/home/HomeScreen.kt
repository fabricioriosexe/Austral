package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.ui.theme.AustralTheme

// Asumo que los componentes (WalletHeader, NetWorthCard, etc.)
// están en el mismo paquete y se importan automáticamente.

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

    // ESTADO PARA MOSTRAR/OCULTAR EL DIÁLOGO
    var showNameDialog by remember { mutableStateOf(false) }

    HomeContent(
        state = state,
        onAddTransaction = onNavigateToAdd,
        onViewHistory = onNavigateToHistory,
        onDeleteGoal = { goal -> viewModel.deleteGoal(goal) },
        onAddGoalClick = onNavigateToAddGoal,
        onGoalClick = { goalId -> onNavigateToGoalDetail(goalId) },
        onDebtsClick = onNavigateToDebts,
        onEditProfileClick = { showNameDialog = true }
    )

    // --- DIÁLOGO PARA EDITAR NOMBRE ---
    if (showNameDialog) {
        var tempName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Editar Perfil") },
            text = {
                Column {
                    Text("¿Cómo te gustaría que te llamemos?")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = { Text(state.userName) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotEmpty()) {
                            viewModel.updateUserName(tempName)
                            showNameDialog = false
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onAddTransaction: () -> Unit,
    onViewHistory: () -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit,
    onAddGoalClick: () -> Unit,
    onGoalClick: (Int) -> Unit,
    onDebtsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // CAMBIO CLAVE: Usamos LazyColumn como contenedor principal
        // Esto permite que TODA la pantalla haga scroll junto.
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre elementos
        ) {

            // 1. Header
            item {
                WalletHeader(
                    userName = state.userName,
                    onEditClick = onEditProfileClick
                )
            }

            // 2. Balance
            item {
                // Nota: Agregué currencyCode porque tu componente lo pide en el otro archivo
                NetWorthCard(
                    totalBalanceARS = state.totalBalance,
                    dolarPrice = state.dolarBlue,
                    currencyCode = state.userCurrency
                )
            }

            // 3. Ticker
            item {
                TickerRow(dolarBluePrice = state.dolarBlue)
            }

            // 4. Botones
            item {
                ActionButtonsRow(
                    onAddClick = onAddTransaction,
                    onDebtsClick = onDebtsClick
                )
            }

            // 5. Metas
            item {
                GoalsSection(
                    goals = state.goals,
                    onDeleteGoal = onDeleteGoal,
                    onGoalClick = { goal -> onGoalClick(goal.id) },
                    onAddGoalClick = onAddGoalClick
                )
            }

            // 6. Cabecera Lista (Título)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
            }

            // 7. Lista Dinámica
            // Aquí usamos 'items' directamente en la LazyColumn principal
            val recentTransactions = state.transactions.take(4)

            if (recentTransactions.isEmpty()) {
                item {
                    Text(
                        text = "Sin movimientos recientes",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(recentTransactions) { transaction ->
                    TransactionItem(transaction)
                    // Pequeño espacio extra entre items de la lista si lo deseas
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Espacio al final para que no se corte con la navegación del celular
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    AustralTheme {
        HomeContent(
            // Agregué userCurrency al mock para que compile
            state = HomeUiState(totalBalance = 150000.0, userName = "CFO Fabricio", userCurrency = "ARS"),
            onAddTransaction = {},
            onViewHistory = {},
            onDeleteGoal = {},
            onAddGoalClick = {},
            onGoalClick = {},
            onDebtsClick = {},
            onEditProfileClick = {}
        )
    }
}