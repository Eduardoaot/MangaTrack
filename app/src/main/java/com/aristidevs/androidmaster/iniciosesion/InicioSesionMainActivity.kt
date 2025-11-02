package com.aristidevs.androidmaster.iniciosesion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aristidevs.androidmaster.MenuActivity
import com.aristidevs.androidmaster.databinding.ActivityInicioSesionMainBinding
import com.aristidevs.androidmaster.inicioyregistro.InicioSesionActivity

class InicioSesionMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenSplash.setKeepOnScreenCondition { false }


        // Verificamos si hay sesión activa al iniciar la actividad
        if (verificarSesion()) {
            val userId = obtenerUserId()

            // Verificar si el userId no es nulo y es un valor válido (no -1)
            if (userId != null && userId != -1) {
                // Si el userId es válido, redirigir al menú
                navigateToMenu(userId)
            } else {
                // Si el userId es nulo o -1, cerrar sesión y redirigir a la pantalla de inicio
                cerrarSesion()
            }
        } else {
            // Si no hay sesión activa, mostrar las opciones de iniciar sesión o registrarse
            binding.btnIniciarSesion.setOnClickListener {
                navigateToInicioSesion()  // Navegar a la actividad de inicio de sesión
            }

            binding.btnRegistro.setOnClickListener {
                navigateToRegistro()  // Navegar a la actividad de registro
            }
        }
    }


    private fun verificarSesion(): Boolean {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val sesionIniciada = sharedPreferences.getBoolean("sesionIniciada", false)

        // Log para verificar si la sesión está activa
        Log.d("SesionStatus", "Sesión iniciada: $sesionIniciada")

        return sesionIniciada
    }

    private fun obtenerUserId(): Int? {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        // Log para verificar el userId obtenido
        Log.d("UserId", "User ID: $userId")

        return userId.takeIf { it != -1 }
    }

    private fun cerrarSesion() {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Eliminar los datos de sesión
        editor.remove("sesionIniciada")  // Eliminar la bandera de sesión activa
        editor.remove("userId")  // Eliminar el userId
        editor.apply()  // Aplicar los cambios

        // Log para confirmar que la sesión fue cerrada
        Log.d("SesionStatus", "Sesión cerrada y datos eliminados.")

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, InicioSesionMainActivity::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad actual para que no puedan volver
    }


    private fun navigateToMenu(userId: Int) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()  // Finaliza la actividad actual
    }

    private fun navigateToInicioSesion() {
        val intent = Intent(this, InicioSesionActivity::class.java)
        intent.putExtra("IntencionBoton", 1)
        startActivity(intent)
    }

    private fun navigateToRegistro() {
        val intent = Intent(this, InicioSesionActivity::class.java)
        intent.putExtra("IntencionBoton", 2)
        startActivity(intent)
    }
}
