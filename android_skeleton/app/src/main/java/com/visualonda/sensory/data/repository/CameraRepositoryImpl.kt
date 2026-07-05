package com.visualonda.sensory.data.repository

import android.util.Log
import com.visualonda.sensory.domain.model.ControlCell
import com.visualonda.sensory.domain.model.ControlFrame
import com.visualonda.sensory.domain.repository.ICameraRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

/**
 * Implementación del repositorio de cámara.
 * La captura real de frames ocurre en MainActivity (que tiene el lifecycle).
 * Este repositorio expone un Flow de ControlFrames que puede
 * suscribirse desde CameraViewModel.
 *
 * La generación del ControlFrame se realiza en analyzeFrame() de
 * MainActivity y se envía directamente a native-lib vía sendControlJson().
 */
class CameraRepositoryImpl @Inject constructor() : ICameraRepository {

    private val TAG = "CameraRepo"
    private var capturing = false

    /**
     * En la arquitectura actual el pipeline camera→JSON→C++ es manejado
     * directamente en MainActivity para minimizar latencia.
     * Este Flow se puede usar en futuras versiones para separar capas.
     */
    override fun startCapture(): Flow<ControlFrame> {
        if (capturing) return emptyFlow()
        capturing = true
        Log.i(TAG, "startCapture() — pipeline activo en MainActivity")
        // El pipeline real está en MainActivity.analyzeFrame()
        // Este flow puede usarse para observar frames desde la capa domain
        return callbackFlow {
            // Listeners para frames se registran en MainActivity
            awaitClose {
                capturing = false
                Log.i(TAG, "Flow cerrado")
            }
        }
    }

    override fun stopCapture() {
        capturing = false
        Log.i(TAG, "stopCapture()")
    }

    /**
     * Convierte los valores de luminancia de una imagen YUV a una lista de ControlCells.
     * Usado cuando se quiere generar el ControlFrame desde Kotlin
     * en lugar de en la JNI layer.
     */
    fun buildControlFrame(
        yData: ByteArray,
        width: Int,
        height: Int,
        rowStride: Int,
        pixStride: Int,
        gridCols: Int = 8,
        gridRows: Int = 8
    ): ControlFrame {
        val cells = mutableListOf<ControlCell>()
        val cw = width / gridCols
        val ch = height / gridRows

        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                var sum = 0L
                for (dy in 0 until ch) {
                    for (dx in 0 until cw) {
                        val idx = (row * ch + dy) * rowStride + (col * cw + dx) * pixStride
                        if (idx < yData.size) sum += (yData[idx].toInt() and 0xFF)
                    }
                }
                val lum = sum.toFloat() / (cw * ch * 255f)
                val az  = ((col - gridCols / 2f) / (gridCols / 2f)) * 90f
                val elev = ((gridRows / 2f - row) / (gridRows / 2f)) * 2f
                val dist = 1f + (1f - lum) * 4f
                val mat  = when {
                    lum > 0.7f -> "metal"
                    lum > 0.4f -> "wood"
                    else       -> "fabric"
                }
                cells.add(
                    ControlCell(
                        id          = row * gridCols + col,
                        row         = row,
                        col         = col,
                        azimuthDeg  = az,
                        elevationM  = elev,
                        distanceM   = dist,
                        material    = mat,
                        luminance   = lum,
                        confidence  = 0.9f
                    )
                )
            }
        }

        return ControlFrame(
            timestampMs = System.currentTimeMillis(),
            frameRateHz = 30,
            cells       = cells
        )
    }
}
