package com.example.todoapp.logic

import android.content.Context
import com.example.todoapp.R

// IDEA CLAVE DE LOS IDIOMAS:
// En la base de datos los valores se guardan SIEMPRE en español ("Alta", "Pendiente"...),
// porque la lógica (ordenar, filtrar, cambiar de estado) depende de esos textos.
// Lo que cambia con el idioma es solo lo que se MUESTRA en pantalla.
// Esta clase traduce de "valor guardado" a "texto que se ve".
object Etiquetas {
    // Valores guardados (no se traducen)
    val PRIORIDADES = listOf("Alta", "Media", "Baja")
    val CATEGORIAS = listOf("Escuela", "Casa", "Personal")
    val ESTADOS = listOf("Pendiente", "En progreso", "Completada")
    val FILTROS = listOf("Todos") + ESTADOS
    val ORDENES = listOf("Más recientes", "Prioridad", "Título A-Z")

    // Textos que se muestran (getString toma el idioma actual del celular o de la app)
    fun prioridad(ctx: Context, valor: String): String = when (valor) {
        "Alta" -> ctx.getString(R.string.prio_alta)
        "Media" -> ctx.getString(R.string.prio_media)
        "Baja" -> ctx.getString(R.string.prio_baja)
        else -> valor
    }

    fun categoria(ctx: Context, valor: String): String = when (valor) {
        "Escuela" -> ctx.getString(R.string.cat_escuela)
        "Casa" -> ctx.getString(R.string.cat_casa)
        "Personal" -> ctx.getString(R.string.cat_personal)
        else -> valor
    }

    fun estado(ctx: Context, valor: String): String = when (valor) {
        "Pendiente" -> ctx.getString(R.string.estado_pendiente)
        "En progreso" -> ctx.getString(R.string.estado_en_progreso)
        "Completada" -> ctx.getString(R.string.estado_completada)
        else -> valor
    }

    fun filtro(ctx: Context, valor: String): String =
        if (valor == "Todos") ctx.getString(R.string.filtro_todos) else estado(ctx, valor)

    fun orden(ctx: Context, valor: String): String = when (valor) {
        "Prioridad" -> ctx.getString(R.string.orden_prioridad)
        "Título A-Z" -> ctx.getString(R.string.orden_titulo)
        else -> ctx.getString(R.string.orden_recientes)
    }
}
