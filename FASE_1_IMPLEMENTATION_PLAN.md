# FASE 1: FOUNDATION — Plan de Implementación Detallado

## 🎯 Objetivo: Audio funcional end-to-end (4 semanas)

### Semana 1: LibPD Integration + AAudio Setup

#### TAREA 1.1: Descargar & Setup LibPD
**Duración:** 2-3 horas

1. Descargar libpd prebuilt para Android ARM64 desde:
   ```
   https://github.com/libpd/libpd/releases
   Versión recomendada: 0.12.x o más nueva
   ```

2. Estructura de archivos a crear:
   ```
   android_skeleton/app/src/main/
   ├── jniLibs/
   │   ├── arm64-v8a/
   │   │   └── libpd.so          ← copiar binario aquí
   │   ├── armeabi-v7a/          ← (opcional, para compatibilidad)
   │   │   └── libpd.so
   │   └── x86_64/               ← (opcional, para emulador)
   │       └── libpd.so
   ├── cpp/
   │   └── (headers existentes + nuevos)
   └── java/
       └── (código Java existente)
   ```

3. Descargar headers de libpd:
   ```bash
   # Desde repo: https://github.com/libpd/libpd
   # Copiar a:
   android_skeleton/app/src/main/cpp/libpd/include/
   - libpd.h
   - pd.h
   - m_pd.h
   ```

#### TAREA 1.2: Actualizar CMakeLists.txt para LibPD
**Duración:** 1 hora

Reemplazar `android_skeleton/app/CMakeLists.txt`:

```cmake
cmake_minimum_required(VERSION 3.10.2)
project("native-lib")

# Define paths
set(LIBPD_PATH "${CMAKE_CURRENT_SOURCE_DIR}/libpd")

# Create native-lib library
add_library(native-lib SHARED 
    src/main/cpp/native-lib.cpp
    src/main/cpp/mapping_engine.cpp
    src/main/cpp/audio_engine.cpp
    src/main/cpp/libpd_wrapper.cpp
)

# Link system libraries
find_library(log-lib log)
find_library(audiotrack android)

# Include libpd headers
target_include_directories(native-lib PRIVATE
    ${LIBPD_PATH}/include
    src/main/cpp
)

# Link libpd (prebuilt .so)
target_link_libraries(native-lib
    ${log-lib}
    ${audiotrack}
    ${LIBPD_PATH}/../../../jniLibs/${ANDROID_ABI}/libpd.so
)

# C++ standard
set_property(TARGET native-lib PROPERTY CXX_STANDARD 17)
```

#### TAREA 1.3: Implementar LibPD Wrapper (C++)
**Duración:** 3-4 horas

Crear `android_skeleton/app/src/main/cpp/libpd_wrapper.cpp`:

```cpp
#include <libpd.h>
#include <android/log.h>
#include <thread>
#include <mutex>
#include <queue>

#define LOG_TAG "LibPDWrapper"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static void *pd_handle = nullptr;
static std::mutex pd_mutex;
static std::queue<std::pair<std::string, float>> param_queue;

// Inicializar LibPD
bool libpd_init() {
    std::lock_guard<std::mutex> lock(pd_mutex);
    if (pd_handle) {
        ALOG("LibPD ya inicializado");
        return true;
    }
    
    int ret = libpd_init();
    if (ret != 0) {
        ALOG("Error inicializando LibPD: %d", ret);
        return false;
    }
    
    ALOG("LibPD inicializado correctamente");
    libpd_set_verbose(1);  // Debug mode
    return true;
}

// Cargar patch
bool libpd_load_patch(const char* patch_path) {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (!pd_handle) {
        ALOG("LibPD no inicializado. Llamar a libpd_init() primero");
        return false;
    }
    
    int ret = libpd_openpatch(patch_path);
    if (ret < 0) {
        ALOG("Error cargando patch %s: %d", patch_path, ret);
        return false;
    }
    
    ALOG("Patch cargado: %s", patch_path);
    pd_handle = (void*)(intptr_t)ret;
    return true;
}

// Enviar float a receiver
bool libpd_send_float(const char* receiver_name, float value) {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (!pd_handle) {
        ALOG("LibPD no inicializado");
        return false;
    }
    
    int ret = libpd_float(receiver_name, value);
    if (ret != 0) {
        ALOG("Error enviando float a %s: %d", receiver_name, ret);
        return false;
    }
    
    ALOG("Enviado: %s = %.2f", receiver_name, value);
    return true;
}

// Cleanup
void libpd_cleanup() {
    std::lock_guard<std::mutex> lock(pd_mutex);
    if (pd_handle) {
        libpd_closepatch((int)(intptr_t)pd_handle);
        pd_handle = nullptr;
    }
    ALOG("LibPD limpiado");
}
```



#### TAREA 1.4: Actualizar native-lib.cpp con LibPD calls
**Duración:** 2 horas

Reemplazar stubs en `native-lib.cpp`:

```cpp
// JNI stubs for LibPD integration (REEMPLAZADO)
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(JNIEnv* env, jobject /* this */) {
    if (libpd_init()) {
        ALOG("[JNI] LibPD initialized successfully");
    } else {
        ALOG("[JNI] Failed to initialize LibPD");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdOpenPatch(JNIEnv* env, jobject /* this */, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    if (libpd_load_patch(path)) {
        ALOG("[JNI] Patch loaded: %s", path);
    } else {
        ALOG("[JNI] Failed to load patch: %s", path);
    }
    env->ReleaseStringUTFChars(jpath, path);
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdSendFloat(JNIEnv* env, jobject /* this */, jstring jname, jfloat value) {
    const char* name = env->GetStringUTFChars(jname, nullptr);
    libpd_send_float(name, (float)value);
    env->ReleaseStringUTFChars(jname, name);
}
```

---

### Semana 2: Audio Engine (AAudio Callback)

#### TAREA 2.1: Crear Audio Engine (C++)
**Duración:** 4-5 horas

Crear `android_skeleton/app/src/main/cpp/audio_engine.cpp`:

```cpp
#include <aaudio/AAudio.h>
#include <android/log.h>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <queue>
#include <cstring>

#define LOG_TAG "AudioEngine"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Audio engine state
static AAudioStream *stream = nullptr;
static std::mutex audio_mutex;
static float mix_buffer[8192];  // Buffer temporal

// Callback de audio (CRÍTICO: latencia baja)
aaudio_data_callback_result_t audio_callback(
    AAudioStream *stream,
    void *userData,
    void *audioData,
    int32_t numFrames) {
    
    float *output = (float *)audioData;
    
    // AQUÍ: Llamar a LibPD para generar síntesis
    // Por ahora, generar onda de prueba simple
    static float phase = 0.0f;
    float freq = 4000.0f;  // Será reemplazado con parámetros reales
    float sr = (float)AAudioStream_getSampleRate(stream);
    
    for (int i = 0; i < numFrames; ++i) {
        // Generar onda senoidal simple
        float sample = sinf(phase * 2.0f * M_PI);
        output[i * 2] = sample * 0.1f;      // Left channel
        output[i * 2 + 1] = sample * 0.1f;  // Right channel
        
        phase += freq / sr;
        if (phase > 1.0f) phase -= 1.0f;
    }
    
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

bool audio_engine_init() {
    std::lock_guard<std::mutex> lock(audio_mutex);
    
    AAudioStreamBuilder *builder = nullptr;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    
    if (result != AAUDIO_OK) {
        ALOG("Error creando builder: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    // Configurar builder
    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(builder, 44100);
    AAudioStreamBuilder_setChannelCount(builder, 2);  // Estéreo
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setDataCallback(builder, audio_callback, nullptr);
    AAudioStreamBuilder_setErrorCallback(builder, nullptr, nullptr);
    
    // Crear stream
    result = AAudioStreamBuilder_openStream(builder, &stream);
    AAudioStreamBuilder_delete(builder);
    
    if (result != AAUDIO_OK) {
        ALOG("Error creando stream: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    // Iniciar stream
    result = AAudioStream_requestStart(stream);
    if (result != AAUDIO_OK) {
        ALOG("Error iniciando stream: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    ALOG("Audio engine inicializado @ 44.1kHz, estéreo");
    return true;
}

void audio_engine_cleanup() {
    std::lock_guard<std::mutex> lock(audio_mutex);
    if (stream) {
        AAudioStream_requestStop(stream);
        AAudioStream_close(stream);
        stream = nullptr;
        ALOG("Audio engine limpiado");
    }
}
```

#### TAREA 2.2: Build.gradle con NDK & aaudio
**Duración:** 1 hora

Actualizar `app/build.gradle`:

```gradle
android {
    // ...
    
    defaultConfig {
        // ...
        externalNativeBuild {
            cmake {
                cppFlags "-std=c++17 -O3"
            }
        }
        ndk {
            abiFilters 'arm64-v8a'  // (opcional: 'armeabi-v7a', 'x86_64')
        }
    }
    
    // ...
}

dependencies {
    // Audio
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    
    // Rest igual
}
```

---

### Semana 2-3: Vision Frontend Mínimo

#### TAREA 3.1: Captura de Cámara Android (Kotlin)
**Duración:** 4-5 horas

Crear `MainActivity.kt` actualizado:

```kotlin
package com.visualonda.sensory

import android.Manifest
import android.camera.CameraManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private val TAG = "Visualonda"
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null
    
    external fun pdInit()
    external fun pdOpenPatch(path: String)
    external fun pdSendFloat(name: String, value: Float)
    external fun sendControlJson(json: String)
    external fun audioEngineInit()
    external fun audioEngineCleanup()
    
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
        
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar permisos
        if (!hasPermissions()) {
            requestPermissions()
        } else {
            initializeApp()
        }
    }
    
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, PERMISSION_AUDIO) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(PERMISSION_CAMERA, PERMISSION_AUDIO),
            REQUEST_CODE_PERMISSIONS
        )
    }
    
    private fun initializeApp() {
        // Crear UI
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        val btnAudioInit = Button(this)
        btnAudioInit.text = "Init Audio Engine"
        btnAudioInit.setOnClickListener { audioEngineInit() }
        
        val btnStartCamera = Button(this)
        btnStartCamera.text = "Start Camera (Processing)"
        btnStartCamera.setOnClickListener { startCameraCapture() }
        
        val btnStop = Button(this)
        btnStop.text = "Stop"
        btnStop.setOnClickListener { stopCamera() }
        
        layout.addView(btnAudioInit)
        layout.addView(btnStartCamera)
        layout.addView(btnStop)
        setContentView(layout)
        
        // Inicializar LibPD
        pdInit()
    }
    
    private fun startCameraCapture() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.result
            bindCameraPreview()
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun bindCameraPreview() {
        val preview = Preview.Builder().build()
        
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        
        imageAnalysis.setAnalyzer(cameraExecutor) { image ->
            processFrame(image)
            image.close()
        }
        
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        
        try {
            cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            Log.d(TAG, "Camera binding successful")
        } catch (e: Exception) {
            Log.e(TAG, "Error binding camera", e)
        }
    }
    
    private fun processFrame(image: Image) {
        // Extraer luminancia (Y channel en YUV)
        val data = image.planes[0].buffer.array()
        
        // Crear grid 16x16 (downsample)
        val gridSize = 16
        val cellWidth = image.width / gridSize
        val cellHeight = image.height / gridSize
        
        // Generar control_schema.json
        val cells = mutableListOf<String>()
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val pixelIndex = (row * cellHeight) * image.width + (col * cellWidth)
                val luminance = (data[pixelIndex].toInt() and 0xFF) / 255.0f
                
                val azimuth = ((col - gridSize / 2.0f) / (gridSize / 2.0f)) * 90.0f
                val elevation = ((gridSize / 2.0f - row) / (gridSize / 2.0f)) * 2.0f  // 0-2m
                val distance = 2.5f  // Placeholder: usar depth si disponible
                
                cells.add("""{
                    "id": ${row * gridSize + col},
                    "row": $row,
                    "col": $col,
                    "azimuth_deg": $azimuth,
                    "elevation_m": $elevation,
                    "distance_m": $distance,
                    "material": "${if (luminance > 0.7) "metal" else "wood"}",
                    "luminance": $luminance,
                    "confidence": 0.9
                }""")
            }
        }
        
        val json = """{
            "timestamp_ms": System.currentTimeMillis(),
            "frame_rate_hz": 30,
            "grid": {"rows": 16, "cols": 16},
            "cells": [${cells.joinToString(",")}]
        }"""
        
        sendControlJson(json)
    }
    
    private fun stopCamera() {
        cameraProvider?.unbindAll()
        audioEngineCleanup()
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeApp()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
```

