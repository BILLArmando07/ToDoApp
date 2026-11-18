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
    private var tareas: List<TareaEntity> = emptyList() // las tareas que se ven ahorita
    private val textos = mutableListOf<String>()        // el texto de cada tarjeta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seguridad: si no hay sesión, regresamos al login
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

        // Opciones de los menús de filtro y orden
        spFiltro.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            listOf("Todos", "Pendiente", "En progreso", "Completada")
        )
        spOrden.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            listOf("Más recientes", "Prioridad", "Título A-Z")
        )

        adapter = ArrayAdapter(this, R.layout.item_tarea, textos)
        lvTareas.adapter = adapter
        lvTareas.emptyView = findViewById(R.id.tvVacio) // si la lista está vacía, muestra este mensaje

        // Recargar la lista cuando cambia la búsqueda, el filtro o el orden
        etBuscar.doAfterTextChanged { cargarTareas() }
        val recargar = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarTareas()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spFiltro.onItemSelectedListener = recargar
        spOrden.onItemSelectedListener = recargar

        // Tocar una tarea abre un menú con las acciones posibles
        lvTareas.setOnItemClickListener { _, _, position, _ ->
            mostrarOpciones(tareas[position])
        }

        btnNueva.setOnClickListener { startActivity(Intent(this, MainActivity4::class.java)) }
        tvAjustes.setOnClickListener { startActivity(Intent(this, MainActivity5::class.java)) }
    }

    // Se ejecuta cada vez que la pantalla vuelve a verse (por ejemplo, después de guardar una tarea)
    override fun onResume() {
        super.onResume()
        if (usuarioId != -1) cargarTareas()
    }

    private fun cargarTareas() {
        tareas = tareasRepo.listar(
            usuarioId,
            etBuscar.text.toString(),
            spFiltro.selectedItem.toString(),
            spOrden.selectedItem.toString()
        )
        textos.clear()
        for (t in tareas) {
            textos.add("${t.titulo}\n${t.categoria} · ${t.prioridad} · ${t.estado}")
        }
        adapter.notifyDataSetChanged()
        tvContador.text = "${tareas.size} tareas"
    }

    private fun mostrarOpciones(tarea: TareaEntity) {
        val opciones = arrayOf("Cambiar estado", "Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle(tarea.titulo)
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> { // avanza al siguiente estado
                        val nuevo = tareasRepo.siguienteEstado(tarea.estado)
                        tareasRepo.actualizar(tarea.copy(estado = nuevo))
                        cargarTareas()
                        Toast.makeText(this, "Estado: $nuevo", Toast.LENGTH_SHORT).show()
                    }
                    1 -> { // abre la pantalla de crear, pero en modo edición
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
            .setTitle("Eliminar tarea")
            .setMessage("¿Seguro que quieres eliminar \"${tarea.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                tareasRepo.eliminar(tarea.id, usuarioId)
                cargarTareas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}