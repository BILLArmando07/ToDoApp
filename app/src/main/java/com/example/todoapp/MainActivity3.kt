package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.todoapp.data.TareaEntity
import com.example.todoapp.logic.Etiquetas
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.TareaRepository
import com.example.todoapp.logic.irALogin

class MainActivity3 : AppCompatActivity() {

    private lateinit var tareasRepo: TareaRepository
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var etBuscar: EditText
    private lateinit var spFiltro: Spinner
    private lateinit var spOrden: Spinner
    private lateinit var tvContador: TextView
    private var usuarioId = -1
    private var tareas: List<TareaEntity> = emptyList()
    private val textos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usuarioId = SessionManager(this).usuarioActual()
        if (usuarioId == -1) {
            irALogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        tareasRepo = TareaRepository(this)
        tvContador = findViewById(R.id.tvContador)
        etBuscar = findViewById(R.id.etBuscar)
        spFiltro = findViewById(R.id.spFiltro)
        spOrden = findViewById(R.id.spOrden)
        val lvTareas = findViewById<ListView>(R.id.lvTareas)
        val btnNueva = findViewById<Button>(R.id.btnNueva)
        val tvAjustes = findViewById<TextView>(R.id.tvAjustes)

        // Los menús MUESTRAN el texto traducido, pero para saber qué valor real eligió
        // el usuario usamos la POSICIÓN (selectedItemPosition) en las listas de Etiquetas.
        spFiltro.adapter = ArrayAdapter(
            this, R.layout.spinner_item, Etiquetas.FILTROS.map { Etiquetas.filtro(this, it) }
        )
        spOrden.adapter = ArrayAdapter(
            this, R.layout.spinner_item, Etiquetas.ORDENES.map { Etiquetas.orden(this, it) }
        )

        adapter = ArrayAdapter(this, R.layout.item_tarea, textos)
        lvTareas.adapter = adapter
        lvTareas.emptyView = findViewById(R.id.tvVacio)

        etBuscar.doAfterTextChanged { cargarTareas() }
        val recargar = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarTareas()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spFiltro.onItemSelectedListener = recargar
        spOrden.onItemSelectedListener = recargar

        lvTareas.setOnItemClickListener { _, _, position, _ ->
            mostrarOpciones(tareas[position])
        }

        btnNueva.setOnClickListener { startActivity(Intent(this, MainActivity4::class.java)) }
        tvAjustes.setOnClickListener { startActivity(Intent(this, MainActivity5::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        if (usuarioId != -1) cargarTareas()
    }

    private fun cargarTareas() {
        tareas = tareasRepo.listar(
            usuarioId,
            etBuscar.text.toString(),
            Etiquetas.FILTROS[spFiltro.selectedItemPosition], // valor real (en español) del filtro
            Etiquetas.ORDENES[spOrden.selectedItemPosition]   // valor real del orden
        )
        textos.clear()
        for (t in tareas) {
            // Lo guardado está en español; aquí se traduce solo para mostrarlo
            val cat = Etiquetas.categoria(this, t.categoria)
            val prio = Etiquetas.prioridad(this, t.prioridad)
            val est = Etiquetas.estado(this, t.estado)
            textos.add("${t.titulo}\n$cat · $prio · $est")
        }
        adapter.notifyDataSetChanged()
        // getQuantityString elige "1 tarea" o "2 tareas" (singular/plural) según el idioma
        tvContador.text = resources.getQuantityString(R.plurals.contador_tareas, tareas.size, tareas.size)
    }

    private fun mostrarOpciones(tarea: TareaEntity) {
        val opciones = arrayOf(
            getString(R.string.op_cambiar_estado),
            getString(R.string.op_editar),
            getString(R.string.op_eliminar)
        )
        AlertDialog.Builder(this)
            .setTitle(tarea.titulo)
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> {
                        val nuevo = tareasRepo.siguienteEstado(tarea.estado)
                        tareasRepo.actualizar(tarea.copy(estado = nuevo))
                        cargarTareas()
                        Toast.makeText(
                            this,
                            getString(R.string.toast_estado, Etiquetas.estado(this, nuevo)),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> {
                        val intent = Intent(this, MainActivity4::class.java)
                        intent.putExtra("tareaId", tarea.id)
                        startActivity(intent)
                    }
                    2 -> confirmarEliminar(tarea)
                }
            }
            .show()
    }

    private fun confirmarEliminar(tarea: TareaEntity) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.eliminar_titulo))
            // %1$s en strings.xml se reemplaza por el título de la tarea
            .setMessage(getString(R.string.eliminar_mensaje, tarea.titulo))
            .setPositiveButton(getString(R.string.op_eliminar)) { _, _ ->
                tareasRepo.eliminar(tarea.id, usuarioId)
                cargarTareas()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }
}
