package com.aristidevs.androidmaster.inicioyregistro

data class RegistroUsuarioRequest(
    val name: String,
    val email: String,
    val user: String,
    val password: String,
    val meta: Int
)
