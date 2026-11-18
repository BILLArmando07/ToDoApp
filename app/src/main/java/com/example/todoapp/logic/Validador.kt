package com.example.todoapp.logic

import android.util.Patterns

// Todas las validaciones en un solo lugar para reutilizarlas en cualquier pantalla.
// Cada función devuelve el mensaje de error, o null si todo está bien.
object Validador {
    fun correoValido(correo: String) = Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    fun validarRegistro(nombre: String, correo: String, password: String, confirmar: String): String? {
        if (nombre.trim().length < 2) return "Escribe tu nombre"
        if (!correoValido(correo)) return "El correo no es válido"
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (password != confirmar) return "Las contraseñas no coinciden"
        return null
    }

    fun validarTarea(titulo: String): String? {
        if (titulo.trim().length < 3) return "El título debe tener al menos 3 caracteres"
        if (titulo.trim().length > 60) return "El título es muy largo (máximo 60)"
        return null
    }
}