package com.example.todoapp.logic

import android.content.Context
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.UserEntity

// Guardamos el contexto para poder traducir el mensaje de "correo repetido"
class UserRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).userDao()

    // Devuelve null si se registró bien, o el mensaje de error
    fun registrar(nombre: String, correo: String, password: String): String? {
        val correoLimpio = correo.trim().lowercase()
        if (dao.buscarPorCorreo(correoLimpio) != null) return context.getString(R.string.err_correo_repetido)
        val salt = Seguridad.generarSalt()
        dao.insertUser(
            UserEntity(
                nombre = nombre.trim(),
                correo = correoLimpio,
                passwordHash = Seguridad.hash(password, salt),
                salt = salt
            )
        )
        return null
    }

    fun login(correo: String, password: String): UserEntity? {
        val user = dao.buscarPorCorreo(correo.trim().lowercase()) ?: return null
        return if (Seguridad.hash(password, user.salt) == user.passwordHash) user else null
    }

    fun obtener(id: Int): UserEntity? = dao.buscarPorId(id)
}
