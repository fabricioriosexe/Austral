package com.fabridev.austral.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: Int, // Recibimos el ID de la meta
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    // Buscamos la meta específica en la lista del ViewModel usando el ID
    val state by viewModel.uiState.collectAsState()
    val goal = state.goals.find { it.id == goalId }

    // Estados para el popup de agregar dinero
    var showDialog by remember { mutableStateOf(false) }
    var amountToAdd by remember { mutableStateOf("") }

    // Si no encuentra la meta (ej: se borró), mostramos carga o nada
    if (goal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Calculamos porcentaje visual
    val percentage = if (goal.targetAmount > 0) ((goal.savedAmount / goal.targetAmount) * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(goal.name, color = Color.White) },
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
                onClick = { showDialog = true },
                containerColor = Color(0xFF6C5CE7),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Sumar")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. CÍRCULO GIGANTE
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
                // Fondo gris
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2D3440),
                    strokeWidth = 18.dp,
                )
                // Progreso violeta
                CircularProgressIndicator(
                    progress = { (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF6C5CE7),
                    strokeWidth = 18.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$percentage%", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Text("Completado", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 2. DATOS NUMÉRICOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B26), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ahorrado", color = Color.Gray)
                    Text("$ ${goal.savedAmount.toInt()}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Meta Total", color = Color.Gray)
                    Text("$ ${goal.targetAmount.toInt()}", color = Color(0xFF00CEC9), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Moneda: ${goal.currencyCode}", color = Color.Gray, fontSize = 12.sp)
        }

        // 3. POPUP PARA SUMAR PLATA
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Sumar Ahorros") },
                text = {
                    Column {
                        Text("¿Cuánto ahorraste hoy?")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amountToAdd,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amountToAdd = it },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = amountToAdd.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                // LLAMAMOS A LA FUNCIÓN DEL VIEWMODEL
                                viewModel.addFundsToGoal(goal, amount)
                                showDialog = false
                                amountToAdd = ""
                            }
                        }
                    ) {
                        Text("Sumar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}