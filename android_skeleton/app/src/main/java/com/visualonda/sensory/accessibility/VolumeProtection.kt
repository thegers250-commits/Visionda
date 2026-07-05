package com.visualonda.sensory.accessibility

import android.util.Log

/**
 * Fase 2 — Protección de volumen (seguridad auditiva)
 *
 * Basado en recomendaciones WHO/NIOSH:
 *   - Máximo 85 dB SPL para uso prolongado
 *   - Límite absoluto de sesión: 2 horas continuas
 *   - Avisar al usuario si lleva >30 min a volumen alto
 *
 * En la práctica limitamos la amplitud máxima que enviamos
 * al audio engine y llevamos un contador de tiempo.
 */
class VolumeProtection {

    private val TAG = "VolumeProtection"

    // Límites de amplitud
    private val MAX_AMPLITUDE     = 0.45f   // ~85 dB relativo
    private val WARNING_AMPLITUDE = 0.35f   // umbral para advertir
    private val DEFAULT_AMPLITUDE = 0.15f

    // Seguimiento de tiempo
    private var sessionStartMs   = 0L
    private var highVolumeStartMs = 0L
    private var isHighVolume     = false

    private val WARNING_DURATION_MS  = 30 * 60 * 1000L  // 30 min
    private val MAX_SESSION_MS       = 2 * 60 * 60 * 1000L  // 2 horas

    fun startSession() {
        sessionStartMs = System.currentTimeMillis()
        Log.i(TAG, "Sesión de audio iniciada")
    }

    /**
     * Aplica límites de seguridad a la amplitud.
     * @param requestedAmp amplitud solicitada (0–1)
     * @return amplitud segura + flag de advertencia
     */
    fun safeAmplitude(requestedAmp: Float): SafeAmplitudeResult {
        val clamped = requestedAmp.coerceIn(0f, MAX_AMPLITUDE)
        val now = System.currentTimeMillis()

        // Trackear tiempo en volumen alto
        if (clamped > WARNING_AMPLITUDE) {
            if (!isHighVolume) {
                isHighVolume = true
                highVolumeStartMs = now
            }
        } else {
            isHighVolume = false
        }

        // Comprobar tiempo en volumen alto
        val highVolumeDuration = if (isHighVolume) now - highVolumeStartMs else 0L
        val sessionDuration    = if (sessionStartMs > 0) now - sessionStartMs else 0L

        val warning = when {
            sessionDuration > MAX_SESSION_MS    -> WarningLevel.SESSION_LIMIT
            highVolumeDuration > WARNING_DURATION_MS -> WarningLevel.HIGH_VOLUME
            else -> WarningLevel.NONE
        }

        return SafeAmplitudeResult(amplitude = clamped, warning = warning)
    }

    /** Reducir amplitud por tiempo de exposición (fade automático) */
    fun getExposureFactor(): Float {
        if (sessionStartMs == 0L) return 1.0f
        val sessionMinutes = (System.currentTimeMillis() - sessionStartMs) / 60_000f
        return when {
            sessionMinutes > 120 -> 0.5f   // 2h+: reducir a la mitad
            sessionMinutes > 60  -> 0.75f  // 1h+: reducir 25%
            else                 -> 1.0f
        }
    }

    fun endSession() {
        sessionStartMs = 0L
        isHighVolume = false
        Log.i(TAG, "Sesión de audio terminada")
    }

    data class SafeAmplitudeResult(
        val amplitude: Float,
        val warning: WarningLevel
    )

    enum class WarningLevel { NONE, HIGH_VOLUME, SESSION_LIMIT }
}
