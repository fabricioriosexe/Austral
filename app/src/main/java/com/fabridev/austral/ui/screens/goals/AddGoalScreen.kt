package com.fabridev.austral.ui.screens.goals
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Variables del formulario
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var currentSaved by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Meta", color = Color.White) },
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
                .padding(24.dp)
        ) {

            // 1. NOMBRE DE LA META
            Text("Nombre de la Meta", color = Color.Gray, fontSize = 14.sp)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Ej: Play 5, Viaje a Europa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. SELECTOR DE MONEDA
            Text("Moneda", color = Color.Gray, fontSize = 14.sp)
            Row(modifier = Modifier.padding(top = 8.dp)) {
                CurrencyChip(text = "USD", selected = selectedCurrency == "USD") { selectedCurrency = "USD" }
                Spacer(modifier = Modifier.width(8.dp))
                CurrencyChip(text = "ARS", selected = selectedCurrency == "ARS") { selectedCurrency = "ARS" }
                Spacer(modifier = Modifier.width(8.dp))
                CurrencyChip(text = "BRL", selected = selectedCurrency == "BRL") { selectedCurrency = "BRL" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. MONTO OBJETIVO
            Text("Monto Objetivo (Total)", color = Color.Gray, fontSize = 14.sp)
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) targetAmount = it },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. YA TENGO AHORRADO...
            Text("¿Cuánto tenés ahorrado ya?", color = Color.Gray, fontSize = 14.sp)
            OutlinedTextField(
                value = currentSaved,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) currentSaved = it },
                placeholder = { Text("0.00 (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 5. BOTÓN GUARDAR
            Button(
                onClick = {
                    val target = targetAmount.toDoubleOrNull()
                    val saved = currentSaved.toDoubleOrNull() ?: 0.0

                    if (name.isNotEmpty() && target != null && target > 0) {
                        viewModel.addGoal(name, target, saved, selectedCurrency)
                        Toast.makeText(context, "¡Meta creada! 🚀", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Completá el nombre y el monto", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Meta", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CurrencyChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFF6C5CE7) else Color(0xFF2D3440))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}