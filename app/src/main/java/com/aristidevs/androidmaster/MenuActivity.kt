package com.aristidevs.androidmaster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.aristidevs.androidmaster.coleccion.ColeccionDetallesActivity
import com.aristidevs.androidmaster.firstapp.FirstAppActivity
import com.aristidevs.androidmaster.imccalculator.ImcCalculatorActivity
import com.aristidevs.androidmaster.manga.MangaListActivity
import com.aristidevs.androidmaster.serie.SerieListActivity
import com.aristidevs.androidmaster.settings.SettingsActivity
import com.aristidevs.androidmaster.superheroapp.SuperHeroListActivity
import com.aristidevs.androidmaster.todoapp.TodoActivity



class MenuActivity : AppCompatActivity() {

    private val ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)



        val btnColeccion = findViewById<Button>(R.id.btnColec)

        // Setear los listeners para los botones


        btnColeccion.setOnClickListener { navigateToColeccion() }
    }



    private fun navigateToColeccion() {
        val intent = Intent(this, ColeccionDetallesActivity::class.java)
        intent.putExtra("USER_ID", ID) // Pasar el ID como un extra
        startActivity(intent)
    }
}
