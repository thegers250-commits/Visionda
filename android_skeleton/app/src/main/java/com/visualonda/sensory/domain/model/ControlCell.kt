package com.visualonda.sensory.domain.model

data class ControlCell(
    val id: Int,
    val row: Int,
    val col: Int,
    val azimuthDeg: Float,
    val elevationM: Float,
    val distanceM: Float,
    val material: String,
    val luminance: Float,
    val confidence: Float
)
