package app.dsantander17.bopit33

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))
        // Obtén una referencia al TextView
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje)

        // Obtén una referencia a las preferencias compartidas
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        val puntajeGuardado = sharedPreferences.getInt("puntaje", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        // Muestra el puntaje en el TextView
        textViewPuntaje.text = "Puntaje: $puntajeGuardado"

        val buttonOKAbout = findViewById<Button>(R.id.about_button)

        // Button click listeners
        buttonOKAbout.setOnClickListener {
            val intentAbout = Intent(this, PlayGame::class.java)
            startActivity(intentAbout)
        }
    }
    override fun onResume() {
        super.onResume()

        // Obtén una referencia al TextView
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje)

        // Obtén una referencia a las preferencias compartidas
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)

        // Supongamos que "puntaje" es el nombre de la clave
        val puntajeGuardado = sharedPreferences.getInt("puntaje", 0) // 0 es el valor predeterminado si no se encuentra el puntaje

        // Muestra el puntaje en el TextView
        textViewPuntaje.text = "$puntajeGuardado"
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