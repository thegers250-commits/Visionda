# 🔧 FASE 1: TAREAS ESPECÍFICAS DE CÓDIGO

## Semana 1: LibPD Integration

### TAREA 1.1: mapping_engine.cpp - Funciones de Mapeo (6 HORAS)

**Archivo:** `app/src/main/cpp/mapping_engine.cpp`

Este archivo DEBE contener las 6 funciones de mapeo del manifiesto:

```cpp
#include "mapping_engine.h"
#include <cmath>
#include <android/log.h>

#define LOG_TAG "MappingEngine"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// ============= MAPEO 1: Elevación → Frecuencia =============
double map_elevation_to_freq(double elevation_m) {
    // f(h) = 60 * exp(1.7685 * h)
    const double f0 = 60.0;
    const double k = 1.7685;  // Para f(2.5) = 5000 Hz
    
    double freq = f0 * std::exp(k * elevation_m);
    ALOG("Elevation %.2fm → Freq %.1f Hz", elevation_m, freq);
    return freq;
}

// ============= MAPEO 2: Distancia → Ganancia + LPF =============
double map_distance_to_gain(double distance_m) {
    // G(r) = 1 / (1 + (r/r_ref)^2)
    const double r_ref = 1.0;
    double gain = 1.0 / (1.0 + (distance_m / r_ref) * (distance_m / r_ref));
    ALOG("Distance %.2fm → Gain %.3f", distance_m, gain);
    return gain;
}

double map_distance_to_lpf_cutoff(double distance_m) {
    // fc(r) = 12000 * exp(-0.18 * r)
    const double fc0 = 12000.0;
    const double c = 0.18;
    double fc = fc0 * std::exp(-c * distance_m);
    ALOG("Distance %.2fm → LPF cutoff %.1f Hz", distance_m, fc);
    return fc;
}

// ============= MAPEO 3: Azimut → Paneo Binaural =============
void map_azimuth_to_pan(double azimuth_deg, double& left_pan, double& right_pan) {
    // Normalizar azimuth en [-1, 1] para paneo
    double normalized = azimuth_deg / 90.0;  // Rango: [-1, 1]
    normalized = std::max(-1.0, std::min(1.0, normalized));  // Clamp
    
    // Equal-power panning
    double theta = (normalized + 1.0) / 2.0 * M_PI / 2.0;  // [0, π/2]
    left_pan = std::cos(theta);
    right_pan = std::sin(theta);
    
    ALOG("Azimuth %.1f° → Pan L:%.3f R:%.3f", azimuth_deg, left_pan, right_pan);
}

// ============= MAPEO 4: Luminancia → Binaural Beats =============
struct BinauraralBeatParams {
    double delta_hz;
    double left_freq;
    double right_freq;
    double amplitude;
};

BinauraralBeatParams map_luminance_to_binaural_beat(double luminance) {
    // Δ(L) = 5 + 7*L → [5-12] Hz
    double delta = 5.0 + 7.0 * luminance;
    delta = std::max(5.0, std::min(12.0, delta));  // Safety: clamp to 5-12 Hz
    
    // Carriers @ 4000 Hz
    const double fc = 4000.0;
    
    BinauraralBeatParams params;
    params.delta_hz = delta;
    params.left_freq = fc + delta / 2.0;
    params.right_freq = fc - delta / 2.0;
    params.amplitude = luminance * 0.5;  // Max 0.5 for safety
    
    ALOG("Luminance %.2f → Delta %.1f Hz (L:%.1f R:%.1f)", 
         luminance, delta, params.left_freq, params.right_freq);
    
    return params;
}

// ============= MAPEO 5: Material → Síntesis Parámetros =============
struct MaterialSynthParams {
    const char* material;
    double carrier_freq;
    double mod_index;
    double grain_density;
};

MaterialSynthParams map_material_to_synth_params(const char* material) {
    MaterialSynthParams params;
    params.material = material;
    
    if (std::strcmp(material, "metal") == 0) {
        params.carrier_freq = 5000.0;    // High carrier
        params.mod_index = 3.0;           // Strong modulation
        params.grain_density = 0.0;       // No grain
    } else if (std::strcmp(material, "wood") == 0) {
        params.carrier_freq = 300.0;     // Low carrier
        params.mod_index = 1.0;           // Moderate modulation
        params.grain_density = 0.0;
    } else if (std::strcmp(material, "stone") == 0) {
        params.carrier_freq = 100.0;     // Very low
        params.mod_index = 0.5;
        params.grain_density = 0.7;      // High grain density
    } else {
        params.carrier_freq = 440.0;     // Default
        params.mod_index = 1.0;
        params.grain_density = 0.2;
    }
    
    ALOG("Material %s → Carrier:%.0f Hz, ModIdx:%.1f, Grain:%.1f",
         material, params.carrier_freq, params.mod_index, params.grain_density);
    
    return params;
}

// ============= MAPEO 6: Confidence → Mixtura =============
double map_confidence_to_amplitude_scale(double confidence) {
    // Confidence [0, 1] → Amplitude scale [0, 1]
    // Usar smoothstep para suavizar cambios
    double t = confidence * confidence * (3.0 - 2.0 * confidence);
    ALOG("Confidence %.2f → Amp scale %.3f", confidence, t);
    return t;
}

// ============= UTILIDADES =============
struct CellMappedParams {
    double freq;           // From elevation
    double gain;           // From distance
    double lpf_cutoff;     // From distance
    double left_pan;       // From azimuth
    double right_pan;      // From azimuth
    double left_freq;      // Binaural beat
    double right_freq;     // Binaural beat
    double beat_amplitude; // Luminance
    const char* material;
    double material_carrier;
    double material_mod_index;
    double confidence_scale;
};

CellMappedParams map_cell_all_params(
    double azimuth_deg,
    double elevation_m,
    double distance_m,
    double luminance,
    const char* material,
    double confidence) {
    
    CellMappedParams out;
    
    out.freq = map_elevation_to_freq(elevation_m);
    out.gain = map_distance_to_gain(distance_m);
    out.lpf_cutoff = map_distance_to_lpf_cutoff(distance_m);
    
    map_azimuth_to_pan(azimuth_deg, out.left_pan, out.right_pan);
    
    auto beat = map_luminance_to_binaural_beat(luminance);
    out.left_freq = beat.left_freq;
    out.right_freq = beat.right_freq;
    out.beat_amplitude = beat.amplitude;
    
    auto mat = map_material_to_synth_params(material);
    out.material = mat.material;
    out.material_carrier = mat.carrier_freq;
    out.material_mod_index = mat.mod_index;
    
    out.confidence_scale = map_confidence_to_amplitude_scale(confidence);
    
    ALOG("[CELL MAPPING] All params computed successfully");
    return out;
}
```

### TAREA 1.2: mapping_engine.h (1 HORA)

```cpp
// FILE: app/src/main/cpp/mapping_engine.h
#ifndef MAPPING_ENGINE_H
#define MAPPING_ENGINE_H

struct BinauraralBeatParams {
    double delta_hz;
    double left_freq;
    double right_freq;
    double amplitude;
};

struct MaterialSynthParams {
    const char* material;
    double carrier_freq;
    double mod_index;
    double grain_density;
};

struct CellMappedParams {
    double freq;
    double gain;
    double lpf_cutoff;
    double left_pan;
    double right_pan;
    double left_freq;
    double right_freq;
    double beat_amplitude;
    const char* material;
    double material_carrier;
    double material_mod_index;
    double confidence_scale;
};

// Función principal
CellMappedParams map_cell_all_params(
    double azimuth_deg,
    double elevation_m,
    double distance_m,
    double luminance,
    const char* material,
    double confidence);

#endif  // MAPPING_ENGINE_H
```

### TAREA 1.3: json_parser.cpp (8 HORAS)

```cpp
// FILE: app/src/main/cpp/json_parser.cpp
#include "json_parser.h"
#include <android/log.h>
#include <cstring>
#include <cstdlib>

#define LOG_TAG "JSONParser"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Simple JSON parser (no STL, portátil)
// Asume formato específico de control_schema.json

struct ControlFrame {
    int num_cells;
    ControlCell cells[256];  // Max 16x16 grid
};

static double extract_double(const char* json, const char* key) {
    const char* pos = std::strstr(json, key);
    if (!pos) return 0.0;
    
    pos = std::strchr(pos, ':');
    if (!pos) return 0.0;
    
    return std::strtod(pos + 1, nullptr);
}

static void extract_string(const char* json, const char* key, char* out, int max_len) {
    const char* pos = std::strstr(json, key);
    if (!pos) {
        out[0] = '\0';
        return;
    }
    
    pos = std::strchr(pos, ':');
    if (!pos) {
        out[0] = '\0';
        return;
    }
    
    // Skip spaces and quotes
    while (*pos && (*pos == ':' || *pos == ' ' || *pos == '"')) pos++;
    
    int i = 0;
    while (*pos && *pos != '"' && *pos != ',' && i < max_len - 1) {
        out[i++] = *pos++;
    }
    out[i] = '\0';
}

bool parse_control_schema(const char* json, ControlFrame* frame) {
    if (!json || !frame) {
        ALOG("ERROR: Null pointer");
        return false;
    }
    
    // Parse cells array (simplified: assume first cell for now)
    frame->num_cells = 0;
    
    const char* cells_pos = std::strstr(json, "\"cells\"");
    if (!cells_pos) {
        ALOG("ERROR: No cells array found");
        return false;
    }
    
    // Find first cell object
    const char* cell_pos = std::strstr(cells_pos, "{");
    int cell_count = 0;
    
    while (cell_pos && cell_count < 256) {
        ControlCell cell;
        
        // Extract fields
        cell.id = (int)extract_double(cell_pos, "\"id\"");
        cell.row = (int)extract_double(cell_pos, "\"row\"");
        cell.col = (int)extract_double(cell_pos, "\"col\"");
        cell.azimuth_deg = extract_double(cell_pos, "\"azimuth_deg\"");
        cell.elevation_m = extract_double(cell_pos, "\"elevation_m\"");
        cell.distance_m = extract_double(cell_pos, "\"distance_m\"");
        cell.luminance = extract_double(cell_pos, "\"luminance\"");
        cell.confidence = extract_double(cell_pos, "\"confidence\"");
        
        extract_string(cell_pos, "\"material\"", cell.material, 32);
        
        frame->cells[cell_count++] = cell;
        ALOG("Cell %d: az=%.1f el=%.2f dist=%.2f lum=%.2f mat=%s",
             cell.id, cell.azimuth_deg, cell.elevation_m, 
             cell.distance_m, cell.luminance, cell.material);
        
        // Find next cell
        const char* next_cell = std::strstr(cell_pos + 1, "{");
        const char* end_cells = std::strstr(cell_pos + 1, "]");
        
        if (!next_cell || (end_cells && next_cell > end_cells)) {
            break;
        }
        cell_pos = next_cell;
    }
    
    frame->num_cells = cell_count;
    ALOG("Parsed %d cells", cell_count);
    return cell_count > 0;
}
```

### TAREA 1.4: json_parser.h (1 HORA)

```cpp
// FILE: app/src/main/cpp/json_parser.h
#ifndef JSON_PARSER_H
#define JSON_PARSER_H

typedef struct {
    int id;
    int row;
    int col;
    double azimuth_deg;
    double elevation_m;
    double distance_m;
    double luminance;
    double confidence;
    char material[32];
} ControlCell;

bool parse_control_schema(const char* json, struct ControlFrame* frame);

#endif  // JSON_PARSER_H
```

### TAREA 1.5: Actualizar CMakeLists.txt (1 HORA)

```cmake
# Agregar estas líneas al CMakeLists.txt existente:

add_library(native-lib SHARED 
    src/main/cpp/native-lib.cpp
    src/main/cpp/libpd_wrapper.cpp
    src/main/cpp/audio_engine.cpp
    src/main/cpp/mapping_engine.cpp
    src/main/cpp/json_parser.cpp
)

# Include directories
target_include_directories(native-lib PRIVATE
    ${LIBPD_PATH}/include
    src/main/cpp
)

# Link all libraries
target_link_libraries(native-lib
    ${log-lib}
    ${audiotrack}
    ${aaudio-lib}
    ${LIBPD_PATH}/../../../jniLibs/${ANDROID_ABI}/libpd.so
)

set_property(TARGET native-lib PROPERTY CXX_STANDARD 17)
```

---

## Semana 2: Audio Engine (Continuación)

(Ver PLAN_EJECUCION_COMPLETO.md)

---

## ✅ CHECKLIST SEMANA 1

```
☐ libpd_wrapper.cpp creado (150 líneas)
☐ libpd_wrapper.h creado (30 líneas)
☐ mapping_engine.cpp creado (300 líneas)
☐ mapping_engine.h creado (40 líneas)
☐ json_parser.cpp creado (250 líneas)
☐ json_parser.h creado (35 líneas)
☐ native-lib.cpp reemplazar stubs (80 líneas)
☐ CMakeLists.txt actualizado (40 líneas)
☐ Compilación sin errores
☐ App instala sin crash
☐ Botón "Init PD" funciona (Logcat: "LibPD initialized")
☐ Botón "Load Patch" funciona (Logcat: "Patch loaded")
☐ Botón "Send sample frame" funciona (mapeos calculados)
```

**TOTAL SEMANA 1:** ~825 líneas de código

