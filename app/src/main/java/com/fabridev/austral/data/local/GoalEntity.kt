package com.fabridev.austral.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,           // Ej: "Auto para Brasil"
    val targetAmount: Double,   // Ej: 5000.0 (Lo que querés juntar)
    val savedAmount: Double,    // Ej: 2500.0 (Lo que ya tenés)
    val currencyCode: String,   // "USD", "ARS", "BRL" (Real), "EUR"
    val deadline: Long? = null, // Fecha límite (Opcional, por eso el ?)
    val isCompleted: Boolean = false
)