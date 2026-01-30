package com.fabridev.austral.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. AGREGAMOS DebtEntity Y SUBIMOS VERSIÓN A 3
@Database(entities = [TransactionEntity::class, GoalEntity::class, DebtEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun debtDao(): DebtDao // <--- NUEVO DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "austral_database"
                )
                    .fallbackToDestructiveMigration() // Borrará datos viejos al migrar
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}