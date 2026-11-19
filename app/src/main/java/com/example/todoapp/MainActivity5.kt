package com.example.todoapp
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.logic.SessionManager
import com.example.todoapp.logic.UserRepository
import com.example.todoapp.logic.irALogin

class MainActivity5 : AppCompatActivity() {

    private val canalId = "canal_tareas"

    // Pide permiso para mostrar avisos en Android 13+
    private val pedirPermisoNotif = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mostrarNotificacionPrueba()
        } else {
            Toast.makeText(this, "Activa el permiso de notificaciones en ajustes", Toast.LENGTH_SHORT).show()
        }
    }

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

        crearCanalNotificacion()

        findViewById<TextView>(R.id.tvUsuario).text = "${usuario.nombre}\n${usuario.correo}"

        // Al abrir la pantalla, el interruptor muestra lo que se guardó la última vez
        val swNotif = findViewById<Switch>(R.id.swNotif)
        swNotif.isChecked = sesion.notificacionesActivas()

        // Cada vez que lo mueves, se guarda la nueva preferencia y manda aviso si se prende
        swNotif.setOnCheckedChangeListener { _, activo ->
            sesion.guardarNotificaciones(activo)
            if (activo) {
                revisarPermisoYMostrar()
            }
        }

        findViewById<Button>(R.id.btnCerrar).setOnClickListener {
            sesion.cerrar() // borra la sesión guardada
            irALogin()
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                canalId,
                "Recordatorios de tareas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos de tus tareas pendientes"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    private fun revisarPermisoYMostrar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (tienePermiso) {
                mostrarNotificacionPrueba()
            } else {
                pedirPermisoNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            mostrarNotificacionPrueba()
        }
    }

    @SuppressLint("MissingPermission")
    private fun mostrarNotificacionPrueba() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificacion = NotificationCompat.Builder(this, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Notificaciones activadas")
            .setContentText("Ya te avisaremos cuando tengas tareas pendientes.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1001, notificacion)
    }
}