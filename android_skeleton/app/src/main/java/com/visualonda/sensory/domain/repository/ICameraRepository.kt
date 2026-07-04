package com.visualonda.sensory.domain.repository

import com.visualonda.sensory.domain.model.ControlFrame
import kotlinx.coroutines.flow.Flow

interface ICameraRepository {
    fun startCapture(): Flow<ControlFrame>
    fun stopCapture()
}
