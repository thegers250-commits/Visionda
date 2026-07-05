package com.visualonda.sensory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.visualonda.sensory.accessibility.AccessibilityManager
import com.visualonda.sensory.accessibility.AudioModeController
import com.visualonda.sensory.accessibility.VolumeProtection

/**
 * Fase 2 — ViewModel de accesibilidad
 * Coordina TTS, haptics, modos de audio y protección de volumen.
 */
class AccessibilityViewModel(app: Application) : AndroidViewModel(app) {

    val accessibilityManager = AccessibilityManager(app)
    val audioModeController  = AudioModeController()
    val volumeProtection     = VolumeProtection()

    private val _currentMode = MutableLiveData(AudioModeController().currentMode)
    val currentMode: LiveData<AccessibilityManager.AudioMode> = _currentMode

    private val _volumeWarning = MutableLiveData(VolumeProtection.WarningLevel.NONE)
    val volumeWarning: LiveData<VolumeProtection.WarningLevel> = _volumeWarning

    private var volume = 0.15f  // 0–1

    // ─── Init ────────────────────────────────────────────────────────────────
    fun init() {
        accessibilityManager.init {
            volumeProtection.startSession()
            accessibilityManager.announceAppStart()
        }
    }

    // ─── Acciones desde gestos ────────────────────────────────────────────────
    fun onDoubleTap() {
        accessibilityManager.vibrateConfirm()
        // MainActivity escucha este evento para toggle cámara
    }

    fun onSwipeRight() {
        volume = (volume + 0.1f).coerceAtMost(1f)
        val safe = volumeProtection.safeAmplitude(volume)
        _volumeWarning.postValue(safe.warning)
        accessibilityManager.vibrateConfirm()
        if (safe.warning != VolumeProtection.WarningLevel.NONE) {
            accessibilityManager.speak("Volumen alto. Cuida tus oídos.")
        } else {
            accessibilityManager.speak("Volumen ${(volume * 100).toInt()}%")
        }
    }

    fun onSwipeLeft() {
        volume = (volume - 0.1f).coerceAtLeast(0f)
        accessibilityManager.vibrateConfirm()
        accessibilityManager.speak("Volumen ${(volume * 100).toInt()}%")
    }

    fun onSwipeUp() {
        val newMode = audioModeController.nextMode()
        _currentMode.postValue(newMode)
        accessibilityManager.vibrateConfirm()
        accessibilityManager.announceAudioMode(newMode)
    }

    fun onSwipeDown() {
        accessibilityManager.vibrateWarning()
        accessibilityManager.speak("Deteniendo")
    }

    fun onLongPress() {
        accessibilityManager.vibrateConfirm()
        accessibilityManager.speak("Describiendo escena")
    }

    fun onTwoFingerTap() {
        accessibilityManager.vibrateConfirm()
        accessibilityManager.speak("Pausa")
    }

    // ─── Feedback de escena ──────────────────────────────────────────────────
    fun announceScene(distance: Float, azimuth: Float, luminance: Float) {
        val brightness = when {
            luminance > 0.7f -> "ambiente brillante"
            luminance > 0.4f -> "luz moderada"
            else             -> "poca luz"
        }
        accessibilityManager.vibrateProximity(1f - (distance / 5f).coerceIn(0f, 1f))
        accessibilityManager.announceDirection(azimuth)
        // No verbalizamos todo el tiempo para no saturar al usuario
    }

    fun getCurrentVolume() = volume

    // ─── Cleanup ─────────────────────────────────────────────────────────────
    override fun onCleared() {
        super.onCleared()
        volumeProtection.endSession()
        accessibilityManager.cleanup()
    }
}
