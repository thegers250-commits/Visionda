# 🌉 FASE 0 → WEEK 1 BRIDGE: ARQUITECTURA & SETUP

**From Decision to Day 1 Coding (2-3 días)**

---

## DECISIÓN REQUERIDA: ¿QUÉ ROADMAP EJECUTAR?

### Preguntas Críticas HOY:

```
1. ¿Timeline?
   a) 6-8 semanas (MVP solo Audio+Camera)
   b) 14-16 semanas (Completo, recomendado, SIN Google Play)
   c) 18 semanas (Completo + Google Play)

2. ¿Presupuesto?
   a) $25K (Opción A)
   b) $30-40K (Opción B - recomendada)
   c) $40-50K (Opción C)

3. ¿Equipo?
   a) 2-3 devs
   b) 3.5-4 devs (recomendado)
   c) 4+ devs

RESPUESTA RECOMENDADA: OPCIÓN B (14-16 semanas, $30-40K, 3.5-4 FTE)
Razón: Producto completo, revolucionario, sin overhead Play Store
```

**Leer:** `DECISION_FINAL_ROADMAP.md` (10 min)

---

## CRONOGRAMA: ESTA SEMANA (FASE 0)

```
✓ COMPLETADO:
  ☑ Análisis de gaps (12,000 líneas faltantes)
  ☑ Arquitectura propuesta (Clean Architecture + MVVM)
  ☑ 4-phase roadmap documentado
  ☑ 32 markdown files creados

TODAY (Lunes):
  ☐ Decidir roadmap (Opción A, B, o C)
  ☐ Confirmar equipo & presupuesto
  ☐ Descargar libpd.so + headers (1 hora)
  ☐ Setup directorios NDK (30 min)

MAÑANA (Martes):
  ☐ Setup Hilt dependency injection (2 horas)
  ☐ Crear estructura de paquetes MVVM (3 horas)
  ☐ Compilar proyecto (1 hora)

MIÉRCOLES:
  ☐ Setup architecture base (2 horas)
  ☐ Test primera compilación (1 hora)
  ☐ Documentación interna (1 hora)
  ☐ GATE FASE 0: Project compila sin errores

JUEVES:
  ☐ Kick-off Fase 1 Week 1
  ☐ Audio engineer comienza libpd_wrapper.cpp
  ☐ Android engineer comienza UI setup
```

---

## PASO 1: SETUP INICIAL (Lunes - 2 horas)

### 1.1: Descargar LibPD (20 min)

```bash
# En tu PC:
# 1. Ir a https://github.com/libpd/libpd/releases
# 2. Descargar: libpd-0.12.x-android.zip
# 3. Extraer a: C:\temp\libpd-android

# Copiar a proyecto:
mkdir -p android_skeleton\app\src\main\jniLibs\arm64-v8a
mkdir -p android_skeleton\app\src\main\cpp\libpd\include

# De C:\temp\libpd-android\android\arm64-v8a\:
copy libpd.so → android_skeleton\app\src\main\jniLibs\arm64-v8a\

# De C:\temp\libpd-android\pure-data\src\:
copy libpd.h, pd.h, m_pd.h → android_skeleton\app\src\main\cpp\libpd\include\
```

### 1.2: Copiar Patch de PD (10 min)

```bash
mkdir -p android_skeleton\app\src\main\assets\patches
copy sensory-language\light_material_patch.pd → android_skeleton\app\src\main\assets\patches\
```

### 1.3: Verificar Setup (10 min)

```bash
# Verificar estructura:
dir android_skeleton\app\src\main\jniLibs\arm64-v8a\
# Debe mostrar: libpd.so (~1.2 MB)

dir android_skeleton\app\src\main\cpp\libpd\include\
# Debe mostrar: libpd.h, pd.h, m_pd.h

dir android_skeleton\app\src\main\assets\patches\
# Debe mostrar: light_material_patch.pd
```

**RESULTADO PASO 1:** Binarios y headers en lugar correcto ✓

---

## PASO 2: SETUP HILT & ARQUITECTURA (Martes - 5 horas)

### 2.1: Actualizar build.gradle (app-level) - 30 min

**REEMPLAZAR:** `android_skeleton\app\build.gradle`

```groovy
plugins {
    id 'com.android.application'
    id 'dagger.hilt.android.plugin'
    id 'kotlin-android'
    id 'kotlin-kapt'
}

android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.visualonda.sensory"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "0.1.0-alpha"
        
        externalNativeBuild {
            cmake {
                cppFlags "-std=c++17"
                arguments "-DANDROID_STL=c++_shared"
            }
        }
        
        ndk {
            abiFilters 'arm64-v8a'
        }
    }
    
    buildTypes {
        release {
            minifyEnabled false
        }
    }
    
    buildFeatures {
        viewBinding true
        dataBinding true
    }
    
    externalNativeBuild {
        cmake {
            path "CMakeLists.txt"
        }
    }
    
    namespace 'com.visualonda.sensory'
}

dependencies {
    // Android & Core
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // CameraX (Fase 1 Week 3-4)
    implementation 'androidx.camera:camera-core:1.2.3'
    implementation 'androidx.camera:camera-camera2:1.2.3'
    implementation 'androidx.camera:camera-lifecycle:1.2.3'
    
    // Kotlin & Coroutines
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.9.10'
    implementation 'androidx.lifecycle:lifecycle-runtime-kt:2.6.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // Jetpack Arch
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'
    
    // Hilt DI
    implementation 'com.google.dagger:hilt-android:2.48'
    kapt 'com.google.dagger:hilt-compiler:2.48'
    
    // Room Database (Fase 1 Week 3+)
    implementation 'androidx.room:room-runtime:2.6.0'
    kapt 'androidx.room:room-compiler:2.6.0'
    implementation 'androidx.room:room-ktx:2.6.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Logging
    implementation 'com.jakewharton.timber:timber:5.0.1'
    
    // Testing (Fase 1 Week 4+)
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:5.2.0'
    testImplementation 'org.mockito.kotlin:mockito-kotlin:5.1.0'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}


### 2.2: Actualizar build.gradle (project-level) - 15 min

**REEMPLAZAR:** `android_skeleton\build.gradle`

```groovy
plugins {
    id 'com.android.application' version '7.4.2' apply false
    id 'dagger.hilt.android' version '2.48' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.48'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

```

### 2.3: Crear Estructura de Paquetes - 1 hora

**Crear la siguiente estructura (vacía por ahora):**

```
app/src/main/java/com/visualonda/sensory/
│
├── MainActivity.kt (actualizar)
│
├── domain/
│   ├── model/
│   │   ├── ControlFrame.kt (NUEVA)
│   │   ├── ControlCell.kt (NUEVA)
│   │   └── AudioParameters.kt (NUEVA)
│   ├── repository/
│   │   ├── ICameraRepository.kt (NUEVA)
│   │   ├── IAudioRepository.kt (NUEVA)
│   │   └── ISettingsRepository.kt (NUEVA)
│   └── usecase/
│       ├── ProcessFrameUseCase.kt (NUEVA)
│       └── GenerateAudioUseCase.kt (NUEVA)
│
├── data/
│   ├── repository/
│   │   ├── CameraRepositoryImpl.kt (NUEVA)
│   │   ├── AudioRepositoryImpl.kt (NUEVA)
│   │   └── SettingsRepositoryImpl.kt (NUEVA)
│   └── datasource/
│       └── local/
│           └── PreferenceDataSource.kt (NUEVA)
│
├── ui/
│   ├── viewmodel/
│   │   ├── CameraViewModel.kt (NUEVA)
│   │   └── AudioViewModel.kt (NUEVA)
│   └── activity/
│       └── MainActivity.kt (ACTUALIZAR)
│
├── di/ (NUEVA)
│   ├── AppModule.kt (NUEVA)
│   ├── RepositoryModule.kt (NUEVA)
│   ├── UseCaseModule.kt (NUEVA)
│   └── DataModule.kt (NUEVA)
│
└── util/
    ├── Constants.kt (NUEVA)
    └── Extensions.kt (NUEVA)
```

**Comando (CMD o PowerShell):**

```powershell
# PowerShell
$basePath = "app\src\main\java\com\visualonda\sensory"

@(
    "$basePath\domain\model",
    "$basePath\domain\repository",
    "$basePath\domain\usecase",
    "$basePath\data\repository",
    "$basePath\data\datasource\local",
    "$basePath\ui\viewmodel",
    "$basePath\ui\activity",
    "$basePath\di",
    "$basePath\util"
) | ForEach-Object { New-Item -ItemType Directory -Path $_ -Force | Out-Null }

Write-Host "✓ Directorios creados"
```

### 2.4: Crear Hilt Modules (2 horas)

Crear estos 4 archivos en `app/src/main/java/com/visualonda/sensory/di/`:

**AppModule.kt** (provides app-level singletons)

```kotlin
package com.visualonda.sensory.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences("visualonda_prefs", Context.MODE_PRIVATE)
    }
}

```

**RepositoryModule.kt** (binds repository implementations)

```kotlin
package com.visualonda.sensory.di

import com.visualonda.sensory.data.repository.AudioRepositoryImpl
import com.visualonda.sensory.data.repository.CameraRepositoryImpl
import com.visualonda.sensory.data.repository.SettingsRepositoryImpl
import com.visualonda.sensory.domain.repository.IAudioRepository
import com.visualonda.sensory.domain.repository.ICameraRepository
import com.visualonda.sensory.domain.repository.ISettingsRepository
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
    abstract fun bindCameraRepository(impl: CameraRepositoryImpl): ICameraRepository
    
    @Singleton
    @Binds
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): IAudioRepository
    
    @Singleton
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): ISettingsRepository
}

```

**UseCaseModule.kt** (provides use cases)

```kotlin
package com.visualonda.sensory.di

import com.visualonda.sensory.domain.repository.ICameraRepository
import com.visualonda.sensory.domain.repository.IAudioRepository
import com.visualonda.sensory.domain.usecase.ProcessFrameUseCase
import com.visualonda.sensory.domain.usecase.GenerateAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    fun provideProcessFrameUseCase(
        cameraRepository: ICameraRepository
    ): ProcessFrameUseCase {
        return ProcessFrameUseCase(cameraRepository)
    }
    
    @Provides
    fun provideGenerateAudioUseCase(
        audioRepository: IAudioRepository
    ): GenerateAudioUseCase {
        return GenerateAudioUseCase(audioRepository)
    }
}

```

**DataModule.kt** (provides data sources & local storage)

```kotlin
package com.visualonda.sensory.di

import android.content.Context
import android.content.SharedPreferences
import com.visualonda.sensory.data.datasource.local.PreferenceDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Singleton
    @Provides
    fun providePreferenceDataSource(
        sharedPreferences: SharedPreferences
    ): PreferenceDataSource {
        return PreferenceDataSource(sharedPreferences)
    }
}

```

**RESULTADO PASO 2:** Hilt DI configurado ✓

---

## PASO 3: CREAR MODELOS DOMINIO (Martes - 1 hora)

Crear en `app/src/main/java/com/visualonda/sensory/domain/model/`:

### ControlFrame.kt

```kotlin
package com.visualonda.sensory.domain.model

data class ControlFrame(
    val timestampMs: Long,
    val frameRateHz: Int,
    val gridRows: Int,
    val gridCols: Int,
    val cells: List<ControlCell>
)

```

### ControlCell.kt

```kotlin
package com.visualonda.sensory.domain.model

data class ControlCell(
    val id: Int,
    val row: Int,
    val col: Int,
    val azimuthDeg: Float,
    val elevationM: Float,
    val distanceM: Float,
    val material: String,
    val luminance: Float,
    val confidence: Float
)

```

### AudioParameters.kt

```kotlin
package com.visualonda.sensory.domain.model

data class AudioParameters(
    val freq: Double,
    val gain: Double,
    val lpfCutoff: Double,
    val leftFreq: Double,
    val rightFreq: Double,
    val leftPan: Double,
    val rightPan: Double
)

```

---

## PASO 4: CREAR REPOSITORY INTERFACES (Martes - 1 hora)

Crear en `app/src/main/java/com/visualonda/sensory/domain/repository/`:

### ICameraRepository.kt

```kotlin
package com.visualonda.sensory.domain.repository

import com.visualonda.sensory.domain.model.ControlFrame
import kotlinx.coroutines.flow.Flow

interface ICameraRepository {
    fun startCapture(): Flow<ControlFrame>
    fun stopCapture()
    fun isCapturing(): Boolean
}

```

### IAudioRepository.kt

```kotlin
package com.visualonda.sensory.domain.repository

import com.visualonda.sensory.domain.model.AudioParameters

interface IAudioRepository {
    fun initialize(): Boolean
    fun playAudio(params: AudioParameters)
    fun stopAudio()
    fun cleanup()
}

```

### ISettingsRepository.kt

```kotlin
package com.visualonda.sensory.domain.repository

interface ISettingsRepository {
    fun getSetting(key: String): String?
    fun setSetting(key: String, value: String)
    fun getSettingFloat(key: String, default: Float): Float
    fun setSettingFloat(key: String, value: Float)
}

```

---

## PASO 5: CREAR USE CASES (Martes - 1 hora)

Crear en `app/src/main/java/com/visualonda/sensory/domain/usecase/`:

### ProcessFrameUseCase.kt

```kotlin
package com.visualonda.sensory.domain.usecase

import com.visualonda.sensory.domain.model.ControlFrame
import com.visualonda.sensory.domain.repository.ICameraRepository
import javax.inject.Inject

class ProcessFrameUseCase @Inject constructor(
    private val cameraRepository: ICameraRepository
) {
    operator fun invoke() = cameraRepository.startCapture()
}

```

### GenerateAudioUseCase.kt

```kotlin
package com.visualonda.sensory.domain.usecase

import com.visualonda.sensory.domain.model.AudioParameters
import com.visualonda.sensory.domain.repository.IAudioRepository
import javax.inject.Inject

class GenerateAudioUseCase @Inject constructor(
    private val audioRepository: IAudioRepository
) {
    fun initialize() = audioRepository.initialize()
    
    fun playAudio(params: AudioParameters) = audioRepository.playAudio(params)
    
    fun stop() = audioRepository.stopAudio()
    
    fun cleanup() = audioRepository.cleanup()
}

```

---

## PASO 6: CREAR REPOSITORY IMPLEMENTATIONS (Martes - 1 hora)

Crear en `app/src/main/java/com/visualonda/sensory/data/repository/`:

### CameraRepositoryImpl.kt

```kotlin
package com.visualonda.sensory.data.repository

import com.visualonda.sensory.domain.model.ControlFrame
import com.visualonda.sensory.domain.repository.ICameraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor() : ICameraRepository {
    private var isCapturing = false
    
    override fun startCapture(): Flow<ControlFrame> {
        isCapturing = true
        return emptyFlow()  // Placeholder: implementar en Week 3
    }
    
    override fun stopCapture() {
        isCapturing = false
    }
    
    override fun isCapturing(): Boolean = isCapturing
}

```

### AudioRepositoryImpl.kt

```kotlin
package com.visualonda.sensory.data.repository

import com.visualonda.sensory.domain.model.AudioParameters
import com.visualonda.sensory.domain.repository.IAudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor() : IAudioRepository {
    private var isInitialized = false
    
    override fun initialize(): Boolean {
        isInitialized = true
        // TODO: implementar AAudio init en Week 2
        return true
    }
    
    override fun playAudio(params: AudioParameters) {
        // TODO: implementar en Week 2
    }
    
    override fun stopAudio() {
        // TODO: implementar en Week 2
    }
    
    override fun cleanup() {
        isInitialized = false
    }
}

```

### SettingsRepositoryImpl.kt

```kotlin
package com.visualonda.sensory.data.repository

import android.content.SharedPreferences
import com.visualonda.sensory.domain.repository.ISettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : ISettingsRepository {
    
    override fun getSetting(key: String): String? = prefs.getString(key, null)
    
    override fun setSetting(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    override fun getSettingFloat(key: String, default: Float): Float {
        return prefs.getFloat(key, default)
    }
    
    override fun setSettingFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }
}

```

---

## PASO 7: DATA SOURCE (Martes - 30 min)

Crear `app/src/main/java/com/visualonda/sensory/data/datasource/local/PreferenceDataSource.kt`:

```kotlin
package com.visualonda.sensory.data.datasource.local

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceDataSource @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getString(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }
    
    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    
    fun getInt(key: String, default: Int = 0): Int {
        return prefs.getInt(key, default)
    }
    
    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
}

```

---

## PASO 8: VIEW MODELS (Martes - 1 hora)

Crear en `app/src/main/java/com/visualonda/sensory/ui/viewmodel/`:

### CameraViewModel.kt

```kotlin
package com.visualonda.sensory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.visualonda.sensory.domain.usecase.ProcessFrameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val processFrameUseCase: ProcessFrameUseCase
) : ViewModel() {
    
    fun startCapture() {
        viewModelScope.launch {
            // TODO: Implement frame processing
        }
    }
}

```

### AudioViewModel.kt

```kotlin
package com.visualonda.sensory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.visualonda.sensory.domain.usecase.GenerateAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val generateAudioUseCase: GenerateAudioUseCase
) : ViewModel() {
    
    fun initialize() = generateAudioUseCase.initialize()
    
    override fun onCleared() {
        super.onCleared()
        generateAudioUseCase.cleanup()
    }
}

```

**RESULTADO PASO 8:** Todos los modelos, repositorios, use cases, view models creados ✓

---

## PASO 9: ACTUALIZAR MAINACTIVITY.KT (Martes - 1 hora)

**Reemplazar completamente:** `app/src/main/java/com/visualonda/sensory/MainActivity.kt`

```kotlin
package com.visualonda.sensory

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.visualonda.sensory.ui.viewmodel.AudioViewModel
import com.visualonda.sensory.ui.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private val cameraViewModel: CameraViewModel by viewModels()
    private val audioViewModel: AudioViewModel by viewModels()
    
    // JNI stubs (for Week 1 integration)
    external fun pdInit()
    external fun audioEngineInit()
    
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity created")
        
        setupUI()
    }
    
    private fun setupUI() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        // Initialize Audio Button
        val btnAudioInit = Button(this)
        btnAudioInit.text = "Init Audio Engine"
        btnAudioInit.setOnClickListener {
            try {
                val success = audioViewModel.initialize()
                audioEngineInit()
                Toast.makeText(this, "Audio initialized", Toast.LENGTH_SHORT).show()
                Timber.d("Audio initialized: $success")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing audio")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Start Camera Button
        val btnStartCamera = Button(this)
        btnStartCamera.text = "Start Camera (Week 3)"
        btnStartCamera.setOnClickListener {
            cameraViewModel.startCapture()
            Toast.makeText(this, "Camera capturing...", Toast.LENGTH_SHORT).show()
            Timber.d("Camera capture started")
        }
        
        // Libreria PD
        val btnPDInit = Button(this)
        btnPDInit.text = "Init PD (Week 1)"
        btnPDInit.setOnClickListener {
            try {
                pdInit()
                Toast.makeText(this, "PD initialized", Toast.LENGTH_SHORT).show()
                Timber.d("PD initialized")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing PD")
            }
        }
        
        layout.addView(btnAudioInit)
        layout.addView(btnPDInit)
        layout.addView(btnStartCamera)
        setContentView(layout)
        
        Timber.d("UI setup complete")
    }
}

```

**RESULTADO PASO 9:** MainActivity refactored con Hilt & MVVM ✓

---

## PASO 10: CMakeLists.txt & NDK (Martes - 30 min)

**Reemplazar:** `app/CMakeLists.txt`

```cmake
cmake_minimum_required(VERSION 3.22.1)
project("native-lib")

# Source files to compile
add_library(native-lib SHARED 
    src/main/cpp/native-lib.cpp
    # Agregar semana 1-2: libpd_wrapper.cpp, audio_engine.cpp
    # Agregar semana 3: frame_processor.cpp, mapping_engine.cpp
)

# Include directories
target_include_directories(native-lib PRIVATE
    src/main/cpp/libpd/include
    src/main/cpp
)

# Link libraries
find_library(log-lib log)
find_library(android-lib android)

# Link AAudio (Fase 1 Week 2)
find_library(aaudio-lib aaudio)

target_link_libraries(native-lib
    ${log-lib}
    ${android-lib}
    ${aaudio-lib}
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpd.so
)

# Compiler flags
target_compile_options(native-lib PRIVATE -Wall -Wextra)

```

**RESULTADO PASO 10:** CMakeLists.txt actualizado para AAudio + libpd ✓

---

## PASO 11: BUILD & VERIFY (Miércoles - 2 horas)

### 11.1: Compilación (1 hora)

```bash
# En proyecto root:
cd android_skeleton

# Limpiar
.\gradlew clean

# Build
.\gradlew build

# Esperado: BUILD SUCCESSFUL

# Si hay errores: Verificar
```

### 11.2: Troubleshooting Común

#### Error: "Cannot find libpd.so"

```
Solución: Verificar ruta en build.gradle
ndk {
    abiFilters 'arm64-v8a'
}

Y CMakeLists.txt:
${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpd.so
```

#### Error: "Hilt not found"

```
Solución: Verificar en build.gradle (app):
kapt 'com.google.dagger:hilt-compiler:2.48'

Y project build.gradle:
classpath 'com.google.dagger:hilt-android-gradle-plugin:2.48'
```

#### Error: "Kotlin version mismatch"

```
Solución: Actualizar en build.gradle (project):
classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10'
```

### 11.3: Instalación & Testing (1 hora)

```bash
# Build APK
.\gradlew installDebug

# En dispositivo:
# ☑ App abre sin crash
# ☑ Ver 3 botones
# ☑ Presionar "Init Audio Engine" → Toast muestra "Audio initialized"
# ☑ Presionar "Init PD" → Toast muestra "PD initialized"

# Verificar logcat:
adb logcat | grep -E "(VisualondaNative|MainActivity|Hilt)"
```

**RESULTADO PASO 11:** Proyecto compila y se abre sin errores ✓

---

## PASO 12: GATE FASE 0 (Miércoles EOD)

### ✅ Criterios de Aceptación:

```
ARQUITECTURA:
☑ Estructura de paquetes completada (domain/data/ui/di)
☑ Hilt DI configurado y funciona
☑ MVVM pattern implementado en ViewModels
☑ Repository pattern con interfaces
☑ Use cases creados

BUILD:
☑ Compilación: 0 errores, <10 warnings
☑ CMakeLists.txt actualizado para libpd + AAudio
☑ build.gradle con Hilt + CameraX + Coroutines

RUNTIME:
☑ App instala en device/emulator
☑ MainActivity abre sin crash
☑ 3 botones funcionales (no crashing)
☑ Logcat limpio (sin crashes importantes)

DOCUMENTACIÓN:
☑ Inline comments en código
☑ Classes documentadas con KDoc
☑ README.md actualizado

Si ✅ TODO PASS → PROCEDE A FASE 1 WEEK 1
Si ❌ FALLA ALGO → ITERATE + FIX
```

---

## SIGUIENTE PASO: FASE 1 WEEK 1

**Jueves (Mañana):**

```
☐ Kick-off meeting (30 min)
  - Revisar gate Fase 0 ✓
  - Confirmar equipo assignments
  - Daily standup schedule

☐ Audio engineer comienza: libpd_wrapper.cpp (4 horas)
  ├─ Write libpd_wrapper.h
  ├─ Write libpd_wrapper.cpp (180 líneas)
  ├─ Integrate JNI stubs
  └─ Testing

☐ Android engineer comienza: Native code setup (2 horas)
  ├─ Integrate libpd_wrapper
  ├─ Update native-lib.cpp stubs
  └─ First compile test

RESULTADO WEEK 1 END: libpd funcional ✓
```

**Leer siguiente:** `FASE_1_IMPLEMENTATION_PLAN.md`

---

## 📋 RESUMEN: 2-3 DÍAS PARA WEEK 1

| Día | Tarea | Horas | Resultado |
|-----|-------|-------|----------|
| Lunes | Setup libpd + directorios | 2 | Binarios en lugar ✓ |
| Martes | Hilt + Repos + VMs | 8 | Arquitectura lista ✓ |
| Miércoles | Build + Test | 2 | Proyecto compila ✓ |
| **Total** | **Fase 0 + Setup** | **~12** | **Ready for Week 1** ✓ |

---

## 🎯 DECISIÓN FINAL

**Se recomienda: OPCIÓN B (14-16 semanas, RECOMENDADO)**

Ver: `DECISION_FINAL_ROADMAP.md`

**Próximos documentos a leer:**
- `ROADMAP_SIN_GOOGLE_PLAY.md` (14-16 week timeline)
- `FASE_1_IMPLEMENTATION_PLAN.md` (Week 1-4 detail)
- `CHECKLIST_FASE_1.md` (Daily executable tasks)

---

**Documento:** FASE_0_TO_WEEK_1_BRIDGE.md
**Objetivo:** Cerrar gap entre documentación y implementación
**Timeline:** 2-3 días antes de Fase 1 Week 1
**Acción:** Ejecutar pasos 1-12 en orden

