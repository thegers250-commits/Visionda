package com.visualonda.sensory.domain.repository

import com.visualonda.sensory.domain.model.AudioParameters

interface IAudioRepository {
    fun initialize(): Boolean
    fun playAudio(params: AudioParameters)
    fun stopAudio()
    fun cleanup()
}
