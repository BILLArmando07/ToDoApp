package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.Validador
import com.example.todoapp.logic.activarOjito

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sesion = SessionManager(this)
        val usuarios = UserRepository(this)

        // Si ya había una sesión guardada (y ese usuario existe), saltamos al listado
        if (sesion.haySesion()) {
            if (usuarios.obtener(sesion.usuarioActual()) != null) {
                startActivity(Intent(this, MainActivity3::class.java))
                finish()
                return
            } else {
                sesion.cerrar()
            }
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top, bars.right + 24, bars.bottom)
            insets
        }

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvIrRegistro = findViewById<TextView>(R.id.tvIrRegistro)

        // El ojito para ver/ocultar la contraseña
        etPassword.activarOjito()

        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString()

            if (!Validador.correoValido(correo)) {
                etCorreo.error = getString(R.string.err_correo_login)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = getString(R.string.err_password_vacia)
                return@setOnClickListener
            }

            val usuario = usuarios.login(correo, password)
            if (usuario == null) {
                Toast.makeText(this, getString(R.string.err_login), Toast.LENGTH_SHORT).show()
            } else {
                sesion.iniciar(usuario.id)
                startActivity(Intent(this, MainActivity3::class.java))
                finish()
            }
        }

        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }
}
