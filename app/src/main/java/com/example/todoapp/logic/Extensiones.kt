package com.example.todoapp.logic

import android.app.Activity
import android.content.Intent
import com.example.todoapp.MainActivity

// Una función que cualquier pantalla puede usar para volver al login
// y borrar las pantallas anteriores (así "atrás" no regresa a la app).
fun Activity.irALogin() {
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}