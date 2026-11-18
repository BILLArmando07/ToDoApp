package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    fun buscarPorCorreo(correo: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun buscarPorId(id: Int): UserEntity?
}