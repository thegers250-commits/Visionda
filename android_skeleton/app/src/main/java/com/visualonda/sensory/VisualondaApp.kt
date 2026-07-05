package com.visualonda.sensory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — requerida por Hilt DI
 * También es el punto de entrada para configuración global.
 */
@HiltAndroidApp
class VisualondaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Configuración global de la app
        // Los componentes individuales se inicializan en MainActivity
    }
}
