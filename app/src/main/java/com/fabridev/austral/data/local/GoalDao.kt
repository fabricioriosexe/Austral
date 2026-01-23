package com.fabridev.austral.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    // Traer todos los objetivos
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    // Traer un objetivo específico (por si queremos ver el detalle)
    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: Int): Flow<GoalEntity>

    // Crear un objetivo nuevo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    // Actualizar (Ej: cuando agregás plata al objetivo)
    @Update
    suspend fun updateGoal(goal: GoalEntity)

    // Borrar (Si te arrepentís del viaje)
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
}