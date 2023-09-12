package app.dsantander17.bopit33

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.preference.PreferenceManager

class PlayGame : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var puntajeActual = 100 // Reemplaza esto con tu puntaje real

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        // Obtén una referencia al TextView en tu diseño de actividad
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje) // Reemplaza R.id.textViewPuntaje con el ID de tu TextView

        // Obtén una referencia a las preferencias compartidas
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        puntajeActual = sharedPreferences.getInt("puntaje", puntajeActual) // Obtén el puntaje guardado, o usa el valor predeterminado puntajeActual si no se encuentra

        // Muestra el puntaje en el TextView
        textViewPuntaje.text = "$puntajeActual"

        val buttonIncrementador = findViewById<Button>(R.id.incrementador)

        // Button click listeners
        buttonIncrementador.setOnClickListener {
            // Incrementa el puntaje
            puntajeActual++
            // Actualiza el TextView
            textViewPuntaje.text = "$puntajeActual"
            // Guarda el puntaje actualizado en las preferencias compartidas
            val editor = sharedPreferences.edit()
            editor.putInt("puntaje", puntajeActual)
            editor.apply() // Guarda los cambios
        }
    }

    override fun onPause() {
        super.onPause()
        // Guarda el puntaje actualizado en las preferencias compartidas al pausar la actividad
        val editor = sharedPreferences.edit()
        editor.putInt("puntaje", puntajeActual)
        editor.apply() // Guarda los cambios
    }
}