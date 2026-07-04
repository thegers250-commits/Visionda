package com.visualonda.sensory.domain.model

data class ControlFrame(
    val timestampMs: Long,
    val frameRateHz: Int,
    val cells: List<ControlCell>
)
