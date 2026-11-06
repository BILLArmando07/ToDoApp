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
import com.example.todoapp.data.AppDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // usa el XML de arriba
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + 24, systemBars.top, systemBars.right + 24, systemBars.bottom)
            insets
        }

        // Conectamos los elementos del XML con variables
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvIrRegistro = findViewById<TextView>(R.id.tvIrRegistro)

        // Abrimos la base de datos (la que hicimos en el paso 4)
        val db = AppDatabase.getInstance(this)

        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString()

            // Si falta algo, avisamos con un Toast (mensajito abajo) y salimos
            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buscamos en la base de datos un usuario con ese correo y contraseña
            val usuario = db.userDao().login(correo, password)

            if (usuario == null) {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            } else {
                // Intent = "ábreme otra pantalla". MainActivity3 será la lista de tareas.
                startActivity(Intent(this, MainActivity3::class.java))
                finish() // cierra el login para que "atrás" no regrese aquí
            }
        }

        // Al tocar "Regístrate" abrimos la pantalla de registro (MainActivity2)
        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }
}