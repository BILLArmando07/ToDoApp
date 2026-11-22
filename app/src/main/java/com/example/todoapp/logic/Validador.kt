package com.example.todoapp.logic

import android.content.Context
import android.util.Patterns
import com.example.todoapp.R

// Ahora cada validación recibe el "contexto" (la pantalla) para poder pedir
// el mensaje de error en el idioma correcto con getString.
object Validador {
    fun correoValido(correo: String) = Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    fun validarRegistro(ctx: Context, nombre: String, correo: String, password: String, confirmar: String): String? {
        if (nombre.trim().length < 2) return ctx.getString(R.string.err_nombre)
        if (!correoValido(correo)) return ctx.getString(R.string.err_correo_invalido)
        if (password.length < 6) return ctx.getString(R.string.err_password_corta)
        if (password != confirmar) return ctx.getString(R.string.err_password_no_coincide)
        return null
    }

    fun validarTarea(ctx: Context, titulo: String): String? {
        if (titulo.trim().length < 3) return ctx.getString(R.string.err_titulo_corto)
        if (titulo.trim().length > 60) return ctx.getString(R.string.err_titulo_largo)
        return null
    }
}
