package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.ui.theme.AustralTheme

// Asumimos que WalletHeader, NetWorthCard, TickerRow, etc.
// están en tu archivo 'HomeComponents.kt' y se importan solos.

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

    // --- DIÁLOGO DE EDICIÓN (NOMBRE + MONEDA) ---
    if (showNameDialog) {
        var tempName by remember { mutableStateOf("") }
        // Inicializamos la moneda con la que tiene el usuario actualmente
        var selectedCurrency by remember { mutableStateOf(state.userCurrency) }

        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    // 1. CAMBIAR NOMBRE
                    Text("Tu Nombre:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = { Text(state.userName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C5CE7),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. SELECTOR DE MONEDA
                    Text("Moneda Principal:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CurrencyOption("ARS", selectedCurrency) { selectedCurrency = "ARS" }
                        CurrencyOption("USD", selectedCurrency) { selectedCurrency = "USD" }
                        CurrencyOption("BRL", selectedCurrency) { selectedCurrency = "BRL" }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotEmpty()) {
                            viewModel.updateUserName(tempName)
                        }
                        // Guardamos la moneda seleccionada
                        viewModel.updateUserCurrency(selectedCurrency)
                        showNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF2D3440),
            titleContentColor = Color.White,
            textContentColor = Color.White
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
        // USAMOS LAZYCOLUMN COMO CONTENEDOR PRINCIPAL
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            // 6. Título Lista
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
            val recentTransactions = state.transactions.take(10) // Muestra hasta 10 si hay espacio

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
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Espacio final
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }
}

// Helper pequeño para el diálogo (lo dejo aquí para no romper el import)
@Composable
private fun CurrencyOption(currency: String, selected: String, onSelect: () -> Unit) {
    val isSelected = currency == selected
    val bgColor = if (isSelected) Color(0xFF00B894) else Color(0xFF161B26)
    val txtColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = currency, color = txtColor, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun HomePreview() {
    AustralTheme {
        HomeContent(
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