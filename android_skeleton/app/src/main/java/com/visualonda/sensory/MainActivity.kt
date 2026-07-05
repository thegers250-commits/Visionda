package com.visualonda.sensory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.visualonda.sensory.accessibility.GestureHandler
import com.visualonda.sensory.ui.viewmodel.AccessibilityViewModel
import com.visualonda.sensory.ui.viewmodel.AudioViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "VisualondaApp"

    // ViewModels
    private val audioViewModel: AudioViewModel by viewModels()
    private val a11yViewModel: AccessibilityViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccessibilityViewModel(application) as T
            }
        }
    }

    // Camera
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraActive = false

    // Gesture handler (Fase 2)
    private lateinit var gestureHandler: GestureHandler
    private lateinit var rootView: View

    // UI
    private lateinit var tvStatus: TextView
    private lateinit var btnCamera: Button

    // ─── JNI ───────────────────────────────────────────────────────────────
    external fun pdInit()
    external fun pdOpenPatch(path: String)
    external fun pdSendFloat(name: String, value: Float)
    external fun audioEngineInit()
    external fun audioEngineCleanup()
    external fun audioEngineGetLatency(): Int
    external fun sendControlJson(json: String)

    companion object {
        init { System.loadLibrary("native-lib") }
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.all { it.value }) {
            log("✅ Permisos otorgados")
            a11yViewModel.accessibilityManager.speak("Permisos concedidos")
        } else {
            log("❌ Permisos denegados")
            a11yViewModel.accessibilityManager.speak("Permisos denegados. La app necesita cámara y audio.")
        }
    }

    // ─── onCreate ─────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        buildUI()
        setupGestures()
        initAudio()

        if (!hasPermissions()) permLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // ─── UI ────────────────────────────────────────────────────────────────
    private fun buildUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 48, 24, 24)
            setBackgroundColor(0xFF121212.toInt())  // fondo oscuro (accesible)
            isFocusable = true
            isClickable = true
        }
        rootView = root

        val title = TextView(this).apply {
            text = "VISUALONDA"
            textSize = 24f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 0, 0, 8)
            contentDescription = "Visualonda — aplicación de navegación sonora para personas ciegas"
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Navegación sonora para personas ciegas"
            textSize = 13f
            setTextColor(0xFFAAAAAA.toInt())
            setPadding(0, 0, 0, 24)
        }
        root.addView(subtitle)

        // Botón audio
        root.addView(makeButton("🔊 Iniciar Audio", "Inicia el motor de audio") {
            initAudio()
        })

        // Botón cámara (toggle)
        btnCamera = makeButton("📷 Activar Cámara", "Activa o desactiva la cámara de navegación") {
            toggleCamera()
        }
        root.addView(btnCamera)

        // Botón modo
        root.addView(makeButton("🔁 Cambiar Modo", "Cambia el modo de audio: espacial, proximidad, navegación") {
            a11yViewModel.onSwipeUp()
        })

        // Instrucciones accesibles
        val instructions = TextView(this).apply {
            text = "Gestos:\n• Doble toque → cámara ON/OFF\n• Deslizar → subir/bajar volumen\n• Deslizar arriba → cambiar modo\n• Toque largo → describir escena"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 16, 0, 8)
        }
        root.addView(instructions)

        // Área de log
        tvStatus = TextView(this).apply {
            text = "Iniciando...\n"
            textSize = 11f
            setTextColor(0xFFCCCCCC.toInt())
            setPadding(8, 8, 8, 8)
            setBackgroundColor(0xFF1E1E1E.toInt())
        }
        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        scroll.addView(tvStatus)
        root.addView(scroll)

        setContentView(root)
    }

    private fun makeButton(label: String, description: String, onClick: () -> Unit) =
        Button(this).apply {
            text = label
            contentDescription = description
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF2979FF.toInt())
            setOnClickListener {
                try { onClick() }
                catch (e: Exception) { log("❌ ${e.message}") }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 6, 0, 6) }
        }

    // ─── Gestos (Fase 2) ──────────────────────────────────────────────────
    private fun setupGestures() {
        gestureHandler = GestureHandler(this, object : GestureHandler.GestureListener {
            override fun onDoubleTap()     { toggleCamera(); a11yViewModel.onDoubleTap() }
            override fun onSwipeRight()    { a11yViewModel.onSwipeRight() }
            override fun onSwipeLeft()     { a11yViewModel.onSwipeLeft() }
            override fun onSwipeUp()       { a11yViewModel.onSwipeUp() }
            override fun onSwipeDown()     { stopAll(); a11yViewModel.onSwipeDown() }
            override fun onLongPress()     { a11yViewModel.onLongPress() }
            override fun onTwoFingerTap()  { toggleCamera(); a11yViewModel.onTwoFingerTap() }
        })
        // Adjuntar gestos al rootView después de que se cree
        window.decorView.post {
            gestureHandler.attachTo(rootView)
        }
    }

    // ─── Audio ────────────────────────────────────────────────────────────
    private fun initAudio() {
        log("Iniciando audio engine...")
        audioEngineInit()
        pdInit()
        copyAndLoadPatch()
        a11yViewModel.init()
        val latency = audioEngineGetLatency()
        log("✅ Audio listo — latencia ~${latency}ms")
        audioViewModel.initialize()
    }

    private fun copyAndLoadPatch() {
        val patchFile = File(filesDir, "light_material_patch.pd")
        if (!patchFile.exists()) {
            try {
                assets.open("patches/light_material_patch.pd")
                    .use { it.copyTo(patchFile.outputStream()) }
            } catch (e: Exception) {
                log("⚠️ Patch no encontrado: ${e.message}")
                return
            }
        }
        pdOpenPatch(patchFile.absolutePath)
        log("✅ Patch LibPD cargado")
    }

    // ─── Cámara ───────────────────────────────────────────────────────────
    private fun toggleCamera() {
        if (cameraActive) {
            stopCamera()
        } else {
            if (hasPermissions()) startCamera()
            else permLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            cameraProvider = future.get()
            bindAnalysis(cameraProvider!!)
            cameraActive = true
            btnCamera.text = "⏹ Detener Cámara"
            log("✅ Cámara activa")
            a11yViewModel.accessibilityManager.speak("Cámara activada")
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraActive = false
        btnCamera.text = "📷 Activar Cámara"
        log("Cámara detenida")
        a11yViewModel.accessibilityManager.speak("Cámara desactivada")
    }

    private fun bindAnalysis(provider: ProcessCameraProvider) {
        val analysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(320, 240))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also { it.setAnalyzer(cameraExecutor, ::analyzeFrame) }

        provider.unbindAll()
        provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, analysis)
    }

    // ─── Análisis de frame ────────────────────────────────────────────────
    private fun analyzeFrame(image: ImageProxy) {
        val plane     = image.planes[0]
        val buf       = plane.buffer
        val W         = image.width
        val H         = image.height
        val rowStride = plane.rowStride
        val pixStride = plane.pixelStride

        val COLS = 8; val ROWS = 8
        val cw = W / COLS;  val ch = H / ROWS

        val cells = StringBuilder()
        var first = true

        // Variables para el feedback de accesibilidad (celda central)
        var centerLum  = 0.5f
        var centerDist = 2.0f
        var centerAz   = 0f

        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                var sum = 0L
                for (dy in 0 until ch) {
                    for (dx in 0 until cw) {
                        val idx = (row * ch + dy) * rowStride + (col * cw + dx) * pixStride
                        if (idx < buf.capacity()) sum += buf.get(idx).toInt() and 0xFF
                    }
                }
                val lum   = sum.toFloat() / (cw * ch * 255f)
                val az    = ((col - COLS / 2f) / (COLS / 2f)) * 90f
                val elev  = ((ROWS / 2f - row) / (ROWS / 2f)) * 2f
                val dist  = 1f + (1f - lum) * 4f
                val mat   = if (lum > 0.7f) "metal" else if (lum > 0.4f) "wood" else "fabric"

                // Capturar celda central para feedback a11y
                if (row == ROWS / 2 && col == COLS / 2) {
                    centerLum = lum; centerDist = dist; centerAz = az
                }

                // Aplicar modo de audio (Fase 2)
                val modeParams = a11yViewModel.audioModeController.applyMode(
                    rawFreq = 60.0 * Math.exp(1.7685 * elev.toDouble()),
                    rawGain = dist.toDouble(),
                    rawPan  = az.toDouble(),
                    rawLpf  = 12000.0,
                    distance = dist.toDouble()
                )
                val activeLum  = if (modeParams.active) lum else 0f
                val activeDist = if (modeParams.active) dist else 99f

                if (!first) cells.append(',')
                first = false
                cells.append("""{"id":${row * COLS + col},"azimuth_deg":${modeParams.pan},"elevation_m":$elev,"distance_m":$activeDist,"luminance":$activeLum,"material":"$mat","confidence":0.9}""")
            }
        }

        val json = """{"timestamp_ms":${System.currentTimeMillis()},"grid":{"rows":$ROWS,"cols":$COLS},"cells":[$cells]}"""

        // Aplicar protección de volumen
        val safeVol = a11yViewModel.volumeProtection.safeAmplitude(
            a11yViewModel.getCurrentVolume()
        )
        if (safeVol.warning == VolumeProtection.WarningLevel.SESSION_LIMIT) {
            runOnUiThread {
                a11yViewModel.accessibilityManager.speak("Llevas mucho tiempo escuchando. Toma un descanso.")
            }
        }

        // Feedback háptico de proximidad (max cada 500ms)
        if (centerDist < 1.0f) {
            a11yViewModel.accessibilityManager.vibrateProximity(1f - centerDist)
        }

        sendControlJson(json)
        image.close()
    }

    // ─── Stop All ─────────────────────────────────────────────────────────
    private fun stopAll() {
        stopCamera()
        audioEngineCleanup()
        log("Todo detenido")
    }

    // ─── Permisos ─────────────────────────────────────────────────────────
    private fun hasPermissions() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    // ─── Log ──────────────────────────────────────────────────────────────
    private fun log(msg: String) {
        Log.i(TAG, msg)
        runOnUiThread { tvStatus.append("$msg\n") }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        audioEngineCleanup()
    }
}
