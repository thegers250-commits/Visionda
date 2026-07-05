package com.visualonda.sensory.accessibility

import android.util.Log

/**
 * Fase 2 — Controlador de modos de audio
 *
 * MODOS:
 *   SPATIAL    → Audio 3D espacial completo (modo principal)
 *   PROXIMITY  → Solo feedback de proximidad (objetos cercanos)
 *   NAVIGATION → Énfasis en dirección (izquierda/derecha)
 *   SILENT     → Sin audio (emergencia)
 *
 * El usuario cambia modo con swipe arriba.
 */
class AudioModeController {

    private val TAG = "AudioModeCtrl"

    private val modes = AccessibilityManager.AudioMode.values()
    private var currentIndex = 0

    val currentMode get() = modes[currentIndex]

    /** Pasar al siguiente modo (cíclico) */
    fun nextMode(): AccessibilityManager.AudioMode {
        currentIndex = (currentIndex + 1) % modes.size
        Log.i(TAG, "Modo cambiado a: ${currentMode.name}")
        return currentMode
    }

    /** Aplicar filtro de parámetros según modo activo */
    fun applyMode(
        rawFreq: Double,
        rawGain: Double,
        rawPan: Double,
        rawLpf: Double,
        distance: Double
    ): AudioModeParams {
        return when (currentMode) {
            AccessibilityManager.AudioMode.SPATIAL -> AudioModeParams(
                freq   = rawFreq,
                gain   = rawGain,
                pan    = rawPan,
                lpf    = rawLpf,
                active = true
            )
            AccessibilityManager.AudioMode.PROXIMITY -> AudioModeParams(
                freq   = 1000.0,                    // tono fijo
                gain   = rawGain * proximityBoost(distance),
                pan    = 0.5,                       // centro
                lpf    = rawLpf,
                active = distance < 2.0             // solo si está cerca
            )
            AccessibilityManager.AudioMode.NAVIGATION -> AudioModeParams(
                freq   = rawFreq,
                gain   = rawGain * 0.8,
                pan    = rawPan,                    // pan exagerado
                lpf    = 8000.0,                    // sin mucho filtro
                active = true
            )
            AccessibilityManager.AudioMode.SILENT -> AudioModeParams(
                freq = 0.0, gain = 0.0, pan = 0.5, lpf = 0.0, active = false
            )
        }
    }

    private fun proximityBoost(distance: Double): Double {
        // Boost cuando objeto muy cercano (<1m) para alertar al usuario
        return if (distance < 0.5) 2.0 else if (distance < 1.0) 1.5 else 1.0
    }

    data class AudioModeParams(
        val freq: Double,
        val gain: Double,
        val pan: Double,
        val lpf: Double,
        val active: Boolean
    )
}
