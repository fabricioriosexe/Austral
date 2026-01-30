package com.fabridev.austral.ui.screens.debts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.data.local.DebtEntity
import com.fabridev.austral.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Estados para los Diálogos
    var showAddDialog by remember { mutableStateOf(false) }
    var showPayDialog by remember { mutableStateOf<DebtEntity?>(null) } // Guarda qué deuda vas a pagar

    // Variables para inputs
    var newName by remember { mutableStateOf("") }
    var newAmount by remember { mutableStateOf("") }
    var payAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Deudas 📉", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFF7675), // Color Salmón para deudas
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Deuda")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (state.debts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("¡Sos libre! No tenés deudas 🎉", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.debts) { debt ->
                        DebtItem(
                            debt = debt,
                            onPayClick = { showPayDialog = debt } // Abrimos el diálogo para ESTA deuda
                        )
                    }
                }
            }
        }

        // --- DIÁLOGO 1: AGREGAR DEUDA ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nueva Deuda") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = { Text("Ej: Tarjeta Visa") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newAmount,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) newAmount = it },
                            placeholder = { Text("Monto Total") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = newAmount.toDoubleOrNull()
                            if (newName.isNotEmpty() && amount != null) {
                                viewModel.addDebt(newName, amount)
                                showAddDialog = false
                                newName = ""
                                newAmount = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7675))
                    ) { Text("Guardar") }
                },
                dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") } }
            )
        }

        // --- DIÁLOGO 2: PAGAR DEUDA ---
        if (showPayDialog != null) {
            AlertDialog(
                onDismissRequest = { showPayDialog = null },
                title = { Text("Pagar a ${showPayDialog?.name}") },
                text = {
                    Column {
                        Text("Deuda restante: $${showPayDialog?.remainingAmount}")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = payAmount,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) payAmount = it },
                            placeholder = { Text("¿Cuánto pagás hoy?") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = payAmount.toDoubleOrNull()
                            if (amount != null && showPayDialog != null) {
                                viewModel.payDebt(showPayDialog!!, amount)
                                showPayDialog = null
                                payAmount = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894)) // Verde para pagar
                    ) { Text("Pagar") }
                },
                dismissButton = { TextButton(onClick = { showPayDialog = null }) { Text("Cancelar") } }
            )
        }
    }
}

@Composable
fun DebtItem(debt: DebtEntity, onPayClick: () -> Unit) {
    val progress = if (debt.totalAmount > 0) 1f - (debt.remainingAmount / debt.totalAmount).toFloat() else 0f

    // Si ya pagó todo (0 restante), mostramos tarjeta verde o la ocultamos
    val isPaid = debt.remainingAmount <= 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isPaid) Color(0xFF00B894) else Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = debt.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (isPaid) {
                    Text("PAGADO ✅", color = Color(0xFF00B894), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Text("Falta: $${debt.remainingAmount.toInt()}", color = Color(0xFFFF7675), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso (Rojo = Deuda, Verde = Pagado)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00B894), // Lo pagado es verde
                trackColor = Color(0xFF2D3440), // Fondo oscuro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total: $${debt.totalAmount.toInt()}", color = Color.Gray, fontSize = 12.sp)

                if (!isPaid) {
                    TextButton(onClick = onPayClick) {
                        Text("Pagar cuota", color = Color(0xFF74B9FF))
                    }
                }
            }
        }
    }
}