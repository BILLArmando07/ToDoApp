package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// unique = true: la base de datos NO deja guardar dos usuarios con el mismo correo
@Entity(tableName = "users", indices = [Index(value = ["correo"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    val passwordHash: String = "", // la contraseña cifrada, nunca en texto normal
    val salt: String = ""          // texto aleatorio que se mezcla con la contraseña antes de cifrarla
)