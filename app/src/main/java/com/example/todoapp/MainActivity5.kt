package com.example.todoapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.irALogin

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sesion = SessionManager(this)
        val usuario = UserRepository(this).obtener(sesion.usuarioActual())
        if (usuario == null) { // sin sesión válida, volvemos al login
            sesion.cerrar()
            irALogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left + 24, bars.top + 16, bars.right + 24, bars.bottom + 16)
            insets
        }

        findViewById<TextView>(R.id.tvUsuario).text = "${usuario.nombre}\n${usuario.correo}"

        // ---- Preferencia de notificaciones (se guarda con DataStore) ----
        val swNotif = findViewById<Switch>(R.id.swNotif)
        swNotif.isChecked = sesion.notificacionesActivas()
        swNotif.setOnCheckedChangeListener { _, activo ->
            sesion.guardarNotificaciones(activo)
        }

        // ---- Selector de idioma DENTRO de la app ----
        val spIdioma = findViewById<Spinner>(R.id.spIdioma)
        val codigos = listOf("es", "en") // códigos de idioma: español e inglés
        spIdioma.adapter = ArrayAdapter(
            this, R.layout.spinner_item,
            listOf(getString(R.string.idioma_es), getString(R.string.idioma_en))
        )

        // Idioma que usa la app ahorita, para mostrarlo seleccionado
        val idiomaActual = resources.configuration.locales[0].language
        // El "false" evita que se dispare el cambio solo por abrir la pantalla
        spIdioma.setSelection(codigos.indexOf(idiomaActual).coerceAtLeast(0), false)

        spIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (codigos[position] != idiomaActual) {
                    // Cambia el idioma de TODA la app. Android recrea las pantallas solo
                    // y se acuerda de la elección aunque cierres la app.
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(codigos[position])
                    )
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            sesion.cerrar()
            irALogin()
        }
    }
}
