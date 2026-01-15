package com.fabridev.austral.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Insertar un movimiento (suspend = se hace en segundo plano para no trabar el celular)
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Borrar un movimiento
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Traer todos los movimientos ordenados por el más nuevo
    // Usamos 'Flow' para que si agregás un gasto, la pantalla se actualice sola en vivo
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Calcular Balance Total (Ingresos - Gastos)
    // Esto es SQL puro optimizado. Devuelve null si no hay datos.
    @Query("SELECT SUM(CASE WHEN isExpense = 0 THEN amount ELSE -amount END) FROM transactions")
    fun getTotalBalance(): Flow<Double?>
}