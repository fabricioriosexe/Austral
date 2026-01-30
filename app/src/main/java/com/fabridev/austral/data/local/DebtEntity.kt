package com.fabridev.austral.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,           // Ej: "Visa", "Préstamo Juan"
    val totalAmount: Double,    // Ej: 150000.0 (Deuda original)
    val remainingAmount: Double // Ej: 50000.0 (Lo que falta pagar)
)