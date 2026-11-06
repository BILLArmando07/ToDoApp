package com.example.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.UserEntity

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 24, systemBars.top, systemBars.right + 24, systemBars.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmar = findViewById<EditText>(R.id.etConfirmar)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tvIrLogin = findViewById<TextView>(R.id.tvIrLogin)

        val db = AppDatabase.getInstance(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmar = etConfirmar.text.toString()

            // Validaciones: cada una avisa y sale si algo está mal
            if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Si ya existe un usuario con ese correo, no lo registramos otra vez
            if (db.userDao().buscarPorCorreo(correo) != null) {
                Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Todo bien: guardamos el usuario en la base de datos
            db.userDao().insertUser(UserEntity(nombre = nombre, correo = correo, password = password))
            Toast.makeText(this, "Cuenta creada, ya puedes iniciar sesión", Toast.LENGTH_SHORT).show()
            finish() // cierra esta pantalla y regresa al login
        }

        // "Inicia sesión" solo cierra el registro y regresa al login
        tvIrLogin.setOnClickListener { finish() }
    }
}