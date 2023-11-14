package app.dsantander17.bopit33

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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



class PlayGame : AppCompatActivity()  {

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
    private var listaEventos = arrayOf("Haz click", "Haz doble click","Manten Presionado","Agitar") // Lista de eventos posibles
    private var handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private var intento = 5
    private var playbackSpeed = 1.0f // Velocidad de reproducción inicial
    private var playbackSpeedIncrement = 0.05f // Incremento de velocidad por cada aumento en el puntaje
    private var timeGame: Long = 5000
    private val intervalo = 100
    private lateinit var textoTime: TextView
    private var tiempoInicial: Long = 0
    private var numeroIntentos: Int = 0
    private var puntajeFacil: Int = 0
    private var puntajeNormal: Int = 0
    private var puntajeDificil: Int = 0
    private var puntajeMaximoFacil: Int = 0
    private var puntajeMaximoNormal: Int = 0
    private var puntajeMaximoDificil: Int = 0
    private var puntajeLogrado: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)
        mediaPlayerBg = MediaPlayer.create(this, R.raw.bgmusica)
        mediaPlayerWin = MediaPlayer.create(this, R.raw.win)
        mediaPlayerLose = MediaPlayer.create(this, R.raw.lose)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textViewInstruction = findViewById(R.id.textViewInstruction)
        textViewPuntaje = findViewById(R.id.textViewPuntaje)
        textoTime = findViewById(R.id.textoTime)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Bloquear en modo retrato
        mediaPlayerBg.setLooping(true)
        mediaPlayerBg.start()


        // recuperar las variables de misPreferencias y recuperar la dificultad
        sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
        val dificultad = sharedPreferences.getString("dificultad", "Normal")

        // Dificultad
        ajustarReglasSegunDificultad(dificultad)

        gestureDetector = GestureDetector(this, MyGestureListener(this))

        //Referencia al TextView en tu diseño de actividad
        val textViewPuntaje = findViewById<TextView>(R.id.textViewPuntaje) // Reemplaza R.id.textViewPuntaje con el ID de tu TextView

        puntajeMaximoFacil = sharedPreferences.getInt(
            "puntaje Maximo Facil",
            puntajeMaximoFacil
        ) // Obtén el puntaje guardado, o usa el valor predeterminado puntajeActual si no se encuentra

        // Muestra el puntaje en el TextView
        textViewPuntaje.text = "$puntajeFacil"

        textoTime.text = timeGame.toString()
        // Inicia la generación constante de instrucciones cada 3 segundos
        configurarAcelerometro()
        generarYMostrarInstruccion()
        iniciarTemporizador()



    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
    private fun ajustarReglasSegunDificultad(dificultad: String?) {
        when (dificultad) {
            "Fácil" -> {
                tiempoInicial = 10000 // Tiempo en milisegundos
                numeroIntentos = 1
            }
            "Normal" -> {
                tiempoInicial = 5000
                numeroIntentos = 1
                // Ajusta otras reglas para la dificultad normal si es necesario
            }
            "Difícil" -> {
                tiempoInicial = 3000
                numeroIntentos = 1
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
        terminarJuego()

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

    // Método para incrementar el puntaje
    private fun incrementarPuntaje() {

        mediaPlayerWin.start()
        val dificultad = sharedPreferences.getString("dificultad", "Normal")
        when(dificultad){
            "Fácil" -> {
                puntajeFacil++

                // Actualiza el TextView
                textViewPuntaje.text = "$puntajeFacil"
                // Guarda el puntaje actualizado en las preferencias compartidas
                if(puntajeMaximoFacil<puntajeFacil){
                    puntajeMaximoFacil = puntajeFacil
                    val editor = sharedPreferences.edit()
                    editor.putInt("puntaje Maximo Facil", puntajeMaximoFacil)
                    editor.apply() // Guarda los cambios
                }

                if(puntajeFacil in 6..9){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)

                    //Disminuimos el tiempo
                    tiempoInicial = 5000
                }else if (puntajeFacil in 11..50){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
                    tiempoInicial = 2500
                }


            }
            "Normal" -> {
                puntajeNormal++
                //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                playbackSpeed += playbackSpeedIncrement
                mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
                // Actualiza el TextView
                textViewPuntaje.text = "$puntajeNormal"
                // Guarda el puntaje actualizado en las preferencias compartidas
                if(puntajeMaximoNormal<puntajeNormal){
                    puntajeMaximoNormal = puntajeNormal
                    val editor = sharedPreferences.edit()
                    editor.putInt("puntaje Maximo Normal", puntajeMaximoNormal)
                    editor.apply() // Guarda los cambios
                }
                if(puntajeFacil in 6..9){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)

                    //Disminuimos el tiempo
                    tiempoInicial = 2500
                }else if (puntajeFacil in 11..50){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
                    tiempoInicial = 1000
                }
            }
            "Difícil" -> {
                puntajeDificil++
                //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                playbackSpeed += playbackSpeedIncrement
                mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
                // Actualiza el TextView
                textViewPuntaje.text = "$puntajeDificil"
                // Guarda el puntaje actualizado en las preferencias compartidas
                if(puntajeMaximoDificil<puntajeDificil){
                    puntajeMaximoDificil = puntajeDificil
                    val editor = sharedPreferences.edit()
                    editor.putInt("puntaje Maximo Dificil", puntajeMaximoDificil)
                    editor.apply() // Guarda los cambios
                }
                if(puntajeDificil in 6..9){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)

                    //Disminuimos el tiempo
                    tiempoInicial = 1500
                }else if (puntajeNormal in 11..50){
                    //Crear otro if para aumentar la velocidad de acuerdo a los aciertos del jugador.
                    playbackSpeed += playbackSpeedIncrement
                    mediaPlayerBg.playbackParams = mediaPlayerBg.playbackParams.setSpeed(playbackSpeed)
                    tiempoInicial = 500
                }

            }
            else -> {
                // Dificultad por defecto o manejo de otro caso
            }
        }

        reiniciarTemporizador()

    }

    // Método para terminar el juego
    private fun terminarJuego() {
        // Reproduce el sonido asociado o resta un intento
        intento--
        if(intento <=0){
            mostrarToast("¡Perdiste!")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            mediaPlayerLose.start()
            finish() // Finaliza la actividad actual para evitar volver a esta pantalla

        }
    }



    private fun configurarAcelerometro() {
        // Variable para almacenar los valores anteriores del acelerómetro
        var previousX: Float = 0.0f
        var previousY: Float = 0.0f
        var previousZ: Float = 0.0f

        // Definición del umbral de cambio
        val threshold = 2.0f // Puedes ajustar este valor según tu requerimiento

        // Tiempo mínimo entre detecciones de movimiento (en milisegundos)
        val minTimeBetweenMovements = 1000

        // Último tiempo en que se detectó un movimiento
        var lastMovementTime: Long = System.currentTimeMillis()

        // Configuración del listener del sensor
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Código para el cambio en la precisión del sensor
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val xAxis = event.values[0]
                    val yAxis = event.values[1]
                    val zAxis = event.values[2]

                    // Obtén el tiempo actual
                    val currentTime = System.currentTimeMillis()

                    // Verifica el tiempo transcurrido desde el último movimiento detectado
                    val elapsedTimeSinceLastMovement = currentTime - lastMovementTime

                    // Comparación con los valores anteriores y determinación de cambio significativo
                    if (hasSignificantChange(xAxis, yAxis, zAxis) && elapsedTimeSinceLastMovement > minTimeBetweenMovements) {
                        verificarEvento("Agitar")
                        // Aquí puedes realizar acciones adicionales si se detecta un movimiento

                        // Actualiza el tiempo del último movimiento
                        lastMovementTime = currentTime
                    }

                    // Actualizar los valores anteriores con los valores actuales
                    previousX = xAxis
                    previousY = yAxis
                    previousZ = zAxis
                }
            }

            private fun hasSignificantChange(
                currentX: Float,
                currentY: Float,
                currentZ: Float
            ): Boolean {
                // Comparación con los valores anteriores y determinación de cambio significativo
                return (currentX - previousX > threshold ||
                        currentY - previousY > threshold ||
                        currentZ - previousZ > threshold)
            }
        }
    }


    // Función para mostrar un Toast
    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private inner class MyGestureListener(private val playGame: PlayGame) : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // This is called when a touch event is first received.

            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            // This is called when a single tap is confirmed.
            // Handle single tap action here.
            playGame.verificarEvento("Haz click")
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // This is called when a double tap is detected.
            // Handle double tap action here.
            playGame.verificarEvento("Haz doble click")
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            // This is called when a long press is detected.
            // Handle long press action here.
            playGame.verificarEvento("Manten Presionado")
        }
    }
}
