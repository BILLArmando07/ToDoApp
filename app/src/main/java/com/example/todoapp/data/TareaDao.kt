package com.example.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY id DESC") // las más nuevas primero
    fun getAllTareas(): List<TareaEntity>

    @Insert
    fun insertTarea(tarea: TareaEntity)

    @Delete
    fun deleteTarea(tarea: TareaEntity)
}