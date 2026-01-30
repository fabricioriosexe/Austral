package com.fabridev.austral.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Modelo de datos para una categoría
data class CategoryData(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

// LISTA DE GASTOS
val expenseCategories = listOf(
    CategoryData("COMIDA", "Comida", Icons.Default.Restaurant, Color(0xFFFF7675)),
    CategoryData("TRANSPORTE", "Transp.", Icons.Default.DirectionsCar, Color(0xFF74B9FF)),
    CategoryData("COMPRAS", "Compras", Icons.Default.ShoppingBag, Color(0xFFA29BFE)),
    CategoryData("OCIO", "Ocio", Icons.Default.SportsEsports, Color(0xFFFD79A8)),
    CategoryData("SALUD", "Salud", Icons.Default.MedicalServices, Color(0xFF00B894)),
    CategoryData("CASA", "Casa", Icons.Default.Home, Color(0xFFFFEAA7)),
    CategoryData("OTROS", "Otros", Icons.Default.MoreHoriz, Color.Gray)
)

// LISTA DE INGRESOS
val incomeCategories = listOf(
    CategoryData("SUELDO", "Sueldo", Icons.Default.AttachMoney, Color(0xFF00B894)),
    CategoryData("VENTAS", "Ventas", Icons.Default.Store, Color(0xFF74B9FF)),
    CategoryData("REGALO", "Regalo", Icons.Default.CardGiftcard, Color(0xFFA29BFE)),
    CategoryData("OTROS", "Otros", Icons.Default.MoreHoriz, Color.Gray)
)

// Función para buscar por ID (la usaremos en el Home)
fun getCategoryById(id: String): CategoryData {
    return (expenseCategories + incomeCategories).find { it.id == id }
        ?: CategoryData("OTROS", "Otros", Icons.Default.MoreHoriz, Color.Gray)
}