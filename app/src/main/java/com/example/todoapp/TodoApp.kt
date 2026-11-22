package com.example.todoapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.todoapp.logic.SessionManager

// Esta clase se ejecuta UNA vez cuando arranca la app, antes de dibujar cualquier pantalla.
// Aquí se aplica el modo (oscuro o claro) que el usuario dejó guardado.
class TodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val oscuro = SessionManager(this).modoOscuro()
        AppCompatDelegate.setDefaultNightMode(
            if (oscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
