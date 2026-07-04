// native-lib.cpp — JNI bridge: Kotlin ↔ C++ (LibPD + AAudio + Mapeos)
#include <jni.h>
#include <string>
#include <android/log.h>
#include <cmath>

#include "libpd_wrapper.h"
#include "audio_engine.h"

#define LOG_TAG "VisualondaNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// ═══════════════════════════════════════════════════════════════════════
//  MAPEOS MATEMÁTICOS (6 mappings camera → audio)
// ═══════════════════════════════════════════════════════════════════════

// 1. Elevación → Frecuencia   [0 m, 2.5 m] → [60 Hz, 5000 Hz]
static double elevation_to_freq(double h) {
    const double f0 = 60.0;
    const double k  = 1.7685;   // k = ln(5000/60) / 2.5
    return f0 * std::exp(k * h);
}

// 2. Distancia → Ganancia     [0 m, ∞] → [1.0, 0.0]
static double distance_gain(double r) {
    const double r_ref = 1.0;
    return 1.0 / (1.0 + (r / r_ref) * (r / r_ref));
}

// 3. Distancia → LPF cutoff   [0 m, 10 m] → [12000 Hz, ~1500 Hz]
static double distance_lpf(double r) {
    return 12000.0 * std::exp(-0.18 * r);
}

// 4. Azimut → Pan estéreo     [-90°, +90°] → [0.0, 1.0]
static double azimuth_to_pan(double az_deg) {
    return (az_deg + 90.0) / 180.0;
}

// 5. Luminancia → Beat delta  [0.0, 1.0] → [2 Hz, 12 Hz]
static double luminance_to_beat(double lum) {
    return 2.0 + 10.0 * lum;
}

// 6. Material → índice de modulación (simplificado)
static double material_mod_index(const std::string& mat) {
    if (mat == "metal")   return 3.5;
    if (mat == "glass")   return 2.8;
    if (mat == "wood")    return 1.2;
    if (mat == "fabric")  return 0.6;
    if (mat == "skin")    return 0.4;
    return 1.0;  // default
}

// ═══════════════════════════════════════════════════════════════════════
//  MINI PARSER JSON (robusto para control_schema)
// ═══════════════════════════════════════════════════════════════════════

static bool extract_number(const std::string& s, const std::string& key, double& out) {
    size_t pos = s.find("\"" + key + "\"");
    if (pos == std::string::npos) return false;
    pos = s.find_first_of("-0123456789", pos + key.length() + 2);
    if (pos == std::string::npos) return false;
    size_t end = pos;
    while (end < s.size() &&
           (s[end] == '-' || s[end] == '+' || s[end] == '.' ||
            s[end] == 'e' || s[end] == 'E' ||
            (s[end] >= '0' && s[end] <= '9'))) {
        ++end;
    }
    try { out = std::stod(s.substr(pos, end - pos)); return true; }
    catch (...) { return false; }
}

static std::string extract_string(const std::string& s, const std::string& key) {
    size_t pos = s.find("\"" + key + "\"");
    if (pos == std::string::npos) return "";
    pos = s.find('"', pos + key.length() + 3);
    if (pos == std::string::npos) return "";
    size_t end = s.find('"', pos + 1);
    if (end == std::string::npos) return "";
    return s.substr(pos + 1, end - pos - 1);
}

// ═══════════════════════════════════════════════════════════════════════
//  JNI — LibPD
// ═══════════════════════════════════════════════════════════════════════

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(JNIEnv*, jobject) {
    LOGI("[JNI] pdInit()");
    if (libpd_wrapper_init()) {
        LOGI("[JNI] ✅ LibPD inicializado");
    } else {
        LOGE("[JNI] ❌ LibPD falló al inicializar");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdOpenPatch(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    LOGI("[JNI] pdOpenPatch(%s)", path);
    if (!libpd_wrapper_load_patch(path)) {
        LOGE("[JNI] ❌ No se pudo cargar patch: %s", path);
    }
    env->ReleaseStringUTFChars(jpath, path);
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdSendFloat(JNIEnv* env, jobject, jstring jname, jfloat value) {
    const char* name = env->GetStringUTFChars(jname, nullptr);
    libpd_wrapper_send_float(name, value);
    env->ReleaseStringUTFChars(jname, name);
}

// ═══════════════════════════════════════════════════════════════════════
//  JNI — Audio Engine
// ═══════════════════════════════════════════════════════════════════════

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineInit(JNIEnv*, jobject) {
    LOGI("[JNI] audioEngineInit()");
    if (audio_engine_init()) {
        LOGI("[JNI] ✅ Audio engine corriendo");
    } else {
        LOGE("[JNI] ❌ Audio engine falló");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineCleanup(JNIEnv*, jobject) {
    LOGI("[JNI] audioEngineCleanup()");
    audio_engine_cleanup();
}

extern "C" JNIEXPORT jint JNICALL
Java_com_visualonda_sensory_MainActivity_audioEngineGetLatency(JNIEnv*, jobject) {
    return static_cast<jint>(audio_engine_get_latency_ms());
}

// ═══════════════════════════════════════════════════════════════════════
//  JNI — Control Schema (camera frame → audio)
// ═══════════════════════════════════════════════════════════════════════

extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_sendControlJson(JNIEnv* env, jobject, jstring jjson) {
    const char* cstr = env->GetStringUTFChars(jjson, nullptr);
    std::string s(cstr ? cstr : "");
    env->ReleaseStringUTFChars(jjson, cstr);

    if (s.empty()) return;

    // Extraer parámetros de la PRIMERA celda del JSON
    double az = 0, elev = 0, dist = 1, lum = 0.5;
    extract_number(s, "azimuth_deg",  az);
    extract_number(s, "elevation_m",  elev);
    extract_number(s, "distance_m",   dist);
    extract_number(s, "luminance",    lum);
    std::string mat = extract_string(s, "material");
    if (mat.empty()) mat = "wood";

    // Calcular todos los mapeos
    double freq       = elevation_to_freq(elev);
    double gain       = distance_gain(dist);
    double lpf        = distance_lpf(dist);
    double pan        = azimuth_to_pan(az);
    double beat_delta = luminance_to_beat(lum);
    double mod_idx    = material_mod_index(mat);

    double left_freq  = freq + beat_delta / 2.0;
    double right_freq = freq - beat_delta / 2.0;

    LOGI("[MAP] az=%.1f° elev=%.2fm dist=%.2fm lum=%.2f mat=%s",
         az, elev, dist, lum, mat.c_str());
    LOGI("[MAP] freq=%.1fHz gain=%.3f lpf=%.1fHz pan=%.2f beat=%.1fHz mod=%.2f",
         freq, gain, lpf, pan, beat_delta, mod_idx);

    // Enviar a LibPD si está disponible
    if (libpd_wrapper_is_initialized()) {
        libpd_wrapper_send_float("light-freq-left",   (float)left_freq);
        libpd_wrapper_send_float("light-freq-right",  (float)right_freq);
        libpd_wrapper_send_float("distance-gain",     (float)gain);
        libpd_wrapper_send_float("distance-lpf",      (float)lpf);
        libpd_wrapper_send_float("azimuth-pan",       (float)pan);
        libpd_wrapper_send_float("luminance-beat",    (float)beat_delta);
        libpd_wrapper_send_float("material-mod",      (float)mod_idx);
    } else {
        // LibPD no disponible: actualizar oscilador de fallback directo
        audio_engine_set_frequency((float)freq);
        audio_engine_set_amplitude((float)(gain * 0.4));
        audio_engine_set_pan((float)pan);
    }
}
