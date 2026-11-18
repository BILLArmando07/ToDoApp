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
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.Validador

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top, bars.right + 24, bars.bottom)
            insets
        }

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmar = findViewById<EditText>(R.id.etConfirmar)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tvIrLogin = findViewById<TextView>(R.id.tvIrLogin)

        val usuarios = UserRepository(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()
            val confirmar = etConfirmar.text.toString()

            // 1) Validamos el formato de los datos
            val errorFormato = Validador.validarRegistro(nombre, correo, password, confirmar)
            if (errorFormato != null) {
                Toast.makeText(this, errorFormato, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2) Intentamos registrar (puede fallar si el correo ya existe)
            val errorRegistro = usuarios.registrar(nombre, correo, password)
            if (errorRegistro != null) {
                Toast.makeText(this, errorRegistro, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cuenta creada, ya puedes iniciar sesión", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        tvIrLogin.setOnClickListener { finish() }
    }
}