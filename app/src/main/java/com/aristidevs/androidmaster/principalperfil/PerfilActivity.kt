package com.aristidevs.androidmaster.principalperfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityMangaListBinding
import com.aristidevs.androidmaster.databinding.ActivityPerfilBinding
import com.aristidevs.androidmaster.iniciosesion.InicioSesionMainActivity
import com.aristidevs.androidmaster.inicioyregistro.LoginRequest
import com.aristidevs.androidmaster.manga.ApiServiceManga
import com.aristidevs.androidmaster.manga.MangaListDataResponse
import com.aristidevs.androidmaster.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofit = RetrofitClient.getRetrofit()

        val userId = intent.getIntExtra("USER_ID", -1).toString()
        if (userId != "-1") {
            initUI(userId)
        }

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUI(userId: String) {
        obtenerDatosPerfil(userId)
        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
        binding.btnMostrarEmail.setOnClickListener {
            solicitarContrasena { contrasena -> autenticarYMostrarDatos(userId, contrasena) }
        }
    }

    private fun obtenerDatosPerfil(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.create(ApiServiceManga::class.java).obtenerPerfil(userId)
            if (response.isSuccessful) {
                val perfil = response.body()
                runOnUiThread {
                    if (perfil != null) {
                        binding.txtEmail.text = "************"
                        binding.txtNombre.text = "************"
                        binding.txtUsuario.text = perfil.usuario
                        binding.txtEmail.tag = perfil.email
                        binding.txtNombre.tag = perfil.nombre
                    }
                }
            } else {
                Log.e("PerfilActivity", "Error al obtener perfil")
            }
        }
    }

    private fun solicitarContrasena(onSuccess: (String) -> Unit) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        AlertDialog.Builder(this)
            .setTitle("Autenticación")
            .setMessage("Ingresa tu contraseña para ver la información:")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val contrasena = input.text.toString()
                if (contrasena.isNotEmpty()) {
                    onSuccess(contrasena)
                } else {
                    Toast.makeText(this, "Contraseña vacía", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun autenticarYMostrarDatos(userId: String, contrasena: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val email = binding.txtEmail.tag as? String ?: return@launch
            val loginRequest = LoginRequest(email, contrasena)
            val loginResponse = retrofit.create(ApiServiceManga::class.java).autenticarUsuario(loginRequest)

            runOnUiThread {
                if (loginResponse.isSuccessful) {
                    binding.txtEmail.text = email
                    binding.txtNombre.text = binding.txtNombre.tag as? String ?: ""
                    binding.btnMostrarEmail.isVisible = false
                } else {
                    Toast.makeText(this@PerfilActivity, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cerrarSesion() {
        getSharedPreferences("Sesion", Context.MODE_PRIVATE).edit().apply {
            remove("sesionIniciada")
            remove("userId")
            apply()
        }
        Log.d("SesionStatus", "Sesión cerrada y datos eliminados.")
        startActivity(Intent(this, InicioSesionMainActivity::class.java))
        finish()
    }
}