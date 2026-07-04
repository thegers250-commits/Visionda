# 🚀 PLAN DE EJECUCIÓN COMPLETO: VISUALONDA

## Visión General

```
Estado Actual:     65% incompleto (stubs, sin audio, sin cámara)
Objetivo Final:    Producto funcional en Google Play Store
Timeline:          18 semanas (4 fases)
Team:              3.5-4 FTE
Budget:            $244K
```

---

## 🎯 FASE 0: PREPARACIÓN (Semana 0 - ESTA SEMANA)

**Duración:** 3-5 días (paralelo a documentación)
**Equipo:** Tech Lead + 1 Android Engineer
**Objetivo:** Tener proyecto compilable con stubs reales

### 0.1: Setup Inicial (1 día)

#### Tarea 0.1.1: Descargar LibPD
- [ ] Ir a https://github.com/libpd/libpd/releases
- [ ] Descargar `libpd-0.12.x-android.zip` (arm64-v8a)
- [ ] Extraer a temp folder

#### Tarea 0.1.2: Setup de Directorios
```bash
mkdir -p android_skeleton/app/src/main/jniLibs/arm64-v8a
mkdir -p android_skeleton/app/src/main/cpp/libpd/include
mkdir -p android_skeleton/app/src/main/assets/patches
mkdir -p android_skeleton/app/src/main/assets/config
```

#### Tarea 0.1.3: Copiar Binarios y Headers
```bash
# Copiar libpd.so
cp libpd-0.12/android/arm64-v8a/libpd.so → android_skeleton/app/src/main/jniLibs/arm64-v8a/

# Copiar headers
cp libpd-0.12/pure-data/src/{libpd.h,pd.h,m_pd.h} → android_skeleton/app/src/main/cpp/libpd/include/

# Copiar patch
cp sensory-language/light_material_patch.pd → android_skeleton/app/src/main/assets/patches/
```

### 0.2: Actualizar Build Configuration (1 día)

#### Tarea 0.2.1: Reescribir CMakeLists.txt
Ver archivo: `CMAKE_UPDATES.md` (créar next)

#### Tarea 0.2.2: Actualizar build.gradle
```gradle
// Agregar después de defaultConfig:
ndk {
    abiFilters 'arm64-v8a'  // Primary
    // Optional: 'armeabi-v7a', 'x86_64'
}

// Agregar dependencias CameraX:
dependencies {
    implementation 'androidx.camera:camera-core:1.2.3'
    implementation 'androidx.camera:camera-camera2:1.2.3'
    implementation 'androidx.camera:camera-lifecycle:1.2.3'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

### 0.3: Verificación (1 día)

#### Tarea 0.3.1: Build Test
```bash
./gradlew clean build
# Esperado: Sin errores (puede haber warnings)
```

#### Tarea 0.3.2: Troubleshooting Común
Si faltan headers:
```bash
# Verificar ruta exacta
ls -la android_skeleton/app/src/main/cpp/libpd/include/
# Debe mostrar: libpd.h, pd.h, m_pd.h
```

Si linker error:
```bash
# Verificar libpd.so existe
ls -la android_skeleton/app/src/main/jniLibs/arm64-v8a/libpd.so
# Debe ser ~1.2 MB
```

**Entregable Fase 0:**
- ✅ Proyecto compila sin stubs rotos
- ✅ libpd.so + headers en lugar correcto
- ✅ CMakeLists.txt actualizado
- ✅ build.gradle con ndk filters

---

## 🔧 FASE 1: FOUNDATION (Semanas 1-4)

**Duración:** 4 semanas
**Equipo:** Android/NDK (2) + Audio (0.5) + Tech Lead
**Objetivo:** Audio funcional end-to-end

### Semana 1: LibPD Integration + AAudio Setup

#### 1.1: Crear libpd_wrapper.cpp (6 horas)

```cpp
// FILE: app/src/main/cpp/libpd_wrapper.cpp
#include "libpd_wrapper.h"
#include <libpd.h>
#include <android/log.h>
#include <mutex>
#include <cstring>

#define LOG_TAG "LibPDWrapper"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static std::mutex pd_mutex;
static bool pd_initialized = false;
static void* pd_patch_handle = nullptr;

// Inicializar LibPD
bool libpd_wrapper_init() {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (pd_initialized) {
        ALOG("LibPD ya inicializado");
        return true;
    }
    
    libpd_init();
    libpd_set_verbose(1);  // Debug mode
    
    pd_initialized = true;
    ALOG("LibPD inicializado correctamente");
    return true;
}

// Cargar patch
bool libpd_wrapper_load_patch(const char* patch_path) {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (!pd_initialized) {
        ALOG("LibPD no inicializado");
        return false;
    }
    
    int patch_handle = libpd_openpatch(patch_path);
    if (patch_handle < 0) {
        ALOG("Error cargando patch %s", patch_path);
        return false;
    }
    
    pd_patch_handle = (void*)(intptr_t)patch_handle;
    ALOG("Patch cargado: %s", patch_path);
    return true;
}

// Enviar float a receiver
bool libpd_wrapper_send_float(const char* receiver, float value) {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (!pd_initialized || !pd_patch_handle) {
        ALOG("LibPD no está listo para enviar");
        return false;
    }
    
    libpd_float(receiver, (double)value);
    ALOG("Enviado: %s = %.3f", receiver, value);
    return true;
}

// Cleanup
void libpd_wrapper_cleanup() {
    std::lock_guard<std::mutex> lock(pd_mutex);
    
    if (pd_patch_handle) {
        libpd_closepatch((int)(intptr_t)pd_patch_handle);
        pd_patch_handle = nullptr;
    }
    
    pd_initialized = false;
    ALOG("LibPD limpiado");
}
```

#### 1.2: Crear libpd_wrapper.h (2 horas)

```cpp
// FILE: app/src/main/cpp/libpd_wrapper.h
#ifndef LIBPD_WRAPPER_H
#define LIBPD_WRAPPER_H

#ifdef __cplusplus
extern "C" {
#endif

bool libpd_wrapper_init();
bool libpd_wrapper_load_patch(const char* patch_path);
bool libpd_wrapper_send_float(const char* receiver, float value);
void libpd_wrapper_cleanup();

#ifdef __cplusplus
}
#endif

#endif  // LIBPD_WRAPPER_H
```

#### 1.3: Actualizar native-lib.cpp - Reemplazar Stubs (4 horas)

```cpp
// REEMPLAZAR ESTO:
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(...) {
    ALOG("[native] pdInit() called - stub");
}

// CON ESTO:
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(JNIEnv* env, jobject /* this */) {
    if (libpd_wrapper_init()) {
        ALOG("[JNI] LibPD initialized successfully");
    } else {
        ALOG("[JNI] Failed to initialize LibPD");
    }
}

// Y SIMILAR PARA pdOpenPatch() Y pdSendFloat()
// (Ver FASE_1_IMPLEMENTATION_PLAN.md para código completo)
```

#### Testing Semana 1:
```
☐ ./gradlew build → Sin errores
☐ ./gradlew installDebug → App instala
☐ Botón "Init PD" → Logcat muestra "LibPD initialized"
☐ No crash cuando carga patch
✅ Semana 1 COMPLETA
```

### Semana 2: Audio Engine (AAudio)

(Continuará en next chunk - es mucho código)



### Semana 2: Audio Engine (AAudio)

#### 2.1: Crear audio_engine.cpp (8 horas)

```cpp
// FILE: app/src/main/cpp/audio_engine.cpp
#include "audio_engine.h"
#include <aaudio/AAudio.h>
#include <android/log.h>
#include <cmath>
#include <thread>

#define LOG_TAG "AudioEngine"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static AAudioStream *stream = nullptr;
static float phase = 0.0f;
static float current_freq = 4000.0f;
static float current_amp = 0.1f;

// Audio callback (CRÍTICO - bajo latency)
aaudio_data_callback_result_t audio_callback(
    AAudioStream *stream,
    void *userData,
    void *audioData,
    int32_t numFrames) {
    
    float *output = (float *)audioData;
    float sr = (float)AAudioStream_getSampleRate(stream);
    
    // Generar sine wave binaural
    for (int i = 0; i < numFrames; ++i) {
        float sample = sinf(phase * 2.0f * M_PI);
        output[i * 2] = sample * current_amp;       // Left
        output[i * 2 + 1] = sample * current_amp;   // Right
        phase += current_freq / sr;
        if (phase > 1.0f) phase -= 1.0f;
    }
    
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

bool audio_engine_init() {
    AAudioStreamBuilder *builder = nullptr;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    
    if (result != AAUDIO_OK) {
        ALOG("Error: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(builder, 44100);
    AAudioStreamBuilder_setChannelCount(builder, 2);
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setDataCallback(builder, audio_callback, nullptr);
    
    result = AAudioStreamBuilder_openStream(builder, &stream);
    AAudioStreamBuilder_delete(builder);
    
    if (result != AAUDIO_OK) {
        ALOG("Error opening stream: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    result = AAudioStream_requestStart(stream);
    if (result != AAUDIO_OK) {
        ALOG("Error starting stream: %s", AAudio_convertResultToText(result));
        return false;
    }
    
    ALOG("Audio engine initialized @ 44.1kHz");
    return true;
}

void audio_engine_set_freq(float freq) {
    current_freq = freq;
    ALOG("Freq set to %.1f Hz", freq);
}

void audio_engine_set_amp(float amp) {
    current_amp = amp * 0.5f;  // Safety: max 0.5
    ALOG("Amp set to %.3f", current_amp);
}

void audio_engine_cleanup() {
    if (stream) {
        AAudioStream_requestStop(stream);
        AAudioStream_close(stream);
        stream = nullptr;
        ALOG("Audio engine cleaned up");
    }
}
```

#### 2.2: Crear audio_engine.h (2 horas)

```cpp
// FILE: app/src/main/cpp/audio_engine.h
#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#ifdef __cplusplus
extern "C" {
#endif

bool audio_engine_init();
void audio_engine_set_freq(float freq);
void audio_engine_set_amp(float amp);
void audio_engine_cleanup();

#ifdef __cplusplus
}
#endif

#endif  // AUDIO_ENGINE_H
```

#### 2.3: Actualizar native-lib.cpp - Agregar Audio Functions (4 horas)

```cpp
// AGREGAR ESTAS FUNCIONES A native-lib.cpp:

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineInit(JNIEnv* env, jobject) {
    if (audio_engine_init()) {
        ALOG("[JNI] Audio engine initialized");
    } else {
        ALOG("[JNI] Audio engine init failed");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineSetFreq(JNIEnv* env, jobject, jfloat freq) {
    audio_engine_set_freq((float)freq);
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineCleanup(JNIEnv* env, jobject) {
    audio_engine_cleanup();
}
```

#### 2.4: Actualizar CMakeLists.txt (2 horas)

```cmake
# AGREGAR ESTO AL CMakeLists.txt:

# Sources to compile
add_library(native-lib SHARED 
    src/main/cpp/native-lib.cpp
    src/main/cpp/libpd_wrapper.cpp
    src/main/cpp/audio_engine.cpp
    # Más en semana 3
)

# Link AAudio
find_library(aaudio-lib aaudio)
target_link_libraries(native-lib ${aaudio-lib})
```

#### Testing Semana 2:
```
☐ Compilación sin errores
☐ App instala
☐ Conectar auriculares
☐ Presionar botón "Init Audio"
☐ Escuchar tono @ 4000 Hz
☐ Logcat muestra "Audio engine initialized"
✅ Semana 2 COMPLETA
```

### Semana 3-4: Vision Frontend + Mapeos

(Continuará...)

---

## 📱 FASE 2: ACCESIBILIDAD (Semanas 5-8)

**Duración:** 4 semanas
**Equipo:** Android (1) + Accesibilidad spec
**Objetivo:** Navegación accesible completa

### Tareas Principales:

```
☐ TalkBack integration (Semana 5)
☐ Gesture recognition (Semana 5-6)
☐ Haptic feedback (Semana 6)
☐ Multi-mode switching (Semana 7)
☐ Settings Activity (Semana 7-8)
☐ Beta testing usuarios ciegos (Semana 8)
```

(Detalles en siguiente documento)

---

## 🤖 FASE 3: INTELIGENCIA (Semanas 9-12)

**Duración:** 4 semanas
**Equipo:** ML (1) + Android (0.5)
**Objetivo:** ML models integrados

### Tareas Principales:

```
☐ Object Detection - TensorFlow Lite (Semanas 9-10)
☐ Depth Estimation - MiDaS (Semana 10-11)
☐ Text Recognition - OCR (Semana 11)
☐ Face/Hand Detection - MediaPipe (Semana 11-12)
☐ Optimization & calibration (Semana 12)
```

---

## 🎛️ FASE 4: PULIDO & RELEASE (Semanas 13-18)

**Duración:** 6 semanas
**Equipo:** Todos (1 dev + 0.5 QA) + Tech Lead
**Objetivo:** Producto en Google Play Store

### Tareas Principales:

```
☐ Performance optimization (Semanas 13-14)
☐ Audio safety hardening (Semana 14-15)
☐ QA testing & bug fixes (Semana 14-15)
☐ Documentation (Semana 15-16)
☐ Google Play submission (Semana 16-18)
☐ Release & launch (Semana 18)
```

---

## 📊 RESUMEN EJECUTIVO: PLAN COMPLETO

```
FASE 0: Preparación          (3-5 días)    ← AHORA
FASE 1: Foundation            (4 semanas)  ← Audio funcional
FASE 2: Accesibilidad         (4 semanas)  ← UI navegable
FASE 3: Inteligencia          (4 semanas)  ← ML models
FASE 4: Pulido & Release      (6 semanas)  ← LAUNCH 🚀

TOTAL: ~18 semanas desde Fase 1
ESFUERZO: ~2,260 líneas código
EQUIPO: 3.5-4 FTE
PRESUPUESTO: $244K
```

---

## ✅ PRÓXIMOS PASOS INMEDIATOS

**HOY:**
1. Tech Lead revisa PLAN_EJECUCION_COMPLETO.md
2. Descargar libpd.so + headers
3. Setup directorios

**MAÑANA:**
1. Compilar proyecto (Fase 0)
2. Resolver cualquier error linker

**LUNES:**
1. Kick-off Fase 1
2. Android engineer comienza libpd_wrapper.cpp
3. Daily standups comienzan

**SEMANA 1 FIN:**
1. libpd_wrapper.cpp terminado
2. Stubs en native-lib.cpp reemplazados
3. Proyecto compila sin warnings mayores



### Semana 3-4: Vision Frontend + Mapeos Completos

#### 3.1: Actualizar MainActivity.kt - Captura de Cámara (12 horas)

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/MainActivity.kt
package com.visualonda.sensory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    
    private val TAG = "VisualondaApp"
    private lateinit var cameraExecutor: ExecutorService
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalysis: ImageAnalysis? = null
    
    // JNI external functions
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
        private const val REQUEST_CODE_PERMISSIONS = 10
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
        // Crear executor para camera
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // Crear UI
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        val btnAudioInit = Button(this)
        btnAudioInit.text = "Init Audio Engine"
        btnAudioInit.setOnClickListener { audioEngineInit() }
        
        val btnStartCamera = Button(this)
        btnStartCamera.text = "Start Camera"
        btnStartCamera.setOnClickListener { startCameraCapture() }
        
        val btnStop = Button(this)
        btnStop.text = "Stop All"
        btnStop.setOnClickListener { stopCamera() }
        
        layout.addView(btnAudioInit)
        layout.addView(btnStartCamera)
        layout.addView(btnStop)
        setContentView(layout)
        
        // Inicializar LibPD
        pdInit()
        Log.d(TAG, "App initialized")
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
        
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(android.util.Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        
        imageAnalysis?.setAnalyzer(cameraExecutor) { image ->
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
                val distance = 2.5f  // Placeholder
                
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
            "timestamp_ms": ${System.currentTimeMillis()},
            "frame_rate_hz": 30,
            "grid": {"rows": 16, "cols": 16},
            "cells": [${cells.joinToString(",")}]
        }"""
        
        sendControlJson(json)
    }
    
    private fun stopCamera() {
        cameraProvider?.unbindAll()
        audioEngineCleanup()
        cameraExecutor.shutdown()
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
    
    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}
```

#### 3.2: Actualizar native-lib.cpp - Integrar Mapeos (8 horas)

```cpp
// FILE: app/src/main/cpp/native-lib.cpp
// AGREGAR ESTA SECCIÓN DESPUÉS DE mapeos existentes:

#include "mapping_engine.h"
#include "json_parser.h"
#include "libpd_wrapper.h"
#include "audio_engine.h"

// REEMPLAZAR LA FUNCIÓN sendControlJson() stub CON:

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_sendControlJson(JNIEnv* env, jobject /* this */, jstring jjson) {
    const char* cstr = env->GetStringUTFChars(jjson, nullptr);
    std::string json_str(cstr ? cstr : "");
    env->ReleaseStringUTFChars(jjson, cstr);

    ALOG("[native] Processing JSON frame (%zu bytes)", json_str.length());

    // Parse JSON
    ControlFrame frame;
    if (!parse_control_schema(json_str.c_str(), &frame)) {
        ALOG("ERROR: Failed to parse JSON");
        return;
    }

    ALOG("Parsed %d cells", frame.num_cells);

    // Process each cell
    for (int i = 0; i < frame.num_cells && i < 10; ++i) {  // Process first 10 for now
        ControlCell& cell = frame.cells[i];
        
        // Map all parameters
        CellMappedParams params = map_cell_all_params(
            cell.azimuth_deg,
            cell.elevation_m,
            cell.distance_m,
            cell.luminance,
            cell.material,
            cell.confidence
        );

        // Send to LibPD
        libpd_wrapper_send_float("light-freq-left", (float)params.left_freq);
        libpd_wrapper_send_float("light-freq-right", (float)params.right_freq);
        libpd_wrapper_send_float("light-amp", (float)params.beat_amplitude);
        libpd_wrapper_send_float("distance-gain", (float)params.gain);
        libpd_wrapper_send_float("distance-lpf-cutoff", (float)params.lpf_cutoff);
        libpd_wrapper_send_float("material-density", (float)params.material_mod_index);

        ALOG("[CELL %d] Freq:%.1f Gain:%.3f Pan(L:%.3f R:%.3f) Lum:%.2f Mat:%s",
             i, params.freq, params.gain, params.left_pan, params.right_pan, 
             cell.luminance, cell.material);
    }

    ALOG("[native] Frame processed complete");
}
```

#### 3.3: Testing Semana 3-4

```
☐ Compilación sin errores
☐ App instala
☐ Permisos solicitados en runtime (CAMERA + RECORD_AUDIO)
☐ Botón "Start Camera" funciona (no crash)
☐ Botón "Init Audio" funciona + escuchar tono
☐ Cámara captura frames @ 30fps
☐ Logcat muestra: "Parsed X cells"
☐ Mapeos calculados correctamente (verificar logs)
☐ Audio cambia con movimiento de cámara (prueba manual)
☐ Latencia end-to-end <100ms (medible con timestamps)
✅ Semana 4 COMPLETA: MVP Fase 1 Funcional
```

---

## 🔒 Entregables Fase 1 (Semana 4)

### Código:
```
✅ libpd_wrapper.cpp/h        (180 líneas)
✅ audio_engine.cpp/h          (225 líneas)
✅ mapping_engine.cpp/h        (340 líneas)
✅ json_parser.cpp/h           (285 líneas)
✅ native-lib.cpp actualizado  (150 líneas)
✅ MainActivity.kt actualizado (350 líneas)
✅ CMakeLists.txt actualizado  (50 líneas)
✅ build.gradle actualizado    (10 líneas)

TOTAL: ~1,590 líneas de código
```

### Funcionalidad:
```
✅ LibPD inicializa y carga patch
✅ AAudio reproduce audio @ 44.1kHz
✅ Cámara captura @ 30fps
✅ 6 mapeos matemáticos funcionan
✅ JSON parse de control_schema
✅ End-to-end: cámara → mapeo → audio
✅ Latencia: <100ms
✅ 0 crashes en 1 hora de prueba
```

### Documentación:
```
✅ Inline comments en código
✅ README actualizado
✅ README.md en android_skeleton mejorado
```

### Testing:
```
✅ Compilación sin warnings mayores
✅ App instala en dispositivo/emulador
✅ Permis os funcionan (runtime)
✅ Audio audible en auriculares
✅ Cámara responsiva
✅ Mapeos correctos (verificar logs)
```

---

## 📊 RESUMEN FASE 1: 4 SEMANAS

```
SEMANA 1: LibPD Integration (40 horas)
  ├─ libpd_wrapper.cpp (180 líneas)
  ├─ mapping_engine.cpp (340 líneas)
  ├─ json_parser.cpp (285 líneas)
  └─ native-lib.cpp stubs → real (80 líneas)

SEMANA 2: Audio Engine (40 horas)
  ├─ audio_engine.cpp (225 líneas)
  ├─ CMakeLists.txt (50 líneas)
  ├─ build.gradle (10 líneas)
  └─ Testing & troubleshooting

SEMANA 3: Vision Frontend (45 horas)
  ├─ MainActivity.kt (350 líneas)
  ├─ Captura de cámara (CameraX)
  ├─ Generación de JSON
  └─ Integración de permisos runtime

SEMANA 4: Integration & Testing (35 horas)
  ├─ End-to-end testing
  ├─ Benchmarking latencia
  ├─ Bug fixes
  └─ Documentación

TOTAL FASE 1: 160 horas (4 FTE × 4 semanas)
RESULTADO: MVP Funcional ✅
```

---

## 🚀 TRANSICIÓN A FASE 2

Al final de Semana 4:

```
✅ Code review by tech lead
✅ Create release branch (release/v0.1.0-alpha)
✅ Tag: v0.1.0-alpha
✅ Deploy to internal beta tester
✅ Gather feedback
✅ Plan Fase 2 (Accesibilidad)

Semana 5 comienza: Fase 2 Kick-off
```

---

## 🔧 PROBLEMAS COMUNES & SOLUCIONES

### Problema: "Cannot find libpd.so"
**Solución:** Verificar ruta en CMakeLists.txt
```cmake
message(STATUS "Looking for: ${LIBPD_PATH}/../../../jniLibs/${ANDROID_ABI}/libpd.so")
```

### Problema: Camera no abre
**Solución:** Verificar permisos en runtime
```kotlin
if (!hasPermissions()) {
    requestPermissions()  // This must be called
}
```

### Problema: Audio no se escucha
**Solución:** 
1. Verificar volumen del dispositivo
2. Verificar AAudio stream está started
3. Conectar auriculares (algunos dispositivos requieren)

### Problema: Latencia >200ms
**Solución:**
1. Reducir buffer size (CMakeLists.txt)
2. Optimizar JSON parser
3. Profiler con Android Studio

---

## 📈 METRICS FASE 1

```
CÓDIGO:
  Total líneas: ~1,590
  Funciones nuevas: 25+
  Archivos nuevos: 8
  Modificaciones: 4 archivos existentes

PERFORMANCE:
  Latencia target: <100ms
  Actual esperado: 70-90ms
  CPU usage target: <15%
  Actual esperado: 8-12%
  Memory: <100MB
  
ESTABILIDAD:
  Crash rate target: 0%
  Test duration: 1 hour minimum
  Expected: 0 crashes

COMPLETITUD:
  Documentación: 100%
  Código: 100%
  Testing: 80% (más en Fase 4)
```

---

## ✅ GATE FASE 1 (Semana 4, Viernes)

**Criterios para PROCEDER a Fase 2:**

```
☐ Compilación: 0 errores, <5 warnings mayores
☐ Tests: App instala, abre, no crashea
☐ Audio: Funciona @ 44.1kHz, se escucha
☐ Cámara: Captura @ 30fps, frames analizados
☐ Mapeos: 6/6 funciones funcionan correctamente
☐ Latencia: <100ms medible
☐ Code review: Approved por tech lead
☐ Documentation: Inline comments + README
☐ Integration: End-to-end funciona

Si ✅ TODOS: PROCEDE A FASE 2
Si ❌ ALGUNO: ITERATE Semana 4 + 1 día
```

---

**Próximo documento:** FASE_2_ACCESIBILIDAD.md (Semanas 5-8)

