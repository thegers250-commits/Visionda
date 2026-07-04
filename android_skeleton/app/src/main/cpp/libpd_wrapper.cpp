// libpd_wrapper.cpp — Integración real con LibPD
#include "libpd_wrapper.h"
#include "libpd/include/libpd.h"
#include <android/log.h>
#include <mutex>
#include <string>
#include <cstring>

#define LOG_TAG "LibPDWrapper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static std::mutex  g_pd_mutex;
static bool        g_initialized   = false;
static void*       g_patch_handle  = nullptr;
static int         g_sample_rate   = 44100;
static const int   BLOCK_SIZE      = 64;   // frames por tick de LibPD

// ─────────────────────────────────────────────
// Callbacks de print y mensajes (debug)
// ─────────────────────────────────────────────
static void pd_print_hook(const char* msg) {
    LOGI("[PD] %s", msg);
}

// ─────────────────────────────────────────────
bool libpd_wrapper_init() {
    std::lock_guard<std::mutex> lock(g_pd_mutex);

    if (g_initialized) {
        LOGI("LibPD ya está inicializado");
        return true;
    }

    libpd_set_printhook(pd_print_hook);
    libpd_init();

    // Configurar 2 canales de salida, 0 de entrada, blocksize=64
    if (libpd_init_audio(0, 2, g_sample_rate) != 0) {
        LOGE("libpd_init_audio falló");
        return false;
    }

    // Activar DSP
    libpd_start_message(1);
    libpd_add_float(1.0f);
    libpd_finish_message("pd", "dsp");

    g_initialized = true;
    LOGI("LibPD inicializado @ %d Hz, blockSize=%d", g_sample_rate, BLOCK_SIZE);
    return true;
}

// ─────────────────────────────────────────────
bool libpd_wrapper_load_patch(const char* patch_path) {
    std::lock_guard<std::mutex> lock(g_pd_mutex);

    if (!g_initialized) {
        LOGE("LibPD no está inicializado — llama libpd_wrapper_init() primero");
        return false;
    }

    // Separar directorio y nombre de archivo
    std::string full(patch_path);
    size_t sep = full.rfind('/');
    std::string dir  = (sep == std::string::npos) ? "." : full.substr(0, sep);
    std::string file = (sep == std::string::npos) ? full : full.substr(sep + 1);

    void* handle = libpd_openfile(file.c_str(), dir.c_str());
    if (!handle) {
        LOGE("No se pudo abrir patch: %s en dir: %s", file.c_str(), dir.c_str());
        return false;
    }

    // Cerrar patch anterior si existía
    if (g_patch_handle) {
        libpd_closefile(g_patch_handle);
    }
    g_patch_handle = handle;

    LOGI("Patch cargado: %s", patch_path);
    return true;
}

// ─────────────────────────────────────────────
bool libpd_wrapper_send_float(const char* receiver, float value) {
    // No bloqueamos mutex aquí (puede llamarse desde audio callback)
    if (!g_initialized) return false;

    int ret = libpd_float(receiver, value);
    if (ret != 0) {
        LOGE("libpd_float(%s, %.3f) falló (ret=%d)", receiver, value, ret);
        return false;
    }
    return true;
}

// ─────────────────────────────────────────────
bool libpd_wrapper_process_tick(float* output, int frames) {
    if (!g_initialized) return false;

    // LibPD procesa en bloques de BLOCK_SIZE
    int ticks = frames / BLOCK_SIZE;
    // Sin entrada (nullptr), salida estéreo
    static float zero_in[BLOCK_SIZE * 1] = {};
    libpd_process_float(ticks, zero_in, output);
    return true;
}

// ─────────────────────────────────────────────
void libpd_wrapper_cleanup() {
    std::lock_guard<std::mutex> lock(g_pd_mutex);

    if (g_patch_handle) {
        libpd_closefile(g_patch_handle);
        g_patch_handle = nullptr;
        LOGI("Patch cerrado");
    }
    g_initialized = false;
    LOGI("LibPD limpiado");
}

bool libpd_wrapper_is_initialized() {
    return g_initialized;
}

int libpd_wrapper_get_sample_rate() {
    return g_sample_rate;
}
