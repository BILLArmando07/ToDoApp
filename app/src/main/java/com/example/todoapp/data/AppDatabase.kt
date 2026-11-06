package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Aquí listamos las tablas (entities) que tiene la base de datos
@Database(entities = [UserEntity::class, TareaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Estas funciones entregan los DAOs (el profe no las tenía y sin ellas no sirve)
    abstract fun userDao(): UserDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Siempre devuelve LA MISMA base de datos (solo se crea una vez)
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    // Permite consultar desde la pantalla principal. No es lo ideal
                    // en apps grandes, pero para esta tarea lo hace más simple.
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}