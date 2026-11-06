package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) // el id se asigna solo (1, 2, 3...)
    val id: Int = 0,
    val nombre: String = "",
    val correo: String = "",
    val password: String = ""
)