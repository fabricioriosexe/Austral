package com.fabridev.austral.ui.screens.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current // Para mostrar mensajes (Toasts)

    // Variables del formulario
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Transacción", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
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

            // 1. SELECTOR (Ingreso vs Gasto)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B26), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                TabButton(text = "Gasto", isSelected = isExpense, onClick = { isExpense = true })
                TabButton(text = "Ingreso", isSelected = !isExpense, onClick = { isExpense = false })
            }

            Spacer(modifier = Modifier.height(50.dp)) // Espacio para bajar el input

            // 2. INPUT DE MONTO (MEJORADO)
            Text("Monto", color = Color.Gray)

            TextField(
                value = amount,
                onValueChange = {
                    // Solo permite números y un punto decimal
                    if (it.all { char -> char.isDigit() || char == '.' }) amount = it
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                    color = if(isExpense) Color(0xFFFF7675) else Color(0xFF00CEC9), // Rojo o Verde según tipo
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center // <--- ESTO CENTRA EL TEXTO
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                // Agregamos el signo $ fijo
                prefix = {
                    Text(
                        "$ ",
                        fontSize = 48.sp,
                        color = if(isExpense) Color(0xFFFF7675) else Color(0xFF00CEC9),
                        fontWeight = FontWeight.Bold
                    )
                },
                placeholder = {
                    Text(
                        "0.00",
                        fontSize = 48.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. INPUT DE DESCRIPCIÓN
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ej: Supermercado, Sueldo...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 4. BOTÓN GUARDAR
            Button(
                onClick = {
                    val finalAmount = amount.toDoubleOrNull()

                    // VALIDACIÓN VISUAL
                    if (finalAmount == null || finalAmount <= 0) {
                        Toast.makeText(context, "Ingresá un monto válido", Toast.LENGTH_SHORT).show()
                    } else if (description.isEmpty()) {
                        Toast.makeText(context, "Falta la descripción", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si todo está bien, guardamos
                        viewModel.addTransaction(finalAmount, description, isExpense)
                        Toast.makeText(context, "¡Guardado!", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExpense) Color(0xFFFF7675) else Color(0xFF00CEC9)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Movimiento", fontSize = 18.sp)
            }
        }
    }
}

// Botón del selector superior
@Composable
fun RowScope.TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .background(
                if (isSelected) Color(0xFF2D3440) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}