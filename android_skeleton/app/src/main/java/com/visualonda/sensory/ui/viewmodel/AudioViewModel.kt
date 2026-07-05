package com.visualonda.sensory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visualonda.sensory.domain.model.AudioParameters
import com.visualonda.sensory.domain.repository.IAudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioRepository: IAudioRepository
) : ViewModel() {

    // Estado observable para la UI
    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    private val _latencyMs = MutableLiveData(0)
    val latencyMs: LiveData<Int> = _latencyMs

    private val _errorMsg = MutableLiveData<String?>(null)
    val errorMsg: LiveData<String?> = _errorMsg

    fun initialize() {
        viewModelScope.launch {
            val ok = audioRepository.initialize()
            _isRunning.postValue(ok)
            if (!ok) _errorMsg.postValue("Audio engine no pudo inicializar")
        }
    }

    fun setParameters(params: AudioParameters) {
        viewModelScope.launch {
            audioRepository.playAudio(params)
        }
    }

    fun stop() {
        viewModelScope.launch {
            audioRepository.stopAudio()
            _isRunning.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRepository.cleanup()
    }
}
