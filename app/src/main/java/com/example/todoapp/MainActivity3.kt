package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.TareaEntity

class MainActivity3 : AppCompatActivity() {

    // Las declaramos aquí arriba (fuera de onCreate) para usarlas en varias funciones
    private lateinit var db: AppDatabase
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var tvContador: TextView
    private var tareas: List<TareaEntity> = emptyList() // las tareas reales de la BD
    private val textos = mutableListOf<String>()        // lo que se ve en cada tarjeta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        db = AppDatabase.getInstance(this)
        tvContador = findViewById(R.id.tvContador)
        val lvTareas = findViewById<ListView>(R.id.lvTareas)
        val btnNueva = findViewById<Button>(R.id.btnNueva)
        val tvAjustes = findViewById<TextView>(R.id.tvAjustes)

        // El adapter conecta la lista "textos" con el diseño item_tarea.xml
        adapter = ArrayAdapter(this, R.layout.item_tarea, textos)
        lvTareas.adapter = adapter

        // Mantener presionada una tarea la borra
        lvTareas.setOnItemLongClickListener { _, _, position, _ ->
            db.tareaDao().deleteTarea(tareas[position]) // borra la tarea de esa posición
            cargarTareas()                              // y refrescamos la lista
            Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
            true // true = "ya manejé este clic largo"
        }

        // Los botones abren otras pantallas con Intent
        btnNueva.setOnClickListener { startActivity(Intent(this, MainActivity4::class.java)) }
        tvAjustes.setOnClickListener { startActivity(Intent(this, MainActivity5::class.java)) }
    }

    // onResume se ejecuta cada vez que esta pantalla vuelve a verse.
    // Por eso, al guardar una tarea en MainActivity4 y regresar, la lista se actualiza sola.
    override fun onResume() {
        super.onResume()
        cargarTareas()
    }

    // Lee las tareas de la base de datos y las muestra
    private fun cargarTareas() {
        tareas = db.tareaDao().getAllTareas()
        textos.clear()
        for (t in tareas) {
            // "\n" es un salto de línea: título arriba, categoría y prioridad abajo
            textos.add("${t.titulo}\n${t.categoria} · ${t.prioridad}")
        }
        adapter.notifyDataSetChanged() // avisamos que cambió para que se redibuje
        tvContador.text = "${tareas.size} tareas pendientes"
    }
}