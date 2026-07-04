package com.visualonda.sensory.data.repository

import com.visualonda.sensory.domain.model.AudioParameters
import com.visualonda.sensory.domain.repository.IAudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor() : IAudioRepository {
    
    override fun initialize(): Boolean {
        // TODO: Implementar en Semana 2
        return true
    }
    
    override fun playAudio(params: AudioParameters) {
        // TODO: Implementar en Semana 2
    }
    
    override fun stopAudio() {
        // TODO: Implementar en Semana 2
    }
    
    override fun cleanup() {
        // TODO: Implementar en Semana 2
    }
}
