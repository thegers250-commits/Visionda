package com.visualonda.sensory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.visualonda.sensory.domain.repository.IAudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioRepository: IAudioRepository
) : ViewModel() {
    
    fun initialize() {
        audioRepository.initialize()
    }
    
    override fun onCleared() {
        super.onCleared()
        audioRepository.cleanup()
    }
}
