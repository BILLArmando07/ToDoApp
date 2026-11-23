package com.example.todoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.logic.NotificacionesHelper
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.irALogin

class MainActivity5 : AppCompatActivity() {

    private val pedirPermisoNotif = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            NotificacionesHelper.mostrarNotificacion(
                this,
                getString(R.string.notif_titulo),
                getString(R.string.notif_texto)
            )
        } else {
            Toast.makeText(this, getString(R.string.notif_permiso_negado), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sesion = SessionManager(this)
        val usuario = UserRepository(this).obtener(sesion.usuarioActual())
        if (usuario == null) {
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

        NotificacionesHelper.crearCanal(this)

        findViewById<TextView>(R.id.tvUsuario).text = "${usuario.nombre}\n${usuario.correo}"

        // ---- Notificaciones ----
        val swNotif = findViewById<Switch>(R.id.swNotif)
        swNotif.isChecked = sesion.notificacionesActivas()

        swNotif.setOnCheckedChangeListener { _, activo ->
            sesion.guardarNotificaciones(activo)
            if (activo) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    pedirPermisoNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    NotificacionesHelper.mostrarNotificacion(
                        this,
                        getString(R.string.notif_titulo),
                        getString(R.string.notif_texto)
                    )
                }
            }
        }

        // ---- Modo Claro / Oscuro ----
        val btnCambiarTema = findViewById<Button>(R.id.btnCambiarTema)
        var modoOscuroActual = sesion.modoOscuro()
        btnCambiarTema.text = getString(if (modoOscuroActual) R.string.modo_claro else R.string.modo_oscuro)

        btnCambiarTema.setOnClickListener {
            modoOscuroActual = !modoOscuroActual
            sesion.guardarModoOscuro(modoOscuroActual)
            AppCompatDelegate.setDefaultNightMode(
                if (modoOscuroActual) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            btnCambiarTema.text = getString(if (modoOscuroActual) R.string.modo_claro else R.string.modo_oscuro)
        }

        // ---- Idioma ----
        val spIdioma = findViewById<Spinner>(R.id.spIdioma)
        val codigos = listOf("es", "en")
        spIdioma.adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            listOf(getString(R.string.idioma_es), getString(R.string.idioma_en))
        )

        val localeActual = AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: resources.configuration.locales[0].language
        val indiceActual = codigos.indexOf(localeActual).coerceAtLeast(0)
        spIdioma.setSelection(indiceActual, false)

        spIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val codigoSeleccionado = codigos[position]
                if (codigoSeleccionado != localeActual) {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(codigoSeleccionado)
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
