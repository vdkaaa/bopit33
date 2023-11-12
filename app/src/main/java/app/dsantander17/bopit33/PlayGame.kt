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
import android.os.Handler
import android.view.View

class PlayGame : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var puntajeActual = 0
    private var puntajeMaximo = -1
    private lateinit var mediaPlayerBg: MediaPlayer
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer
    private lateinit var gestureDetector: GestureDetector
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null
    private lateinit var textViewInstruction: TextView
    private lateinit var textViewPuntaje: TextView
    private var listaEventos = arrayOf("Haz click", "Desplazamiento", "Mueve el celular") // Lista de eventos posibles
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private var intento = 5
    private var playbackSpeed = 1.0f // Velocidad de reproducción inicial
    private var playbackSpeedIncrement = 0.2f // Incremento de velocidad por cada aumento en el puntaje
    private var accion: Boolean = false
    private var timeGame: Long = 5000
    private val intervalo = 100
    private lateinit var textoTime: TextView
    private var tiempoInicial: Long = 0 // Ajusta según la dificultad
    private var numeroIntentos: Int = 0 // Ajusta según la dificultad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)
        mediaPlayerBg = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayerWin = MediaPlayer.create(this, R.raw.win)
        mediaPlayerLose = MediaPlayer.create(this, R.raw.lose)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textViewInstruction = findViewById(R.id.textViewInstruction)
        textViewPuntaje = findViewById(R.id.textViewPuntaje)
        textoTime = findViewById(R.id.textoTime)
        mediaPlayerBg.start()


        // recuperar las variables de misPreferencias y recuperar la dificultad
        val sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val dificultad = sharedPreferences.getString("dificultad", "Normal")

        // Dificultad
        ajustarReglasSegunDificultad(dificultad)

        gestureDetector = GestureDetector(this, MyGestureListener())



        // Variable para almacenar los valores anteriores del acelerómetro
        var previousX: Float = 0.0f
        var previousY: Float = 0.0f
        var previousZ: Float = 0.0f

        // Definición del umbral de cambio
        val threshold = 1.5f // Puedes ajustar este valor según tu requerimiento


        if (accelerometer == null)
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

                    // Actualizar los valores anteriores con los valores actuales
                    previousX = xAxis
                    previousY = yAxis
                    previousZ = zAxis
                    // Comparación con los valores anteriores
                    if (hasSignificantChange(xAxis, yAxis, zAxis)) {
                        verificarEvento("Mueve el celular")
                        //Aqui se verifica si se hizo un cambio en el acelerometro.
                    }


                }

            }

            private fun hasSignificantChange(
                currentX: Float,
                currentY: Float,
                currentZ: Float
            ): Boolean {
                // Comparación con los valores anteriores y determinación de cambio significativo
                return (Math.abs(currentX - previousX) > threshold ||
                        Math.abs(currentY - previousY) > threshold ||
                        Math.abs(currentZ - previousZ) > threshold)
            }

        }

        //Referencia al TextView en tu diseño de actividad
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje) // Reemplaza R.id.textViewPuntaje con el ID de tu TextView


        puntajeMaximo = sharedPreferences.getInt(
            "puntaje",
            puntajeMaximo
        ) // Obtén el puntaje guardado, o usa el valor predeterminado puntajeActual si no se encuentra

        // Muestra el puntaje en el TextView
        textViewPuntaje.text = "$puntajeActual"



        textoTime.text = timeGame.toString()
        // Inicia la generación constante de instrucciones cada 3 segundos
        generarYMostrarInstruccion()
        iniciarTemporizador()

    }

    private fun ajustarReglasSegunDificultad(dificultad: String?) {
        when (dificultad) {
            "Fácil" -> {
                tiempoInicial = 10000 // Tiempo en milisegundos
                numeroIntentos = 10
                if(puntajeActual>5){

                }
            }
            "Normal" -> {
                tiempoInicial = 5000
                numeroIntentos = 5
                // Ajusta otras reglas para la dificultad normal si es necesario
            }
            "Difícil" -> {
                tiempoInicial = 3000
                numeroIntentos = 3
                // Ajusta otras reglas para la dificultad difícil si es necesario
            }
            else -> {
                // Dificultad por defecto o manejo de otro caso
            }
        }

        // Inicializa variables relacionadas con el tiempo y los intentos según la dificultad
        timeGame = tiempoInicial
        intento = numeroIntentos
    }

    private fun iniciarTemporizador() {
        runnable = object : Runnable {
            override fun run() {
                if (timeGame >= 0) {
                    val segundos = timeGame / 1000
                    val milisegundos = (timeGame % 1000) / 10 // Obtener dos dígitos para los milisegundos

                    val formattedTime = String.format("%02d.%02d", segundos, milisegundos)
                    textoTime.text = formattedTime

                    timeGame -= intervalo

                    handler.postDelayed(this, intervalo.toLong())
                } else {
                    // Detener el temporizador cuando el tiempo llega a cero
                    detenerTemporizador()
                }
            }
        }

        // Iniciar el proceso del temporizador
        handler.post(runnable)
    }

    private fun reiniciarTemporizador() {
        // Restablecer el tiempo del temporizador según la dificultad (puedes ajustar esto)
        timeGame = tiempoInicial

        // Detener y volver a iniciar el temporizador
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }
    private fun detenerTemporizador() {
        // Detener el temporizador
        handler.removeCallbacks(runnable)

        // Puedes realizar acciones adicionales cuando el temporizador se detiene, si es necesario
        // Por ejemplo, mostrar un mensaje o realizar alguna acción específica
    }


    private fun mostrarInstruccionEnPantalla(instruccion: String) {
        textViewInstruction.text = instruccion
    }

    private fun verificarEvento(evento: String) {
        val instruccionActual = textViewInstruction.text.toString()

        if (evento == instruccionActual ) {
            incrementarPuntaje()
            generarYMostrarInstruccion()

        } else {
            terminarJuego()
            generarYMostrarInstruccion()
        }
    }
    private fun generarYMostrarInstruccion() {
        // Genera una instrucción aleatoria
        val instruccion = listaEventos.random()

        // Muestra la instrucción en el centro de la pantalla
        mostrarInstruccionEnPantalla(instruccion)

    }

    override fun onPause() {
        super.onPause()
        // Guarda el puntaje actualizado en las preferencias compartidas al pausar la actividad
        val editor = sharedPreferences.edit()
        editor.putInt("puntaje", puntajeMaximo)
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

        //timeGame-=10

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Acción al presionar la pantalla (clic)
                verificarEvento("Haz click")
            }
            MotionEvent.ACTION_MOVE -> {
                // Acción al mover el dedo por la pantalla
                verificarEvento("Desplazamiento")
            }
        }

        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }



    private inner class MyGestureListener : GestureDetector.SimpleOnGestureListener()
    {
        private val SWIPE_THRESHOLD = 150
        private val SWIPE_VELOCITY_THRESHOLD = 150
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val deltaX = e2.x - e1.x
            val deltaY = e2.y - e1.y

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    verificarEvento("Desliza")
                    Log.d("MyGestureListener", "Desliza hacia la izquierda/derecha")

                }
            } /*else {
                if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    verificarEvento("Desliza")
                    Log.d("MyGestureListener", "Desliza hacia arriba/abajo")
                }
            }*/
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    // Método para incrementar el puntaje
    private fun incrementarPuntaje() {

        mediaPlayerWin.start()
        puntajeActual++
        playbackSpeed += playbackSpeedIncrement
        mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
        // Actualiza el TextView
        textViewPuntaje.text = "$puntajeActual"
        // Guarda el puntaje actualizado en las preferencias compartidas
        if(puntajeMaximo<puntajeActual){
            puntajeMaximo = puntajeActual
            val editor = sharedPreferences.edit()
            editor.putInt("puntaje", puntajeMaximo)
            editor.apply() // Guarda los cambios
        }
        reiniciarTemporizador()

    }

    // Método para terminar el juego
    private fun terminarJuego() {
        // Reproduce el sonido asociado o resta un intento
        mediaPlayerLose.start()
        intento--;
        if(intento <=0){
            puntajeActual = 0;
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar volver a esta pantalla
        }

    }


}