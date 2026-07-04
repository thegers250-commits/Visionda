package com.visualonda.sensory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.visualonda.sensory.domain.repository.ICameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: ICameraRepository
) : ViewModel() {
    
    fun startCapture() {
        // TODO: Implementar en Semana 3
    }
}
