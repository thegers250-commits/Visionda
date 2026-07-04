package com.visualonda.sensory.data.repository

import com.visualonda.sensory.domain.model.ControlFrame
import com.visualonda.sensory.domain.repository.ICameraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor() : ICameraRepository {
    
    override fun startCapture(): Flow<ControlFrame> {
        // TODO: Implementar en Semana 3
        return emptyFlow()
    }
    
    override fun stopCapture() {
        // TODO: Implementar en Semana 3
    }
}
