package com.aristidevs.androidmaster.network  // Puedes ponerla en un paquete de tu elección

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.69:8080/"  // Tu URL base

    // Función para obtener una instancia de Retrofit
    fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
