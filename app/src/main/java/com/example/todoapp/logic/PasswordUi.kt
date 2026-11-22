package com.example.todoapp.logic

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.InputType
import android.view.MotionEvent
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.todoapp.R

// Una "extensión": agrega una función nueva a EditText.
// Se usa así:  etPassword.activarOjito()
// Pone un ojito al final del campo; al tocarlo alterna entre ocultar y mostrar la contraseña.
@SuppressLint("ClickableViewAccessibility")
fun EditText.activarOjito() {
    var visible = false

    fun actualizar() {
        // Cambia el tipo de texto: contraseña oculta (puntitos) o visible
        inputType = InputType.TYPE_CLASS_TEXT or
            (if (visible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_TEXT_VARIATION_PASSWORD)
        typeface = Typeface.DEFAULT // al cambiar el tipo Android cambia la letra; la regresamos
        // Ojo abierto cuando se ve la contraseña, ojo tachado cuando está oculta
        val icono = ContextCompat.getDrawable(
            context,
            if (visible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
        )
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icono, null)
        setSelection(text.length) // el cursor se queda al final
    }

    actualizar()

    setOnTouchListener { v, event ->
        // Si el dedo se levanta sobre la zona del ojito (el final del campo), alternamos
        if (event.action == MotionEvent.ACTION_UP && event.x >= width - totalPaddingEnd) {
            visible = !visible
            actualizar()
            v.performClick()
            true
        } else {
            false
        }
    }
}
