package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TareaDao {
    @Insert
    fun insertTarea(tarea: TareaEntity)

    @Update
    fun updateTarea(tarea: TareaEntity)

    // Todas las consultas piden usuarioId: así un usuario NUNCA toca tareas de otro
    @Query("DELETE FROM tareas WHERE id = :id AND usuarioId = :usuarioId")
    fun eliminar(id: Int, usuarioId: Int)

    @Query("SELECT * FROM tareas WHERE id = :id AND usuarioId = :usuarioId LIMIT 1")
    fun buscarPorId(id: Int, usuarioId: Int): TareaEntity?

    // Búsqueda por texto + filtro por estado, todo resuelto en SQL.
    // Si estado = 'Todos', no se filtra por estado.
    @Query("SELECT * FROM tareas WHERE usuarioId = :usuarioId AND titulo LIKE '%' || :texto || '%' AND (:estado = 'Todos' OR estado = :estado) ORDER BY id DESC")
    fun buscar(usuarioId: Int, texto: String, estado: String): List<TareaEntity>
}