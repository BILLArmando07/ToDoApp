package com.example.todoapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.TareaEntity

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val spPrioridad = findViewById<Spinner>(R.id.spPrioridad)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val tvCancelar = findViewById<TextView>(R.id.tvCancelar)

        val db = AppDatabase.getInstance(this)

        // Las opciones de cada menú desplegable
        val prioridades = listOf("Alta", "Media", "Baja")
        val categorias = listOf("Escuela", "Casa", "Personal")

        // Cada Spinner necesita un adapter, igual que la lista, con el diseño spinner_item
        spPrioridad.adapter = ArrayAdapter(this, R.layout.spinner_item, prioridades)
        spCategoria.adapter = ArrayAdapter(this, R.layout.spinner_item, categorias)

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()

            if (titulo.isEmpty()) {
                etTitulo.error = "Escribe un título"
                return@setOnClickListener
            }

            // selectedItem es la opción que el usuario eligió en el menú
            val prioridad = spPrioridad.selectedItem.toString()
            val categoria = spCategoria.selectedItem.toString()

            // Guardamos la tarea en la base de datos (el id se pone solo)
            db.tareaDao().insertTarea(
                TareaEntity(titulo = titulo, prioridad = prioridad, categoria = categoria)
            )
            Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show()
            finish() // cierra esta pantalla y regresa a la lista, que se actualiza sola
        }

        tvCancelar.setOnClickListener { finish() }
    }
}