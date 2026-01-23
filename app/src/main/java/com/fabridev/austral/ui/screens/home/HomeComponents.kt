package com.fabridev.austral.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import java.util.Locale

// 1. EL HEADER
@Composable
fun WalletHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Welcome back,", color = Color.Gray, fontSize = 12.sp)
                Text(text = "CFO Fabricio", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
        }
    }
}

// 2. NET WORTH CARD (¡MODIFICADA PARA API!)
@Composable
fun NetWorthCard(totalBalanceARS: Double, dolarPrice: Double) { // <--- Aceptamos el precio real

    // Evitamos división por cero si la API falla y devuelve 0
    val safeDolarPrice = if (dolarPrice > 0) dolarPrice else 1150.0
    val totalUSD = totalBalanceARS / safeDolarPrice

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

                // Muestra el total convertido
                Text(text = "$ ${String.format(Locale.US, "%.2f", totalUSD)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)

                // Muestra qué cotización estamos usando
                Text(text = "USD (Blue: $${safeDolarPrice.toInt()})", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFF0984E3).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                        Text(text = "🇦🇷 $totalBalanceARS ARS", color = Color(0xFF74B9FF), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

// 3. ACTION BUTTONS
@Composable
fun ActionButtonsRow(onAddClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        ActionButton(icon = Icons.Default.QrCodeScanner, label = "Scan", color = Color(0xFF6C5CE7))
        ActionButton(icon = Icons.Default.Add, label = "Add", color = Color(0xFF6C5CE7), isMain = true, onClick = onAddClick)
        ActionButton(icon = Icons.Default.SwapHoriz, label = "Exchange", color = Color(0xFF6C5CE7))
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
// Modificamos para recibir el precio
@Composable
fun TickerRow(dolarBluePrice: Double) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {

        // AHORA USAMOS EL PRECIO REAL (Formateado sin decimales)
        item {
            TickerItem(
                name = "Dólar Blue",
                price = "$${dolarBluePrice.toInt()}",
                change = "+0%", // La API gratis no nos da el % de cambio hoy, lo dejamos fijo o lo calculamos despues
                isPositive = true
            )
        }

        // Bitcoin y USDT siguen fijos porque necesitaríamos otra API (CoinGecko o Binance) para esos datos
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

// 5. GOAL CARD
@Composable
fun GoalCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B26)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Auto para Brasil 🇧🇷", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "Meta: Enero 2027", color = Color.Gray, fontSize = 10.sp)
                }
                Text(text = "50%", color = Color(0xFF6C5CE7), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF2D3440))) {
                Box(modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE)))))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "$2,500", color = Color.White, fontSize = 12.sp)
                Text(text = "$5,000", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}