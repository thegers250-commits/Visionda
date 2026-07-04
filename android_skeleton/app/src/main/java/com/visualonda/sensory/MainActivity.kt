package com.visualonda.sensory

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val TAG = "VisualondaApp"
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var tvStatus: TextView
    private var cameraProvider: ProcessCameraProvider? = null

    // ─── JNI functions ──────────────────────────────────────────────────
    external fun pdInit()
    external fun pdOpenPatch(path: String)
    external fun pdSendFloat(name: String, value: Float)
    external fun audioEngineInit()
    external fun audioEngineCleanup()
    external fun audioEngineGetLatency(): Int
    external fun sendControlJson(json: String)

    companion object {
        init { System.loadLibrary("native-lib") }
        private const val REQ_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    // ─── Permission launcher ────────────────────────────────────────────
    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.all { it.value }) {
            log("✅ Permisos otorgados")
        } else {
            log("❌ Permisos denegados — la app requiere cámara y audio")
        }
    }

    // ────────────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        buildUI()
        requestPermissionsIfNeeded()
    }

    // ─── UI programática ────────────────────────────────────────────────
    private fun buildUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 48, 24, 24)
        }

        val title = TextView(this).apply {
            text = "VISUALONDA"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }
        root.addView(title)

        // Botón 1: Init Audio Engine (AAudio)
        root.addView(makeButton("🔊 Init Audio Engine") {
            log("Iniciando audio engine...")
            audioEngineInit()
            val latency = audioEngineGetLatency()
            log("✅ Audio engine listo — latencia ~${latency}ms")
        })

        // Botón 2: Init LibPD + cargar patch
        root.addView(makeButton("🎵 Init LibPD + Patch") {
            log("Inicializando LibPD...")
            pdInit()
            // Copiar patch de assets a filesDir si no existe
            val patchFile = File(filesDir, "light_material_patch.pd")
            if (!patchFile.exists()) {
                try {
                    assets.open("patches/light_material_patch.pd")
                        .use { inp -> patchFile.outputStream().use { inp.copyTo(it) } }
                    log("✅ Patch copiado a ${patchFile.absolutePath}")
                } catch (e: Exception) {
                    log("⚠️ Patch no encontrado en assets: ${e.message}")
                }
            }
            pdOpenPatch(patchFile.absolutePath)
            log("✅ LibPD + Patch listos")
        })

        // Botón 3: Start Camera
        root.addView(makeButton("📷 Start Camera") {
            if (hasPermissions()) {
                log("Iniciando cámara...")
                startCamera()
            } else {
                log("⚠️ Necesitas otorgar permisos primero")
                requestPermissionsIfNeeded()
            }
        })

        // Botón 4: Stop All
        root.addView(makeButton("⏹ Stop All") {
            cameraProvider?.unbindAll()
            audioEngineCleanup()
            log("Detenido todo")
        })

        // Área de log
        tvStatus = TextView(this).apply {
            text = "Listo.\n"
            textSize = 11f
            setPadding(8, 8, 8, 8)
            setBackgroundColor(0xFFF5F5F5.toInt())
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

    private fun makeButton(label: String, onClick: () -> Unit) =
        Button(this).apply {
            text = label
            setOnClickListener {
                try { onClick() }
                catch (e: Exception) { log("❌ Error: ${e.message}") }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 4, 0, 4) }
        }

    // ─── Cámara ─────────────────────────────────────────────────────────
    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            cameraProvider = future.get()
            bindAnalysis(cameraProvider!!)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindAnalysis(provider: ProcessCameraProvider) {
        val analysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(320, 240))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also { it.setAnalyzer(cameraExecutor, ::analyzeFrame) }

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                analysis
            )
            log("✅ Cámara activa @ 320×240")
        } catch (e: Exception) {
            log("❌ Error al iniciar cámara: ${e.message}")
        }
    }

    // ─── Procesamiento de frame → JSON → C++ ────────────────────────────
    private fun analyzeFrame(image: ImageProxy) {
        val plane = image.planes[0]          // Canal Y (luminancia)
        val buf   = plane.buffer
        val W     = image.width
        val H     = image.height
        val rowStride = plane.rowStride
        val pixStride = plane.pixelStride

        // Grid 8×8 — cada celda representa una región del campo visual
        val COLS = 8; val ROWS = 8
        val cw = W / COLS; val ch = H / ROWS

        val cells = StringBuilder()
        var first = true

        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                // Luminancia promedio de la celda
                var sum = 0L
                for (dy in 0 until ch) {
                    for (dx in 0 until cw) {
                        val idx = (row * ch + dy) * rowStride + (col * cw + dx) * pixStride
                        if (idx < buf.capacity()) {
                            sum += (buf.get(idx).toInt() and 0xFF)
                        }
                    }
                }
                val lum = sum.toFloat() / (cw * ch * 255f)

                // Mapeo geométrico
                val azimuth  = ((col - COLS / 2f) / (COLS / 2f)) * 90f
                val elevation = ((ROWS / 2f - row) / (ROWS / 2f)) * 2.0f
                val distance  = 1.0f + (1f - lum) * 4f  // lum alto → cerca
                val material  = if (lum > 0.7f) "metal" else if (lum > 0.4f) "wood" else "fabric"

                if (!first) cells.append(',')
                first = false
                cells.append("""{"id":${row * COLS + col},"azimuth_deg":$azimuth,"elevation_m":$elevation,"distance_m":$distance,"luminance":$lum,"material":"$material","confidence":0.9}""")
            }
        }

        val json = """{"timestamp_ms":${System.currentTimeMillis()},"grid":{"rows":$ROWS,"cols":$COLS},"cells":[$cells]}"""
        sendControlJson(json)
        image.close()
    }

    // ─── Permisos ────────────────────────────────────────────────────────
    private fun hasPermissions() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionsIfNeeded() {
        if (!hasPermissions()) permLauncher.launch(REQUIRED_PERMISSIONS)
    }

    // ─── Log helper ──────────────────────────────────────────────────────
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
