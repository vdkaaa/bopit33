package app.dsantander17.bopit33

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))



        // Obtén una referencia al TextView
        // val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje)

        // Obtén una referencia a las preferencias compartidas
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        val puntajeGuardado = sharedPreferences.getInt("puntaje Facil", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        // Muestra el puntaje en el TextView
        //textViewPuntaje.text = "Puntaje: $puntajeGuardado"

        val botonJugar = findViewById<Button>(R.id.about_button)
        val botonScore = findViewById<Button>(R.id.boton_score)

        // Button click listeners
        botonJugar.setOnClickListener {

            showDifficultyDialog()
        }
        botonScore.setOnClickListener(){
            showScores()
        }

    }

    private fun showScores(){

        // Iniciar la pantalla de los puntajes
        val intentPlayGame = Intent(this, ScoreScreen::class.java)
        startActivity(intentPlayGame)
        //finish() // Finaliza la actividad actual para evitar volver a esta pantalla
    }
    private fun showDifficultyDialog() {
        var selectedDifficulty: String? = null
        val difficulties = arrayOf("Fácil", "Normal", "Difícil")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona la dificultad")
            .setSingleChoiceItems(difficulties, -1) { dialog, which ->
                selectedDifficulty = difficulties[which]
            }
            .setPositiveButton("Aceptar") { dialog, which ->
                // Usar let para realizar una conversión segura
                selectedDifficulty?.let { difficulty ->
                    // Guardar la dificultad seleccionada en las preferencias compartidas
                    saveDifficultyPreference(difficulty)

                    // Iniciar la pantalla de juego con la dificultad seleccionada
                    val intentPlayGame = Intent(this, PlayGame::class.java)
                    startActivity(intentPlayGame)
                   // finish() // Finaliza la actividad actual para evitar volver a esta pantalla
                }
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                // Cierre del diálogo sin iniciar el juego
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun saveDifficultyPreference(difficulty: String) {
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("dificultad", difficulty)
        editor.apply()
    }


    override fun onResume() {
        super.onResume()

        // Obtén una referencia al TextView
        //val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje)

        // Obtén una referencia a las preferencias compartidas
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        val puntajeGuardado = sharedPreferences.getInt("puntaje Maximo Facil", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        // Muestra el puntaje en el TextView
        //textViewPuntaje.text = "$puntajeGuardado"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intentPreferences = Intent(this, SettingsActivity::class.java)
                startActivity(intentPreferences)
                return true
            }
            R.id.action_about -> {
                val intentPreferences = Intent(this, AboutActivity::class.java)
                startActivity(intentPreferences)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}