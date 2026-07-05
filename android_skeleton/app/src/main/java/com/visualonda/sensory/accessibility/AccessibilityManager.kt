package com.visualonda.sensory.accessibility

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Fase 2 — Accesibilidad
 * Centraliza TTS (Text-to-Speech) y vibración háptica.
 * Diseñado para usuarios ciegos: toda interacción tiene feedback auditivo y táctil.
 */
class AccessibilityManager(private val context: Context) {

    private val TAG = "A11yManager"
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    // Vibrator API — compatible Android 8+
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // ─── Inicialización ──────────────────────────────────────────────────────
    fun init(onReady: () -> Unit = {}) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "ES"))
                ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                           result != TextToSpeech.LANG_NOT_SUPPORTED
                if (!ttsReady) {
                    // Fallback a inglés si español no disponible
                    tts?.setLanguage(Locale.ENGLISH)
                    ttsReady = true
                }
                Log.i(TAG, "TTS listo")
                onReady()
            } else {
                Log.e(TAG, "TTS falló al inicializar (status=$status)")
            }
        }
    }

    // ─── Texto a voz ─────────────────────────────────────────────────────────

    /** Hablar inmediatamente (interrumpe lo que está diciendo) */
    fun speak(text: String) {
        if (!ttsReady) { Log.w(TAG, "TTS no listo para: $text"); return }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vda_${System.currentTimeMillis()}")
    }

    /** Hablar en cola (espera a que termine el anterior) */
    fun speakQueue(text: String) {
        if (!ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "vda_${System.currentTimeMillis()}")
    }

    /** Silenciar TTS */
    fun stopSpeaking() {
        tts?.stop()
    }

    // ─── Haptics ─────────────────────────────────────────────────────────────

    /** Pulso corto de confirmación */
    fun vibrateConfirm() = vibrate(50L)

    /** Pulso de advertencia (2 pulsos) */
    fun vibrateWarning() = vibratePattern(longArrayOf(0, 80, 60, 80))

    /** Pulso de error (3 pulsos) */
    fun vibrateError() = vibratePattern(longArrayOf(0, 100, 50, 100, 50, 100))

    /** Vibración de proximidad — intensidad según distancia (0=lejos, 1=cerca) */
    fun vibrateProximity(proximity: Float) {
        // proximity 0–1: más cerca = vibración más larga
        val duration = (20 + proximity * 180).toLong()
        vibrate(duration)
    }

    /** Patrón de vibración de navegación direccional */
    fun vibrateDirection(direction: Direction) {
        when (direction) {
            Direction.LEFT   -> vibratePattern(longArrayOf(0, 30, 30, 30))
            Direction.RIGHT  -> vibratePattern(longArrayOf(0, 30, 30, 30, 30, 30))
            Direction.UP     -> vibratePattern(longArrayOf(0, 60))
            Direction.DOWN   -> vibratePattern(longArrayOf(0, 30, 30, 60))
            Direction.CENTER -> vibrate(100L)
        }
    }

    private fun vibrate(ms: Long) {
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ms)
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    // ─── Mensajes de contexto para usuarios ciegos ───────────────────────────

    fun announceAppStart() {
        speak("Visualonda iniciado. Doble toque para activar cámara. Desliza para ajustar volumen.")
    }

    fun announceObjectDetected(description: String, distance: Float) {
        val dist = when {
            distance < 0.5f -> "muy cerca"
            distance < 1.5f -> "cerca"
            distance < 3.0f -> "a media distancia"
            else            -> "lejos"
        }
        speakQueue("$description, $dist")
    }

    fun announceDirection(azimuthDeg: Float) {
        val dir = when {
            azimuthDeg < -60 -> "a tu izquierda"
            azimuthDeg < -20 -> "ligeramente a la izquierda"
            azimuthDeg <  20 -> "al frente"
            azimuthDeg <  60 -> "ligeramente a la derecha"
            else             -> "a tu derecha"
        }
        speakQueue(dir)
    }

    fun announceAudioMode(mode: AudioMode) {
        speak(when (mode) {
            AudioMode.SPATIAL    -> "Modo espacial activado"
            AudioMode.PROXIMITY  -> "Modo proximidad activado"
            AudioMode.NAVIGATION -> "Modo navegación activado"
            AudioMode.SILENT     -> "Silencio"
        })
    }

    // ─── Cleanup ─────────────────────────────────────────────────────────────
    fun cleanup() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ttsReady = false
    }

    // ─── Enumeraciones ───────────────────────────────────────────────────────
    enum class Direction { LEFT, RIGHT, UP, DOWN, CENTER }
    enum class AudioMode  { SPATIAL, PROXIMITY, NAVIGATION, SILENT }
}
