package com.fabridev.austral.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val isExpense: Boolean,
    val date: Long = System.currentTimeMillis(),
    val currencyCode: String = "ARS",
    val category: String = "OTROS" // <--- CAMPO NUEVO (Por defecto es "OTROS")
)