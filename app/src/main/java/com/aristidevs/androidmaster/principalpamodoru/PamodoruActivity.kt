package com.aristidevs.androidmaster.principalpamodoru

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.aristidevs.androidmaster.R
import com.aristidevs.androidmaster.databinding.ActivityPamodoruBinding
import java.util.concurrent.TimeUnit

class PamodoruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPamodoruBinding

    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isPomodoro = true
    private var pomodoroCount = 0
    private var descansoCount = 0
    private var maxCiclos = 0
    private var ciclosCompletados = 0
    private var pomodoroDuration = 25 * 60 * 1000L // default
    private var descansoDuration = 5 * 60 * 1000L  // default
    private var timeLeftInMillis: Long = pomodoroDuration
    private var mediaPlayer: MediaPlayer? = null
    private var incremento: Long = 1 * 60 * 1000L // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPamodoruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        showCiclosDialog()

        binding.btnflecha.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // Detener el temporizador si está corriendo
        timer?.cancel()
        isRunning = false

        // Detener el sonido y liberar el MediaPlayer
        stopSound()

        // Finaliza la actividad
        finish()
    }



    private fun initUI() {
        binding.estadoFinalizacion.isVisible = false
        binding.progressBar.max = (timeLeftInMillis / 1000).toInt()
        binding.progressBar.progress = (timeLeftInMillis / 1000).toInt()
        updateTimerText()

        binding.play.setOnClickListener { startTimer() }
        binding.pause.setOnClickListener { pauseTimer() }
        binding.reiniciar.setOnClickListener { resetTimer() }
        binding.menos.setOnClickListener { cincominmenos() }
        binding.mas.setOnClickListener { cincominmas() }

        binding.btnTiempo.setOnClickListener { showTimeSelectionDialog() }
        binding.btnMasMenos.setOnClickListener { showIncrementSelectionDialog() }
    }

    private fun showTimeSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.elegir_time, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val pomoGroup = dialogView.findViewById<RadioGroup>(R.id.pomodoroGroup)
        val descansoGroup = dialogView.findViewById<RadioGroup>(R.id.descansoGroup)
        val btnAceptar = dialogView.findViewById<Button>(R.id.Aceptar)
        val btnCancelarPamodoru = dialogView.findViewById<Button>(R.id.btnCancelarPamodoru)

        btnAceptar.setOnClickListener {
            when (pomoGroup.checkedRadioButtonId) {
                R.id.pomo20 -> pomodoroDuration = 20 * 60 * 1000L
                R.id.pomo25 -> pomodoroDuration = 25 * 60 * 1000L
                R.id.pomo30 -> pomodoroDuration = 30 * 60 * 1000L
            }

            when (descansoGroup.checkedRadioButtonId) {
                R.id.desc5 -> descansoDuration = 5 * 60 * 1000L
                R.id.desc10 -> descansoDuration = 10 * 60 * 1000L
                R.id.desc15 -> descansoDuration = 15 * 60 * 1000L
            }

            Toast.makeText(this, "Pomodoro: " +
                    "${pomodoroDuration / 60000} min | Descanso: " +
                    "${descansoDuration / 60000} min",
                Toast.LENGTH_SHORT).show()

            if (isPomodoro) {
                timeLeftInMillis = pomodoroDuration
            } else {
                timeLeftInMillis = descansoDuration
            }

            binding.progressBar.max = (timeLeftInMillis / 1000).toInt()
            updateTimerText()
            dialog.dismiss()
        }

        btnCancelarPamodoru.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showIncrementSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.boton_tiempo, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val minGroup = dialogView.findViewById<RadioGroup>(R.id.minutoGroup)
        val btnAceptar = dialogView.findViewById<Button>(R.id.Aceptar)
        val btnCancelarTiempos = dialogView.findViewById<Button>(R.id.btnCancelarTiempos)

        btnAceptar.setOnClickListener {
            when (minGroup.checkedRadioButtonId) {
                R.id.min01 -> incremento = 1 * 60 * 1000L
                R.id.min05 -> incremento = 5 * 60 * 1000L
                R.id.min10 -> incremento = 10 * 60 * 1000L
                R.id.min30 -> incremento = 30 * 60 * 1000L
            }

            Toast.makeText(this, "Incremento configurado en ${incremento / 60000} min", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnCancelarTiempos.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startTimer(restart: Boolean = true) {
        if (isRunning) return
        isRunning = true
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
                binding.progressBar.progress = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                if (isPomodoro) {
                    pomodoroCount++
                    updatePomodoroCounter()
                    playSound()
                    timeLeftInMillis = descansoDuration
                } else {
                    descansoCount++
                    updateDescansoCounter()
                    playSound()
                    ciclosCompletados++
                    if (ciclosCompletados >= maxCiclos) {
                        terminarPomodoro()

                        isRunning = false
                        return
                    }
                    timeLeftInMillis = pomodoroDuration
                }
                isPomodoro = !isPomodoro
                updateEstado()
                updateProgressBar()
                isRunning = false
                startTimer(true)
            }
        }.start()
    }

    private fun terminarPomodoro() {
        // Hacer invisibles los elementos
        binding.menos.isVisible = false
        binding.play.isVisible = false
        binding.pause.isVisible = false
        binding.reiniciar.isVisible = false
        binding.mas.isVisible = false
        binding.txtVista.isVisible = false
        binding.btnTiempo.isVisible = false
        binding.btnMasMenos.isVisible = false
        binding.progressBar.isVisible = false
        binding.estadoActual.isVisible = false
        binding.estadoFinalizacion.isVisible = true
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false

        timeLeftInMillis = if (isPomodoro) {
            pomodoroDuration
        } else {
            descansoDuration
        }

        binding.progressBar.max = (timeLeftInMillis / 1000).toInt()
        updateTimerText()
        updateEstado()
    }

    private fun cincominmenos() {
        if (timeLeftInMillis - incremento <= 0) {
            // Si el tiempo a restar es mayor que el tiempo restante, establecer en 3 segundos
            timeLeftInMillis = 3000 // 3 segundos
        } else {
            timeLeftInMillis -= incremento
        }

        // Ajustar el progreso sin reiniciar la barra
        val progress = (timeLeftInMillis / 1000).toInt()
        binding.progressBar.progress = progress
        updateTimerText()

        if (isRunning) restartTimerLogic()
    }

    private fun cincominmas() {
        timeLeftInMillis += incremento

        // Ajustar el progreso sin reiniciar la barra
        val progress = (timeLeftInMillis / 1000).toInt()
        binding.progressBar.progress = progress
        updateTimerText()

        if (isRunning) restartTimerLogic()
    }

    private fun restartTimerLogic() {
        timer?.cancel()
        isRunning = false
        startTimer()
    }

    private fun showCiclosDialog() {
        val dialogView = layoutInflater.inflate(R.layout.elegir_ciclos, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val ciclosInput = dialogView.findViewById<EditText>(R.id.ciclosInput)
        val btnAceptar = dialogView.findViewById<Button>(R.id.enviarButton)
        val btnCancelarCiclos = dialogView.findViewById<Button>(R.id.btnCancelarCiclos)

        btnAceptar.setOnClickListener {
            val ciclos = ciclosInput.text.toString().toIntOrNull() ?: 0
            if (ciclos > 0) {
                maxCiclos = ciclos
                Toast.makeText(this, "Configurados $maxCiclos ciclos.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startPomodoro()
            } else {
                Toast.makeText(this, "Por favor, ingresa un número válido de ciclos.", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelarCiclos.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
            onBackPressed() // Regresa a la pantalla anterior
        }

        dialog.show()
    }

    private fun startPomodoro() {
        pomodoroCount = 0
        descansoCount = 0
        if (maxCiclos > 0) {
            startTimer(true)
        } else {
            Toast.makeText(this, "Por favor, configura un número válido de ciclos.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimerText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) % 60
        binding.txtVista.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun playSound() {
        stopSound()
        mediaPlayer = MediaPlayer.create(this, R.raw.alarma)
        mediaPlayer?.start()
        Handler().postDelayed({
            stopSound()
        }, 2000)
    }

    override fun onStop() {
        super.onStop()
        // Detener el sonido y liberar el MediaPlayer
        stopSound()
    }

    override fun onPause() {
        super.onPause()
        // Detener el sonido y liberar el MediaPlayer
        stopSound()
    }

    private fun stopSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()  // Asegúrate de liberar los recursos del MediaPlayer
        }
        mediaPlayer = null
    }

    private fun updatePomodoroCounter() {
        binding.txtTotalPamodorus.text = "$pomodoroCount"
    }

    private fun updateDescansoCounter() {
        binding.txtTotalDescansos.text = "$descansoCount"
    }

    private fun updateEstado() {
        // Actualizar el texto del estado
        binding.estadoActual.text = if (isPomodoro) "¡Continua Pamodoru!" else "¡Descansa!"

        // Definir los drawables y colores según el estado de isPomodoro
        val drawableCirculo1 = if (isPomodoro) R.drawable.circulo_1 else R.drawable.circulo_3
        val drawableCirculo2 = if (isPomodoro) R.drawable.circulo_2 else R.drawable.circulo_4
        val colorView = if (isPomodoro) R.color.Principal else R.color.purple_200
        val colorBtnPamodoru = if (isPomodoro) R.color.PrincipalOscuro else R.color.purple_200
        val colorBtnModificar = if (isPomodoro) R.color.purple_200 else R.color.Principal

        // Drawables para los botones
        val drawableMenos = if (isPomodoro) R.drawable.menos_2 else R.drawable.menos_3
        val drawableMas = if (isPomodoro) R.drawable.mas_2 else R.drawable.mas_3
        val drawablePausa = if (isPomodoro) R.drawable.pausa2 else R.drawable.pausa3
        val drawablePlay = if (isPomodoro) R.drawable.play_2 else R.drawable.play_3
        val drawableReiniciar = if (isPomodoro) R.drawable.reinicio_2 else R.drawable.reinicio_3

        // Aplicar los drawables al ProgressBar
        binding.progressBar.background = ContextCompat.getDrawable(this, drawableCirculo2)
        binding.progressBar.progressDrawable = ContextCompat.getDrawable(this, drawableCirculo1)

        // Cambiar el color de fondo de la View
        binding.view.setBackgroundColor(ContextCompat.getColor(this, colorView))

        // Cambiar el backgroundTint de los botones
        binding.btnTiempo.backgroundTintList = ContextCompat.getColorStateList(this, colorBtnPamodoru)
        binding.btnMasMenos.backgroundTintList = ContextCompat.getColorStateList(this, colorBtnModificar)

        // Aplicar los drawables a los botones
        binding.menos.setImageResource(drawableMenos)
        binding.mas.setImageResource(drawableMas)
        binding.pause.setImageResource(drawablePausa)
        binding.play.setImageResource(drawablePlay)
        binding.reiniciar.setImageResource(drawableReiniciar)
    }

    private fun updateProgressBar() {
        binding.progressBar.max = (timeLeftInMillis / 1000).toInt()
        binding.progressBar.progress = (timeLeftInMillis / 1000).toInt()
    }
}