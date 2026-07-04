# 📅 SEMANA 2 - PLAN DETALLADO

**Fase 1, Week 2: Audio Engine (AAudio) + Integration**  
**Duración:** 5 días de trabajo (Lunes-Viernes)  
**Objetivo:** AAudio funcional, binaural audio, <100ms latencia

---

## 🎯 OBJETIVO SEMANA 2

```
ENTRADA:  LibPD funcional (Semana 1)
PROCESO:  Crear Audio Engine con AAudio
SALIDA:   Audio reproduciendo a 44.1kHz, audible
```

---

## 📊 TIMELINE DIARIO

### LUNES (Día 6) - Setup AAudio
**Duración:** 6 horas  
**Objetivo:** AAudio streaming funcional

#### Tarea 2.1: Crear audio_engine.h (1 hora)
**Archivo:** `android_skeleton/app/src/main/cpp/audio_engine.h`

```cpp
#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#ifdef __cplusplus
extern "C" {
#endif

// Inicializar AAudio stream
bool audio_engine_init();

// Setear frecuencia (Hz)
void audio_engine_set_frequency(float freq_hz);

// Setear amplitud (0.0 - 1.0, pero limitamos a 0.5 max)
void audio_engine_set_amplitude(float amp);

// Setear pan estéreo (0.0=left, 0.5=center, 1.0=right)
void audio_engine_set_pan(float pan);

// Procesar buffer de audio (callback)
int32_t audio_engine_process_frame(float* output_buffer, int32_t num_frames);

// Detener stream
void audio_engine_stop();

// Cleanup
void audio_engine_cleanup();

// Get estado
bool audio_engine_is_running();
int32_t audio_engine_get_sample_rate();
int32_t audio_engine_get_latency_ms();

#ifdef __cplusplus
}
#endif

#endif // AUDIO_ENGINE_H
```

#### Tarea 2.2: Crear audio_engine.cpp (3 horas)
**Archivo:** `android_skeleton/app/src/main/cpp/audio_engine.cpp`

```cpp
#include "audio_engine.h"
#include <aaudio/AAudio.h>
#include <android/log.h>
#include <cmath>
#include <mutex>
#include <atomic>

#define LOG_TAG "AudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Global state
static AAudioStream* g_stream = nullptr;
static std::mutex g_mutex;

// Audio parameters
static std::atomic<float> g_frequency(4000.0f);
static std::atomic<float> g_amplitude(0.1f);
static std::atomic<float> g_pan(0.5f);
static std::atomic<float> g_phase(0.0f);

// Audio callback - Llamado por AAudio engine
static aaudio_data_callback_result_t audio_callback(
    AAudioStream* stream,
    void* userData,
    void* audioData,
    int32_t numFrames) {
    
    float* output = (float*)audioData;
    int32_t sample_rate = AAudioStream_getSampleRate(stream);
    
    float freq = g_frequency.load();
    float amp = g_amplitude.load();
    float pan = g_pan.load();
    
    // Generate binaural audio (2 channels)
    for (int32_t i = 0; i < numFrames; ++i) {
        // Simple sine wave
        float sample = sinf(g_phase.load() * 2.0f * M_PI);
        
        // Apply amplitude
        sample *= amp;
        
        // Stereo panning
        output[i * 2 + 0] = sample * (1.0f - pan);  // Left
        output[i * 2 + 1] = sample * pan;            // Right
        
        // Update phase
        float phase_increment = freq / (float)sample_rate;
        float new_phase = g_phase.load() + phase_increment;
        if (new_phase > 1.0f) new_phase -= 1.0f;
        g_phase.store(new_phase);
    }
    
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

// Initialize AAudio engine
bool audio_engine_init() {
    std::lock_guard<std::mutex> lock(g_mutex);
    
    if (g_stream != nullptr) {
        LOGI("Audio engine already initialized");
        return true;
    }
    
    LOGI("Initializing AAudio engine...");
    
    AAudioStreamBuilder* builder = nullptr;
    aaudio_result_t result = AAudio_createStreamBuilder(&builder);
    
    if (result != AAUDIO_OK) {
        LOGE("Failed to create stream builder: %s", 
             AAudio_convertResultToText(result));
        return false;
    }
    
    // Configure stream
    AAudioStreamBuilder_setDirection(builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate(builder, 44100);
    AAudioStreamBuilder_setChannelCount(builder, 2);  // Stereo
    AAudioStreamBuilder_setFormat(builder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setPerformanceMode(builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setSharingMode(builder, AAUDIO_SHARING_MODE_EXCLUSIVE);
    AAudioStreamBuilder_setDataCallback(builder, audio_callback, nullptr);
    
    // Open stream
    result = AAudioStreamBuilder_openStream(builder, &g_stream);
    AAudioStreamBuilder_delete(builder);
    
    if (result != AAUDIO_OK) {
        LOGE("Failed to open stream: %s", 
             AAudio_convertResultToText(result));
        g_stream = nullptr;
        return false;
    }
    
    // Start stream
    result = AAudioStream_requestStart(g_stream);
    if (result != AAUDIO_OK) {
        LOGE("Failed to start stream: %s", 
             AAudio_convertResultToText(result));
        AAudioStream_close(g_stream);
        g_stream = nullptr;
        return false;
    }
    
    int32_t sr = AAudioStream_getSampleRate(g_stream);
    LOGI("✅ AAudio engine initialized @ %d Hz", sr);
    
    return true;
}

void audio_engine_set_frequency(float freq_hz) {
    g_frequency.store(freq_hz);
    LOGI("Frequency set to %.1f Hz", freq_hz);
}

void audio_engine_set_amplitude(float amp) {
    // Limit to 0.5 for safety
    float limited_amp = amp > 0.5f ? 0.5f : amp;
    g_amplitude.store(limited_amp);
    LOGI("Amplitude set to %.3f", limited_amp);
}

void audio_engine_set_pan(float pan) {
    float clamped_pan = (pan < 0.0f) ? 0.0f : (pan > 1.0f ? 1.0f : pan);
    g_pan.store(clamped_pan);
    LOGI("Pan set to %.3f", clamped_pan);
}

int32_t audio_engine_process_frame(float* output_buffer, int32_t num_frames) {
    // This is called externally if needed
    // Usually AAudio handles this internally via callback
    return num_frames;
}

void audio_engine_stop() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream) {
        AAudioStream_requestStop(g_stream);
        LOGI("Audio engine stopped");
    }
}

void audio_engine_cleanup() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream) {
        AAudioStream_requestStop(g_stream);
        AAudioStream_close(g_stream);
        g_stream = nullptr;
        LOGI("Audio engine cleaned up");
    }
}

bool audio_engine_is_running() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream == nullptr) return false;
    
    aaudio_stream_state_t state = AAudioStream_getState(g_stream);
    return state == AAUDIO_STREAM_STATE_STARTED;
}

int32_t audio_engine_get_sample_rate() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream == nullptr) return 0;
    return AAudioStream_getSampleRate(g_stream);
}

int32_t audio_engine_get_latency_ms() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream == nullptr) return 0;
    int32_t frames = AAudioStream_getFramesPerBurst(g_stream);
    int32_t sr = AAudioStream_getSampleRate(g_stream);
    return (frames * 1000) / sr;
}
```

#### Tarea 2.3: Actualizar CMakeLists.txt (1 hora)
**Modificar:** `android_skeleton/app/src/main/cpp/CMakeLists.txt`

Agregar después de `add_library`:

```cmake
# Source files
set(NATIVE_SRCS
    ${CMAKE_CURRENT_SOURCE_DIR}/native-lib.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/libpd_wrapper.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/audio_engine.cpp
)

add_library(native-lib SHARED ${NATIVE_SRCS})

# Link AAudio
find_library(AAudio-lib aaudio)
target_link_libraries(native-lib 
    PUBLIC
    ${AAudio-lib}
    ${LIBPD_PATH}/../../jniLibs/${ANDROID_ABI}/libpd.so
)
```

#### Tarea 2.4: Crear JNI wrapper (1 hora)
**Modificar:** `android_skeleton/app/src/main/cpp/native-lib.cpp`

Agregar:

```cpp
#include "audio_engine.h"

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineInit(JNIEnv* env, jobject) {
    LOGI("[JNI] audioEngineInit() called");
    if (audio_engine_init()) {
        LOGI("[JNI] ✅ Audio engine initialized");
    } else {
        LOGE("[JNI] ❌ Audio engine init failed");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineSetFreq(JNIEnv* env, jobject, jfloat freq) {
    audio_engine_set_frequency((float)freq);
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineSetAmp(JNIEnv* env, jobject, jfloat amp) {
    audio_engine_set_amplitude((float)amp);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineGetLatency(JNIEnv* env, jobject) {
    return (jint)audio_engine_get_latency_ms();
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineCleanup(JNIEnv* env, jobject) {
    LOGI("[JNI] audioEngineCleanup() called");
    audio_engine_cleanup();
}
```

#### 🎯 FIN LUNES
```
✅ audio_engine.h creado
✅ audio_engine.cpp creado
✅ CMakeLists.txt actualizado
✅ JNI functions agregadas
```

---

### MARTES-MIÉRCOLES (Días 7-8) - Integration + Testing
**Duración:** 6-8 horas  
**Objetivo:** Audio audible

#### Tarea 2.5: Actualizar MainActivity UI (2 horas)
Agregar botones:
- "Init Audio"
- "Set Freq 1000Hz"
- "Set Freq 2000Hz"
- "Stop Audio"

#### Tarea 2.6: Testing (4-6 horas)
```
TESTING:
☐ Compilación sin errors
☐ App instala
☐ "Init Audio" → Escuchas tono
☐ "Set Freq 1000Hz" → Cambias tono
☐ "Set Freq 2000Hz" → Cambias tono
☐ Latencia <100ms (medible)
☐ 0 crashes
```

---

### JUEVES (Día 9) - Performance + Optimization
**Duración:** 4 horas  
**Objetivo:** Latencia <100ms

#### Tareas:
```
☐ Medir latencia actual
☐ Optimizar si >100ms
☐ Profile CPU usage
☐ Verify no underruns
```

---

### VIERNES (Día 10) - Gate + Documentation
**Duración:** 2-3 horas  
**Objetivo:** Gate Semana 2

#### 🎯 GATE CRITERIA SEMANA 2

```
☐ Compilación: ✅ 0 errores
☐ Audio plays: ✅ Audible @ 44.1kHz
☐ Frequency control: ✅ Funciona
☐ Latency: ✅ <100ms (target: 70-80ms)
☐ Crashes: 0
☐ CPU usage: <15%

SI ✅ TODOS: PROCEDE WEEK 3 (Vision + Camera)
```

---

## 📊 RESUMEN SEMANA 2

```
LUNES-MARTES:   ✅ Audio engine + JNI
MIÉRCOLES:      ✅ Integration + UI
JUEVES:         ✅ Performance tuning
VIERNES:        ✅ Testing + Gate

RESULTADO: AAudio funcional, <100ms latencia
```

---

## 🎓 PRÓXIMA: SEMANA 3

Una vez Semana 2 gate:

**Semana 3:** Camera integration + Vision processing
- Capturar frames @ 30fps
- Procesar grid 16x16
- Enviar a LibPD
- Escuchar cambios de audio con movimiento de cámara

---

## ✅ CONCLUSIÓN

**Semana 2 = Audio en TIEMPO REAL**

Esto es donde Visualonda comienza a ser **COOL** 🎉

Usuarios podrán escuchar audio en tiempo real.

