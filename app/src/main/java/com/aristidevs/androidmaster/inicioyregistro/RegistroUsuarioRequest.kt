package com.aristidevs.androidmaster.inicioyregistro

data class RegistroUsuarioRequest(
    val email: String,
    val password: String,
    val user: String,
    val name: String
)
