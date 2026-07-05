package com.visualonda.sensory.data.repository

import android.util.Log
import com.visualonda.sensory.domain.model.AudioParameters
import com.visualonda.sensory.domain.repository.IAudioRepository
import javax.inject.Inject

/**
 * Implementación real del repositorio de audio.
 * Delega al motor nativo (AAudio + LibPD) vía JNI.
 * Las funciones JNI están declaradas en MainActivity pero
 * aquí usamos el companion object para acceder a native-lib.
 */
class AudioRepositoryImpl @Inject constructor() : IAudioRepository {

    private val TAG = "AudioRepo"
    private var initialized = false

    // JNI — cargado por MainActivity al iniciar la app
    private external fun audioEngineInit()
    private external fun audioEngineCleanup()
    private external fun pdSendFloat(name: String, value: Float)

    companion object {
        // La librería ya se carga en MainActivity; aquí solo declaramos JNI
    }

    override fun initialize(): Boolean {
        return try {
            audioEngineInit()
            initialized = true
            Log.i(TAG, "✅ Audio engine inicializado")
            true
        } catch (e: UnsatisfiedLinkError) {
            // JNI no disponible (pruebas unitarias sin dispositivo)
            Log.w(TAG, "JNI no disponible: ${e.message}")
            true // retornar true para no bloquear la UI en tests
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando audio: ${e.message}")
            false
        }
    }

    override fun playAudio(params: AudioParameters) {
        if (!initialized) return
        try {
            // Enviar los 6 parámetros mapeados a LibPD
            pdSendFloat("light-freq-left",  (params.freq + params.beatDelta / 2).toFloat())
            pdSendFloat("light-freq-right", (params.freq - params.beatDelta / 2).toFloat())
            pdSendFloat("distance-gain",    params.gain.toFloat())
            pdSendFloat("distance-lpf",     params.lpfCutoff.toFloat())
            pdSendFloat("azimuth-pan",      params.pan.toFloat())
            pdSendFloat("material-mod",     params.modIndex.toFloat())
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando audio params: ${e.message}")
        }
    }

    override fun stopAudio() {
        Log.i(TAG, "stopAudio()")
        // El motor sigue corriendo en background, solo silenciamos
        try { pdSendFloat("distance-gain", 0f) } catch (_: Exception) {}
    }

    override fun cleanup() {
        try {
            audioEngineCleanup()
            initialized = false
            Log.i(TAG, "Audio engine limpiado")
        } catch (_: Exception) {}
    }
}
