package com.fabridev.austral.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,           // El monto (ej: 1500.0)
    val currencyCode: String,     // "ARS", "USD", "USDT"
    val description: String,      // Ej: "Supermercado"
    val isExpense: Boolean,       // true = Gasto, false = Ingreso
    val date: Long = System.currentTimeMillis() // Fecha automática en milisegundos
)