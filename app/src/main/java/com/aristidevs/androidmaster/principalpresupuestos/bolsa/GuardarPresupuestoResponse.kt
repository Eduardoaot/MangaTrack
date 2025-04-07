package com.aristidevs.androidmaster.principalpresupuestos.bolsa

data class GuardarPresupuestoResponse(
    val success: Boolean,      // Indica si la creación fue exitosa
    val mensaje: String = "Presupuesto y mangas guardados correctamente" // Mensaje por defecto
)
