package com.fabridev.austral.ui.screens.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.ui.screens.home.HomeViewModel
import com.fabridev.austral.ui.utils.CategoryData
import com.fabridev.austral.ui.utils.expenseCategories
import com.fabridev.austral.ui.utils.incomeCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("OTROS") } // <--- Estado de categoría

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isExpense) "Nuevo Gasto" else "Nuevo Ingreso", color = Color.White) },
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
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp)
        ) {
            // 1. SWITCH TIPO
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF2D3440), RoundedCornerShape(12.dp)).padding(4.dp)) {
                TabButton("Gasto", isExpense, Modifier.weight(1f)) { isExpense = true; selectedCategory = "OTROS" }
                TabButton("Ingreso", !isExpense, Modifier.weight(1f)) { isExpense = false; selectedCategory = "OTROS" }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 2. INPUTS
            OutlinedTextField(
                value = amount, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amount = it },
                placeholder = { Text("0.00") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                placeholder = { Text("Descripción (Opcional)") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 3. GRILLA DE CATEGORÍAS
            Text("Categoría", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            val categories = if (isExpense) expenseCategories else incomeCategories
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = selectedCategory == category.id,
                        onClick = { selectedCategory = category.id }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. GUARDAR
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null) {
                        // Enviamos la categoría seleccionada
                        viewModel.addTransaction(amountValue, description, isExpense, selectedCategory)
                        onBack()
                    } else {
                        Toast.makeText(context, "Ingresá un monto", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isExpense) Color(0xFFFF7675) else Color(0xFF00B894))
            ) {
                Text("Guardar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(40.dp).clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF6C5CE7) else Color.Transparent)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CategoryItem(category: CategoryData, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape)
                .background(if (isSelected) category.color else Color(0xFF2D3440)).clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = if (isSelected) Color.White else category.color)
        }
        Text(category.name, color = if (isSelected) Color.White else Color.Gray, fontSize = 10.sp, maxLines = 1)
    }
}