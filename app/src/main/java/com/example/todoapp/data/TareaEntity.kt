package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ForeignKey: cada tarea pertenece a un usuario. Si se borra el usuario,
// sus tareas se borran solas (CASCADE). Eso evita tareas "huérfanas".
@Entity(
    tableName = "tareas",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("usuarioId")] // el índice hace más rápidas las búsquedas por usuario
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int = 0,
    val titulo: String = "",
    val prioridad: String = "Media",
    val categoria: String = "Escuela",
    val estado: String = "Pendiente" // Pendiente, En progreso o Completada
)