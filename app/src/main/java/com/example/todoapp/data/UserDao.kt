package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: UserEntity) // guarda un usuario nuevo

    // Busca por correo. Devuelve null si no existe (por eso lleva el "?")
    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    fun buscarPorCorreo(correo: String): UserEntity?

    // Para el login: busca un usuario con ese correo Y esa contraseña
    @Query("SELECT * FROM users WHERE correo = :correo AND password = :password LIMIT 1")
    fun login(correo: String, password: String): UserEntity?
}