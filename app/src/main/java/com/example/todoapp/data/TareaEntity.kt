package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String = "",
    val prioridad: String = "",   // "Alta", "Media" o "Baja"
    val categoria: String = ""    // "Escuela", "Casa" o "Personal"
)