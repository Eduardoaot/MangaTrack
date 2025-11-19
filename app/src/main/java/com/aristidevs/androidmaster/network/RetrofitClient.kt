package com.aristidevs.androidmaster.network  // Puedes ponerla en un paquete de tu elección

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://flutiest-concetta-continently.ngrok-free.dev/"  // Tu URL base
 //https://flutiest-concetta-continently.ngrok-free.dev/
    //http://192.168.1.69:8080/
    // Función para obtener una instancia de Retrofitz
    fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
