package com.fabridev.austral.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Si agregás más tablas (como 'Goals'), las ponés en entities = [...]
@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        // Singleton: Asegura que solo haya una instancia de la BD abierta a la vez
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "austral_database" // El nombre del archivo en el celular
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}