package com.example.todoapp.logic

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Se declara UNA sola vez, fuera de la clase. Crea el archivo "ajustes" donde se guarda todo.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

class SessionManager(context: Context) {
    private val store = context.applicationContext.dataStore

    // Las "llaves" son los nombres con los que se guarda cada dato
    companion object {
        private val KEY_USER_ID = intPreferencesKey("userId")
        private val KEY_NOTIF = booleanPreferencesKey("notificaciones")
    }

    // ---- Sesión ----
    fun iniciar(userId: Int) {
        // edit { } cambia los datos guardados; runBlocking espera a que termine
        runBlocking { store.edit { it[KEY_USER_ID] = userId } }
    }

    // store.data es un Flow (datos que pueden cambiar); first() toma el valor actual.
    // Si no hay nada guardado devuelve -1, o sea, nadie ha iniciado sesión.
    fun usuarioActual(): Int = runBlocking { store.data.first()[KEY_USER_ID] ?: -1 }

    fun haySesion(): Boolean = usuarioActual() != -1

    fun cerrar() {
        runBlocking { store.edit { it.remove(KEY_USER_ID) } }
    }

    // ---- Preferencias ----
    // Si nunca se ha guardado la preferencia, el valor por defecto es true (activadas)
    fun notificacionesActivas(): Boolean = runBlocking { store.data.first()[KEY_NOTIF] ?: true }

    fun guardarNotificaciones(activas: Boolean) {
        runBlocking { store.edit { it[KEY_NOTIF] = activas } }
    }
}