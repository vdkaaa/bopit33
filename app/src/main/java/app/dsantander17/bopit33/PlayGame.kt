package app.dsantander17.bopit33

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.media.MediaPlayer
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.preference.PreferenceManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class PlayGame : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var puntajeActual = 100
    private lateinit var mediaPlayerBg: MediaPlayer
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer
    private lateinit var gestureDetector: GestureDetector
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayerBg = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayerWin = MediaPlayer.create(this, R.raw.win)
        mediaPlayerLose = MediaPlayer.create(this, R.raw.lose)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        setContentView(R.layout.activity_play_game)


        gestureDetector = GestureDetector(this, MyGestureListener())

        // Variables para almacenar los valores anteriores del acelerómetro
        var previousX: Float = 0.0f
        var previousY: Float = 0.0f
        var previousZ: Float = 0.0f

// Definición del umbral de cambio
        val threshold = 1.5f // Puedes ajustar este valor según tu requerimiento


        if(accelerometer==null)
            finish();
        //Configuracion del listener del sensor
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Código para el cambio en la precisión del sensor
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val xAxis = event.values[0]
                    val yAxis = event.values[1]
                    val zAxis = event.values[2]

                    // Comparación con los valores anteriores
                    if (hasSignificantChange(xAxis, yAxis, zAxis)) {
                        // Hubo un cambio significativo en el acelerómetro
                        println("Se detectó un evento en el acelerómetro")

                        // Aquí podrías, por ejemplo, mostrar un mensaje, activar una función, etc.
                         showAccelerometerEventMessage()
                    }

                    // Actualizar los valores anteriores con los valores actuales
                    previousX = xAxis
                    previousY = yAxis
                    previousZ = zAxis
                }

            }

            private fun hasSignificantChange(currentX: Float, currentY: Float, currentZ: Float): Boolean {
                // Comparación con los valores anteriores y determinación de cambio significativo
                return (Math.abs(currentX - previousX) > threshold ||
                        Math.abs(currentY - previousY) > threshold ||
                        Math.abs(currentZ - previousZ) > threshold)
            }
            private fun showAccelerometerEventMessage() {
                val message = "¡Se detectó un cambio en el acelerómetro!"

                // Muestra un Toast con el mensaje
                Toast.makeText(this@PlayGame, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Obtén una referencia al TextView en tu diseño de actividad
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje) // Reemplaza R.id.textViewPuntaje con el ID de tu TextView

        // Obtén una referencia a las preferencias compartidas
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)


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

        val buttonPlayBg = findViewById<Button>(R.id.button_play_bg)
        val buttonPlayWin = findViewById<Button>(R.id.button_play_win)
        val buttonPlayLose = findViewById<Button>(R.id.button_play_lose)

        buttonPlayBg.setOnClickListener {
            if (!mediaPlayerBg.isPlaying) {
                mediaPlayerBg.start()
            }
        }

        buttonPlayWin.setOnClickListener {
            if (!mediaPlayerWin.isPlaying) {
                mediaPlayerWin.start()
            }
        }

        buttonPlayLose.setOnClickListener {
            if (!mediaPlayerLose.isPlaying) {
                mediaPlayerLose.start()
            }
        }

        val buttonIncreaseSpeed = findViewById<Button>(R.id.button_increase_speed)
        val buttonDecreaseSpeed = findViewById<Button>(R.id.button_decrease_speed)

        // Aumentar velocidad
        buttonIncreaseSpeed.setOnClickListener {
            if (mediaPlayerBg.isPlaying) {
                val params = mediaPlayerBg.playbackParams
                val speed = params.speed
                if (speed < 2.0f) { // Limitar la velocidad máxima (puedes ajustar esto)
                    params.setSpeed(speed + 0.1f)
                    mediaPlayerBg.playbackParams = params
                }
            }
        }

        // Disminuir velocidad
        buttonDecreaseSpeed.setOnClickListener {
            if (mediaPlayerBg.isPlaying) {
                val params = mediaPlayerBg.playbackParams
                val speed = params.speed
                if (speed > 0.5f) { // Limitar la velocidad mínima (puedes ajustar esto)
                    params.setSpeed(speed - 0.1f)
                    mediaPlayerBg.playbackParams = params
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        // Guarda el puntaje actualizado en las preferencias compartidas al pausar la actividad
        val editor = sharedPreferences.edit()
        editor.putInt("puntaje", puntajeActual)
        editor.apply() // Guarda los cambios
        if (mediaPlayerBg.isPlaying) {
            mediaPlayerBg.pause()
        }
        if (mediaPlayerWin.isPlaying) {
            mediaPlayerWin.pause()
        }
        if (mediaPlayerLose.isPlaying) {
            mediaPlayerLose.pause()
        }

        sensorManager.unregisterListener(sensorEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerBg.release()
        mediaPlayerWin.release()
        mediaPlayerLose.release()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocidadX: Float,
            velocidadY: Float
        ): Boolean {
            if(velocidadY> velocidadX){
                Toast.makeText(this@PlayGame, "Hubo un evento OnFling en la pantalla", Toast.LENGTH_SHORT).show()
            }

            return super.onFling(e1, e2, velocidadX, velocidadY)
        }
    }
}