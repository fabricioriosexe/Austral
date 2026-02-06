package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabridev.austral.data.local.GoalEntity
import com.fabridev.austral.data.local.TransactionEntity
import com.fabridev.austral.ui.utils.getCategoryById
import java.util.Locale

// 1. EL HEADER
@Composable
fun WalletHeader(
    userName: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onEditClick() }
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Welcome back,", color = Color.Gray, fontSize = 12.sp)
                Text(text = userName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        IconButton(onClick = { /* Notificaciones */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
        }
    }
}

// 2. NET WORTH CARD (ACTUALIZADA CON LÓGICA DE MONEDA)
@Composable
fun NetWorthCard(
    totalBalanceARS: Double,
    dolarPrice: Double,
    currencyCode: String // <--- Recibe ARS, USD o BRL
) {
    // Calculamos el balance a mostrar según la moneda elegida
    val displayBalance = when(currencyCode) {
        "USD" -> totalBalanceARS / (if (dolarPrice > 0) dolarPrice else 1150.0)
        "BRL" -> totalBalanceARS / 200.0 // Estimado fijo para MVP (1 Real = 200 Pesos)
        else -> totalBalanceARS // Por defecto ARS
    }

    // Elegimos el símbolo
    val symbol = when(currencyCode) {
        "USD" -> "US$"
        "BRL" -> "R$"
        else -> "$"
    }

    // También mostramos cuánto sería en dólares abajo si no estamos en USD
    val secondaryInfo = if (currencyCode == "USD") {
        "ARS (Blue: $${dolarPrice.toInt()})"
    } else {
        val totalUSD = totalBalanceARS / (if (dolarPrice > 0) dolarPrice else 1150.0)
        "Equivale a US$ ${totalUSD.toInt()}"
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "TOTAL NET WORTH", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Surface(color = Color(0xFF00B894).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text(text = "🛡 Safe", color = Color(0xFF00B894), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // NÚMERO GRANDE (CONVERTIDO)
                Text(text = "$symbol ${String.format(Locale.US, "%.2f", displayBalance)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)

                // INFORMACIÓN SECUNDARIA
                Text(text = secondaryInfo, color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.weight(1f))

                // CHIP CON EL MONTO ORIGINAL EN ARS (Si no estamos en ARS)
                if (currencyCode != "ARS") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFF0984E3).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text(text = "🇦🇷 $totalBalanceARS ARS", color = Color(0xFF74B9FF), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

// 3. ACTION BUTTONS
@Composable
fun ActionButtonsRow(
    onAddClick: () -> Unit,
    onDebtsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ActionButton(icon = Icons.Default.QrCodeScanner, label = "Scan", color = Color(0xFF6C5CE7))
        ActionButton(icon = Icons.Default.Add, label = "Add", color = Color(0xFF6C5CE7), isMain = true, onClick = onAddClick)
        ActionButton(icon = Icons.Default.CreditCard, label = "Deudas", color = Color(0xFF6C5CE7), onClick = onDebtsClick)
        ActionButton(icon = Icons.Default.MoreHoriz, label = "More", color = Color.Gray)
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, color: Color, isMain: Boolean = false, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (isMain) 64.dp else 56.dp)
                .clip(CircleShape)
                .background(if (isMain) Brush.linearGradient(listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE))) else SolidColor(Color(0xFF2D3440)))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isMain) Color.White else color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}

// 4. TICKER ROW
@Composable
fun TickerRow(dolarBluePrice: Double) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { TickerItem(name = "Dólar Blue", price = "$${dolarBluePrice.toInt()}", change = "+0%", isPositive = true) }
        item { TickerItem("Bitcoin", "$98,200", "-1%", false) }
        item { TickerItem("USDT", "$1,130", "0%", true) }
    }
}

@Composable
fun TickerItem(name: String, price: String, change: String, isPositive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = name, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = price, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = change, color = if (isPositive) Color(0xFF00CEC9) else Color(0xFFFF7675), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. SECCIÓN DE METAS
@Composable
fun GoalsSection(
    goals: List<GoalEntity>,
    onDeleteGoal: (GoalEntity) -> Unit,
    onGoalClick: (GoalEntity) -> Unit,
    onAddGoalClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mis Metas 🚀", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            IconButton(
                onClick = onAddGoalClick,
                modifier = Modifier.size(28.dp).background(Color(0xFF2D3440), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Meta", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        if (goals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onAddGoalClick() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear mi primera meta", color = Color.Gray)
                    }
                }
            }
        } else {
            if (goals.size == 1) {
                GoalItem(
                    goal = goals[0],
                    onDelete = { onDeleteGoal(goals[0]) },
                    onClick = { onGoalClick(goals[0]) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(goals) { goal ->
                        GoalItem(
                            goal = goal,
                            onDelete = { onDeleteGoal(goal) },
                            onClick = { onGoalClick(goal) },
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalItem(
    goal: GoalEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).toFloat() else 0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.height(160.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = goal.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(text = "Meta: ${goal.currencyCode}", color = Color.Gray, fontSize = 10.sp)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color(0xFFFF7675), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$percentage%", color = Color(0xFF6C5CE7), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF2D3440))
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE))))
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "$ ${goal.savedAmount.toInt()}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "/ ${goal.targetAmount.toInt()}", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

// 6. ITEM DE TRANSACCIÓN
@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val categoryData = getCategoryById(transaction.category)

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(categoryData.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        categoryData.icon,
                        contentDescription = null,
                        tint = categoryData.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    val mainText = if (transaction.description.isNotEmpty()) transaction.description else categoryData.name
                    Text(text = mainText, color = Color.White, fontWeight = FontWeight.Medium)

                    val date = java.text.SimpleDateFormat("dd/MM", Locale.getDefault()).format(java.util.Date(transaction.date))
                    Text(text = "$date • ${categoryData.name}", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Text(
                text = (if (transaction.isExpense) "- $" else "+ $") + transaction.amount.toInt(),
                color = if (transaction.isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}