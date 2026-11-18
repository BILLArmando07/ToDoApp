package com.example.todoapp.logic

import android.content.Context
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.TareaEntity

class TareaRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).tareaDao()

    // La búsqueda y el filtro los hace la base de datos; el orden lo hacemos aquí
    fun listar(usuarioId: Int, texto: String, estado: String, orden: String): List<TareaEntity> {
        val lista = dao.buscar(usuarioId, texto.trim(), estado)
        return when (orden) {
            "Prioridad" -> lista.sortedBy { rango(it.prioridad) }
            "Título A-Z" -> lista.sortedBy { it.titulo.lowercase() }
            else -> lista // "Más recientes": ya vienen así de la consulta
        }
    }

    private fun rango(prioridad: String) = when (prioridad) {
        "Alta" -> 0
        "Media" -> 1
        else -> 2
    }

    fun obtener(id: Int, usuarioId: Int) = dao.buscarPorId(id, usuarioId)

    fun crear(usuarioId: Int, titulo: String, prioridad: String, categoria: String, estado: String) {
        dao.insertTarea(
            TareaEntity(
                usuarioId = usuarioId,
                titulo = titulo.trim(),
                prioridad = prioridad,
                categoria = categoria,
                estado = estado
            )
        )
    }

    fun actualizar(tarea: TareaEntity) = dao.updateTarea(tarea)

    fun eliminar(id: Int, usuarioId: Int) = dao.eliminar(id, usuarioId)

    // Pendiente -> En progreso -> Completada -> Pendiente...
    fun siguienteEstado(actual: String) = when (actual) {
        "Pendiente" -> "En progreso"
        "En progreso" -> "Completada"
        else -> "Pendiente"
    }
}