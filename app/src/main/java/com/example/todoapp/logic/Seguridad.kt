package com.example.todoapp.logic

import java.security.MessageDigest
import java.security.SecureRandom

// Cifra contraseñas. SHA-256 es un cifrado de una sola vía: no se puede "descifrar",
// solo comparar. El salt hace que dos contraseñas iguales den resultados distintos.
object Seguridad {
    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hash(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((salt + password).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}