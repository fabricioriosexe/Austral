package com.fabridev.austral.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. AGREGAMOS GoalEntity A LA LISTA DE ENTIDADES
// 2. SUBIMOS LA VERSIÓN A 2 (Porque cambió la estructura)
@Database(entities = [TransactionEntity::class, GoalEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao // <--- NUEVO DAO

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
                    // ESTA LÍNEA ES CLAVE EN DESARROLLO:
                    // Si cambiamos la BD, borra todo y empieza de cero para no crashear.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}