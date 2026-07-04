// audio_engine.cpp — Motor de audio real con AAudio + LibPD
#include "audio_engine.h"
#include "libpd_wrapper.h"
#include <aaudio/AAudio.h>
#include <android/log.h>
#include <cmath>
#include <atomic>
#include <mutex>

#define LOG_TAG "AudioEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// ─── Estado global ────────────────────────────────────────────────────────────
static AAudioStream*        g_stream    = nullptr;
static std::mutex           g_mutex;

// Parámetros del oscilador de fallback (cuando LibPD no está activo)
static std::atomic<float>   g_freq      {4000.0f};
static std::atomic<float>   g_amp       {0.08f};
static std::atomic<float>   g_pan       {0.5f};
static std::atomic<float>   g_phase     {0.0f};

// ─── Audio callback ──────────────────────────────────────────────────────────
// Se ejecuta en el hilo de audio de baja latencia — NO bloquear, NO alloc
static aaudio_data_callback_result_t audio_callback(
        AAudioStream* stream,
        void*         /*userData*/,
        void*         audioData,
        int32_t       numFrames) {

    float* out = reinterpret_cast<float*>(audioData);
    const int32_t sr = AAudioStream_getSampleRate(stream);

    // Si LibPD está listo, procesa con él
    if (libpd_wrapper_is_initialized()) {
        libpd_wrapper_process_tick(out, numFrames);
        return AAUDIO_CALLBACK_RESULT_CONTINUE;
    }

    // Fallback: tono de prueba (seno estéreo)
    const float freq = g_freq.load(std::memory_order_relaxed);
    const float amp  = g_amp.load (std::memory_order_relaxed);
    const float pan  = g_pan.load (std::memory_order_relaxed);
    float phase      = g_phase.load(std::memory_order_relaxed);
    const float inc  = freq / static_cast<float>(sr);

    for (int32_t i = 0; i < numFrames; ++i) {
        float s = sinf(phase * 2.0f * M_PI) * amp;
        out[i * 2 + 0] = s * (1.0f - pan);   // L
        out[i * 2 + 1] = s * pan;             // R
        phase += inc;
        if (phase >= 1.0f) phase -= 1.0f;
    }
    g_phase.store(phase, std::memory_order_relaxed);

    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

// ─── Error callback ──────────────────────────────────────────────────────────
static void error_callback(AAudioStream* /*stream*/, void* /*userData*/, aaudio_result_t error) {
    LOGE("AAudio error callback: %s", AAudio_convertResultToText(error));
}

// ─────────────────────────────────────────────────────────────────────────────
bool audio_engine_init() {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (g_stream) {
        LOGI("Audio engine ya está corriendo");
        return true;
    }

    AAudioStreamBuilder* builder = nullptr;
    aaudio_result_t res = AAudio_createStreamBuilder(&builder);
    if (res != AAUDIO_OK) {
        LOGE("AAudio_createStreamBuilder: %s", AAudio_convertResultToText(res));
        return false;
    }

    AAudioStreamBuilder_setDirection       (builder, AAUDIO_DIRECTION_OUTPUT);
    AAudioStreamBuilder_setSampleRate      (builder, 44100);
    AAudioStreamBuilder_setChannelCount    (builder, 2);
    AAudioStreamBuilder_setFormat          (builder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setPerformanceMode (builder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setSharingMode     (builder, AAUDIO_SHARING_MODE_EXCLUSIVE);
    AAudioStreamBuilder_setDataCallback    (builder, audio_callback,  nullptr);
    AAudioStreamBuilder_setErrorCallback   (builder, error_callback,  nullptr);

    res = AAudioStreamBuilder_openStream(builder, &g_stream);
    AAudioStreamBuilder_delete(builder);

    if (res != AAUDIO_OK) {
        LOGE("openStream: %s", AAudio_convertResultToText(res));
        g_stream = nullptr;
        return false;
    }

    res = AAudioStream_requestStart(g_stream);
    if (res != AAUDIO_OK) {
        LOGE("requestStart: %s", AAudio_convertResultToText(res));
        AAudioStream_close(g_stream);
        g_stream = nullptr;
        return false;
    }

    LOGI("✅ AAudio engine OK @ %d Hz, latencia ~%d ms",
         AAudioStream_getSampleRate(g_stream),
         (AAudioStream_getFramesPerBurst(g_stream) * 1000)
           / AAudioStream_getSampleRate(g_stream));
    return true;
}

void audio_engine_set_frequency(float hz)  { g_freq.store(hz);  }
void audio_engine_set_amplitude(float amp) { g_amp.store(amp > 0.5f ? 0.5f : amp); }
void audio_engine_set_pan(float pan)       { g_pan.store(pan < 0.f ? 0.f : pan > 1.f ? 1.f : pan); }

void audio_engine_stop() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream) AAudioStream_requestStop(g_stream);
}

void audio_engine_cleanup() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_stream) {
        AAudioStream_requestStop(g_stream);
        AAudioStream_close(g_stream);
        g_stream = nullptr;
        LOGI("Audio engine limpiado");
    }
}

bool audio_engine_is_running() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (!g_stream) return false;
    return AAudioStream_getState(g_stream) == AAUDIO_STREAM_STATE_STARTED;
}

int32_t audio_engine_get_sample_rate() {
    std::lock_guard<std::mutex> lock(g_mutex);
    return g_stream ? AAudioStream_getSampleRate(g_stream) : 0;
}

int32_t audio_engine_get_latency_ms() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (!g_stream) return 0;
    int32_t burst = AAudioStream_getFramesPerBurst(g_stream);
    int32_t sr    = AAudioStream_getSampleRate(g_stream);
    return (sr > 0) ? (burst * 1000 / sr) : 0;
}
