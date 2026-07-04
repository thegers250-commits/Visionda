package com.visualonda.sensory.di

import com.visualonda.sensory.data.repository.AudioRepositoryImpl
import com.visualonda.sensory.data.repository.CameraRepositoryImpl
import com.visualonda.sensory.domain.repository.IAudioRepository
import com.visualonda.sensory.domain.repository.ICameraRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Singleton
    @Binds
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): IAudioRepository
    
    @Singleton
    @Binds
    abstract fun bindCameraRepository(impl: CameraRepositoryImpl): ICameraRepository
}
