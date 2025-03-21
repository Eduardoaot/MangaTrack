package com.aristidevs.androidmaster

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.aristidevs.androidmaster.buscador.BuscadorActivity
import com.aristidevs.androidmaster.detallesmanga.DetalleMangaActivity.Companion.USER_ID
import com.aristidevs.androidmaster.principalcoleccion.ColeccionDetallesActivity
import com.aristidevs.androidmaster.iniciosesion.InicioSesionMainActivity
import com.aristidevs.androidmaster.principallectura.LecturaActivity


class MenuActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val userId = intent.getIntExtra("USER_ID", -1)


        val btnColeccion = findViewById<Button>(R.id.btnColec)
        val searchViewMenu = findViewById<ImageButton>(R.id.searchViewMenu)
        val btnConfig = findViewById<Button>(R.id.btnConfig)
        val btnLec = findViewById<Button>(R.id.btnLec)


        // Setear los listeners para los botones
        btnColeccion.setOnClickListener { navigateToColeccion(userId) }
        searchViewMenu.setOnClickListener { navigateToBuscador(userId) }
        btnLec.setOnClickListener { navigateToLectura(userId) }
        btnConfig.setOnClickListener { cerrarSesion() }
    }

    private fun navigateToColeccion(userId: Int) {
        val intent = Intent(this, ColeccionDetallesActivity::class.java)
        intent.putExtra("USER_ID", userId) // Pasar el ID como un extra
        startActivity(intent)
    }

    private fun navigateToLectura(userId: Int) {
        val intent = Intent(this, LecturaActivity::class.java)
        intent.putExtra(USER_ID, userId) // Pasar el ID como un extra
        startActivity(intent)
    }

    private fun navigateToBuscador(userId: Int) {
        val intent = Intent(this, BuscadorActivity::class.java)
        intent.putExtra("USER_ID", userId) // Pasar el ID como un extra
        startActivity(intent)
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



}
