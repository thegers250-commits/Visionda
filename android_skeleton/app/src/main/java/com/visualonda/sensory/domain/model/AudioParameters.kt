package com.visualonda.sensory.domain.model

/**
 * Parámetros de audio calculados por los 6 mapeos matemáticos
 * a partir de un ControlCell (región de la cámara).
 */
data class AudioParameters(
    val freq: Double,           // Hz  — elevación → frecuencia
    val gain: Double,           // 0–1 — distancia → ganancia
    val lpfCutoff: Double,      // Hz  — distancia → LPF
    val pan: Double = 0.5,      // 0–1 — azimut   → paneo estéreo
    val beatDelta: Double = 5.0,// Hz  — luminancia → modulación
    val modIndex: Double = 1.0, // —   — material  → timbre
    val material: String = "wood"
)
