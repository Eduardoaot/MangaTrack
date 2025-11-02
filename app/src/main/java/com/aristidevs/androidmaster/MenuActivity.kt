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
import com.aristidevs.androidmaster.principalmonetario.MonetarioActivity
import com.aristidevs.androidmaster.principalpamodoru.PamodoruActivity
import com.aristidevs.androidmaster.principalperfil.PerfilActivity
import com.aristidevs.androidmaster.principalpresupuestos.presupuestos.PresupuestosActivity


class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val userId = intent.getIntExtra("USER_ID", -1)
        val btnColeccion = findViewById<Button>(R.id.btnColec)
        val searchViewMenu = findViewById<ImageButton>(R.id.searchViewMenu)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val btnLec = findViewById<Button>(R.id.btnLec)
        val btnPamo = findViewById<Button>(R.id.btnPamo)
        val btnPresup = findViewById<Button>(R.id.btnPresup)
        val btnMonetario = findViewById<Button>(R.id.btnMonetario)
        btnColeccion.setOnClickListener { navigateToColeccion(userId) }
        searchViewMenu.setOnClickListener { navigateToBuscador(userId) }
        btnLec.setOnClickListener { navigateToLectura(userId) }
        btnPamo.setOnClickListener { navigateToPamodoru(userId) }
        btnPerfil.setOnClickListener { navigateToPerfil(userId) }
        btnPresup.setOnClickListener { navigateToPresupuestos(userId) }
        btnMonetario.setOnClickListener { navigateToMonetario(userId) }
    }
    private fun navigateToPresupuestos(userId: Int) {
        val intent = Intent(this, PresupuestosActivity::class.java)
        intent.putExtra(USER_ID, userId) // Pasar el ID como un extra
        startActivity(intent)
    }
    private fun navigateToMonetario(userId: Int) {
        val intent = Intent(this, MonetarioActivity::class.java)
        intent.putExtra(USER_ID, userId) // Pasar el ID como un extra
        startActivity(intent)
    }
    private fun navigateToColeccion(userId: Int) {
        val intent = Intent(this, ColeccionDetallesActivity::class.java)
        intent.putExtra("USER_ID", userId) // Pasar el ID como un extra
        startActivity(intent)
    }
    private fun navigateToPamodoru(userId: Int) {
        val intent = Intent(this, PamodoruActivity::class.java)
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
    private fun navigateToPerfil(userId: Int) { val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("USER_ID", userId) // Pasar el ID como un extra
        startActivity(intent)
    }
}
