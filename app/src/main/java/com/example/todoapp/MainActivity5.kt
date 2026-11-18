package com.example.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.irALogin
import android.widget.Switch

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sesion = SessionManager(this)
        val usuario = UserRepository(this).obtener(sesion.usuarioActual())
        if (usuario == null) { // sin sesión válida, volvemos al login
            sesion.cerrar()
            irALogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        findViewById<TextView>(R.id.tvUsuario).text = "${usuario.nombre}\n${usuario.correo}"
        // Al abrir la pantalla, el interruptor muestra lo que se guardó la última vez
        val swNotif = findViewById<Switch>(R.id.swNotif)
        swNotif.isChecked = sesion.notificacionesActivas()

// Cada vez que lo mueves, se guarda la nueva preferencia
        swNotif.setOnCheckedChangeListener { _, activo ->
            sesion.guardarNotificaciones(activo)
        }

        findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            sesion.cerrar() // borra la sesión guardada
            irALogin()
        }
    }
}