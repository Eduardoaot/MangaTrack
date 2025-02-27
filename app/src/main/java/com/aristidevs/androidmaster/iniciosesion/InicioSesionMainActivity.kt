package com.aristidevs.androidmaster.iniciosesion

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aristidevs.androidmaster.MenuActivity
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.coleccion.ColeccionDetallesActivity
import com.aristidevs.androidmaster.databinding.ActivityInicioSesionMainBinding
import com.aristidevs.androidmaster.manga.MangaListActivity
import com.aristidevs.androidmaster.serie.SerieListActivity

class InicioSesionMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()


        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenSplash.setKeepOnScreenCondition { false}

        binding.btnIniciarSesion.setOnClickListener {
            navigateToInicioSesion()  // Navegar a la actividad de Series
        }

        binding.btnRegistro.setOnClickListener {
            navigateToSerieList()  // Navegar a la actividad de Series
        }

    }

    private fun navigateToInicioSesion() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSerieList() {
        val intent = Intent(this, SerieListActivity::class.java)
        startActivity(intent)
    }
}