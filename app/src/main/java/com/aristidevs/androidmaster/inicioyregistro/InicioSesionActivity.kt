package com.aristidevs.androidmaster.inicioyregistro

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aristidevs.androidmaster.MenuActivity
import com.aristidevs.androidmaster.databinding.ActivityInicioSesionBinding
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        // Verificar si el usuario ya está autenticado
        if (verificarSesion()) {
            val userId = obtenerUserId()
            if (userId != null) {
                // Si el userId no es nulo, redirigir al menú
                navigateToMenu(userId)
            }
        }

        val intencionBoton = intent.getIntExtra("IntencionBoton", -1)
        initUI(intencionBoton)
    }

    private fun initUI(intencionBoton: Int) {
        if(intencionBoton == 1){
            InicioSesion()
        } else if (intencionBoton == 2){
            Registro()
        }

        binding.btnreg.setOnClickListener {
            Registro() // Navegar a la actividad de Series
        }

        binding.btnInis.setOnClickListener {
            InicioSesion()  // Navegar a la actividad de Series
        }

        binding.btnIniciarSesion.setOnClickListener {
            val usuario = binding.NombreInicioSesion.text.toString()
            val contrasena = binding.ContraseniaaInicoSesion.text.toString()

            autenticarUsuario(usuario, contrasena)
        }

        binding.btnRegistrarse.setOnClickListener {
            val nombre = binding.NombreRegistro.text.toString()
            val email = binding.EmailRegistro.text.toString()
            val nombreUsuario = binding.NombreusuRegistro.text.toString()
            val contrasena = binding.ContraseniaaRegistro.text.toString()

            registrarUsuario(nombre, email, nombreUsuario, contrasena)
        }

        binding.btnCerrar.setOnClickListener {
            // Acción para regresar a la actividad anterior utilizando onBackPressedDispatcher
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun registrarUsuario(nombre: String, email: String, usuario: String, contrasena: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val nuevoUsuario = RegistroUsuarioRequest(nombre, email, usuario, contrasena, 0)
            val myResponse = retrofit.create(ApiServiceManga::class.java).registrarUsuario(nuevoUsuario)

            if (myResponse.isSuccessful) {
                val response = myResponse.body()

                if (response != null) {
                    runOnUiThread {
                        Toast.makeText(this@InicioSesionActivity, "Registro exitoso: ${response.message}", Toast.LENGTH_SHORT).show()
                        val userId = response.userId
                        if (userId != null) {
                            guardarSesion(userId)
                            navigateToMenu(userId)
                        }
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@InicioSesionActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun autenticarUsuario(email: String, contrasena: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val loginRequest = LoginRequest(email, contrasena)
            val myResponse = retrofit.create(ApiServiceManga::class.java).autenticarUsuario(loginRequest)

            if (myResponse.isSuccessful) {
                val response = myResponse.body()
                runOnUiThread {
                    if (response != null) {
                        val userId = response.userId?.toIntOrNull()
                        if (userId != null) {
                            guardarSesion(userId)
                            navigateToMenu(userId)
                        } else {
                            Toast.makeText(this@InicioSesionActivity, response.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@InicioSesionActivity, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@InicioSesionActivity, "Error al autenticar usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMenu(userid: Int) {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("USER_ID", userid) // Pasar el ID como un extra
        startActivity(intent)
        finish()
    }


    private fun InicioSesion(){
        binding.NombreRegistro.isVisible = false
        binding.EmailRegistro.isVisible = false
        binding.NombreusuRegistro.isVisible = false
        binding.ContraseniaaRegistro.isVisible = false
        binding.btnRegistrarse.isVisible = false

        binding.NombreInicioSesion.isVisible = true
        binding.ContraseniaaInicoSesion.isVisible = true
        binding.btnIniciarSesion.isVisible = true

        binding.btnInis.elevation = 10f
        binding.btnreg.elevation = 5f

        binding.btnreg.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C760686A"))
        binding.btnInis.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1469C7"))
    }

    private fun Registro(){
        binding.NombreInicioSesion.isVisible = false
        binding.ContraseniaaInicoSesion.isVisible = false
        binding.btnIniciarSesion.isVisible = false

        binding.btnRegistrarse.isVisible = true
        binding.NombreRegistro.isVisible = true
        binding.EmailRegistro.isVisible = true
        binding.NombreusuRegistro.isVisible = true
        binding.ContraseniaaRegistro.isVisible = true

        binding.btnreg.elevation = 10f
        binding.btnInis.elevation = 5f

        binding.btnreg.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1469C7"))
        binding.btnInis.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C760686A"))
    }

    // Verificar si hay sesión activa
    private fun verificarSesion(): Boolean {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("sesionIniciada", false)
    }

    // Guardar el ID del usuario y el estado de la sesión
    private fun guardarSesion(userId: Int) {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("sesionIniciada", true)
        editor.putInt("userId", userId)  // Guardar el userId
        editor.apply()
    }

    // Obtener el ID del usuario guardado
    private fun obtenerUserId(): Int? {
        val sharedPreferences = getSharedPreferences("Sesion", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1).takeIf { it != -1 }
    }
}
