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
import com.example.todoapp.logic.Etiquetas
import com.example.todoapp.logic.NotificacionesHelper
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

        // Se muestran traducidos, pero lo que se guarda es el valor real (en español)
        spPrioridad.adapter = ArrayAdapter(
            this, R.layout.spinner_item, Etiquetas.PRIORIDADES.map { Etiquetas.prioridad(this, it) }
        )
        spCategoria.adapter = ArrayAdapter(
            this, R.layout.spinner_item, Etiquetas.CATEGORIAS.map { Etiquetas.categoria(this, it) }
        )
        spEstado.adapter = ArrayAdapter(
            this, R.layout.spinner_item, Etiquetas.ESTADOS.map { Etiquetas.estado(this, it) }
        )

        val tareaId = intent.getIntExtra("tareaId", -1)
        val tareaEditar = if (tareaId != -1) repo.obtener(tareaId, usuarioId) else null

        if (tareaId != -1 && tareaEditar == null) {
            Toast.makeText(this, getString(R.string.msg_tarea_no_encontrada), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (tareaEditar != null) {
            tvTituloPantalla.text = getString(R.string.editar_tarea_titulo)
            btnGuardar.text = getString(R.string.btn_guardar_cambios)
            etTitulo.setText(tareaEditar.titulo)
            spPrioridad.setSelection(Etiquetas.PRIORIDADES.indexOf(tareaEditar.prioridad))
            spCategoria.setSelection(Etiquetas.CATEGORIAS.indexOf(tareaEditar.categoria))
            spEstado.setSelection(Etiquetas.ESTADOS.indexOf(tareaEditar.estado))
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val error = Validador.validarTarea(this, titulo)
            if (error != null) {
                etTitulo.error = error
                return@setOnClickListener
            }

            // Con la posición del menú sacamos el valor real que se guarda en la base de datos
            val prioridad = Etiquetas.PRIORIDADES[spPrioridad.selectedItemPosition]
            val categoria = Etiquetas.CATEGORIAS[spCategoria.selectedItemPosition]
            val estado = Etiquetas.ESTADOS[spEstado.selectedItemPosition]

            if (tareaEditar == null) {
                repo.crear(usuarioId, titulo, prioridad, categoria, estado)
                Toast.makeText(this, getString(R.string.msg_tarea_guardada), Toast.LENGTH_SHORT).show()
                NotificacionesHelper.mostrarNotificacion(
                    this,
                    titulo.trim(),
                    "${Etiquetas.categoria(this, categoria)} · ${Etiquetas.prioridad(this, prioridad)}"
                )
            } else {
                repo.actualizar(
                    tareaEditar.copy(
                        titulo = titulo.trim(),
                        prioridad = prioridad,
                        categoria = categoria,
                        estado = estado
                    )
                )
                Toast.makeText(this, getString(R.string.msg_cambios_guardados), Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        tvCancelar.setOnClickListener { finish() }
    }
}
