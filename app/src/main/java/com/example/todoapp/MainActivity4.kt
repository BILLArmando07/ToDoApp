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
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.TareaRepository
import com.example.todoapp.logic.Validador
import com.example.todoapp.logic.irALogin

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usuarioId = SessionManager(this).usuarioActual()
        if (usuarioId == -1) {
            irALogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        val tvTituloPantalla = findViewById<TextView>(R.id.tvTituloPantalla)
        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val spPrioridad = findViewById<Spinner>(R.id.spPrioridad)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val spEstado = findViewById<Spinner>(R.id.spEstado)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val tvCancelar = findViewById<TextView>(R.id.tvCancelar)

        val repo = TareaRepository(this)

        val prioridades = listOf("Alta", "Media", "Baja")
        val categorias = listOf("Escuela", "Casa", "Personal")
        val estados = listOf("Pendiente", "En progreso", "Completada")
        spPrioridad.adapter = ArrayAdapter(this, R.layout.spinner_item, prioridades)
        spCategoria.adapter = ArrayAdapter(this, R.layout.spinner_item, categorias)
        spEstado.adapter = ArrayAdapter(this, R.layout.spinner_item, estados)

        // Si llegó un "tareaId", esta pantalla funciona en modo EDITAR; si no, en modo CREAR.
        // Buscamos la tarea pasando también usuarioId, así nadie edita tareas ajenas.
        val tareaId = intent.getIntExtra("tareaId", -1)
        val tareaEditar = if (tareaId != -1) repo.obtener(tareaId, usuarioId) else null

        if (tareaId != -1 && tareaEditar == null) {
            Toast.makeText(this, "No se encontró la tarea", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (tareaEditar != null) {
            // Rellenamos el formulario con los datos actuales de la tarea
            tvTituloPantalla.text = "Editar tarea"
            btnGuardar.text = "Guardar cambios"
            etTitulo.setText(tareaEditar.titulo)
            spPrioridad.setSelection(prioridades.indexOf(tareaEditar.prioridad))
            spCategoria.setSelection(categorias.indexOf(tareaEditar.categoria))
            spEstado.setSelection(estados.indexOf(tareaEditar.estado))
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val error = Validador.validarTarea(titulo)
            if (error != null) {
                etTitulo.error = error
                return@setOnClickListener
            }

            val prioridad = spPrioridad.selectedItem.toString()
            val categoria = spCategoria.selectedItem.toString()
            val estado = spEstado.selectedItem.toString()

            if (tareaEditar == null) {
                repo.crear(usuarioId, titulo, prioridad, categoria, estado)
                Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show()
            } else {
                // copy() hace una copia de la tarea cambiando solo lo indicado
                repo.actualizar(
                    tareaEditar.copy(
                        titulo = titulo.trim(),
                        prioridad = prioridad,
                        categoria = categoria,
                        estado = estado
                    )
                )
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        tvCancelar.setOnClickListener { finish() }
    }
}