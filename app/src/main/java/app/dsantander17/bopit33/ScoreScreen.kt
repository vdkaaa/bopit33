package app.dsantander17.bopit33

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ScoreScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_screen)
        val textViewPuntajeFacil = findViewById<TextView>(R.id.puntuacion_facil_texto)

        val textViewPuntajeNormal = findViewById<TextView>(R.id.puntuacion_normal_texto)

        val textViewPuntajeDificil = findViewById<TextView>(R.id.puntuacion_dificil_texto)

        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        val puntajeFacil = sharedPreferences.getInt("puntaje Maximo Facil", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        val puntajeNormal = sharedPreferences.getInt("puntaje Maximo Normal", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        val puntajeDificil = sharedPreferences.getInt("puntaje Maximo Dificil", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        // Muestra el puntaje en el TextView
        textViewPuntajeFacil.text = "$puntajeFacil"

        textViewPuntajeNormal.text = "$puntajeNormal"

        textViewPuntajeDificil.text = "$puntajeDificil"
    }

}