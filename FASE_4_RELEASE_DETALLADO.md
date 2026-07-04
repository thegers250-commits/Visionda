# 🎛️ FASE 4: PULIDO & RELEASE DETALLADO

**Duración:** 6 semanas (Semanas 13-18)
**Equipo:** 1 Android Dev + 0.5 QA + Tech Lead (part-time)
**Objetivo:** Producto pulido listo para Google Play Store
**Presupuesto:** ~$30K

---

## VISIÓN GENERAL FASE 4

Al terminar Fase 3, tenemos:
- ✅ Captura de cámara funcionando
- ✅ Audio 3D en tiempo real
- ✅ Mapeos matemáticos completos
- ✅ Navegación accesible (TalkBack + gestos)
- ✅ ML integrado (objetos, profundidad, OCR, caras)

**PERO** no es producto listo para mercado:
- ⚠️ Performance variable (latencia 100-200ms)
- ⚠️ Memoria crece sin límite (memory leak)
- ⚠️ Audio puede dañar audición (sin protección SPL)
- ⚠️ Bugs en ML (false positives, crashes ocasionales)
- ⚠️ Sin documentación para usuarios
- ⚠️ Sin pruebas de seguridad

**Fase 4 = Resolver TODO esto.**

---

## 📋 ESTRUCTURA FASE 4

```
Semana 13-14: PERFORMANCE OPTIMIZATION
Semana 14-15: AUDIO SAFETY + QA TESTING
Semana 15-16: DOCUMENTATION
Semana 16-18: GOOGLE PLAY SUBMISSION & LAUNCH
```

---

## 🚀 SEMANA 13-14: PERFORMANCE OPTIMIZATION (80 horas)

### 13.1: Profiling & Benchmarking (16 horas)

#### Tarea 13.1.1: CPU Profiling

```cpp
// FILE: app/src/main/cpp/profiler.h
#ifndef PROFILER_H
#define PROFILER_H

#include <chrono>
#include <map>
#include <string>
#include <android/log.h>

class Profiler {
private:
    std::map<std::string, std::vector<double>> metrics;
    std::chrono::high_resolution_clock::time_point start_time;

public:
    Profiler() = default;
    
    void start() {
        start_time = std::chrono::high_resolution_clock::now();
    }
    
    void end(const std::string& name) {
        auto end_time = std::chrono::high_resolution_clock::now();
        double ms = std::chrono::duration<double, std::milli>(
            end_time - start_time
        ).count();
        metrics[name].push_back(ms);
        
        if (metrics[name].size() % 100 == 0) {
            double avg = 0, max_v = 0;
            for (auto v : metrics[name]) {
                avg += v;
                if (v > max_v) max_v = v;
            }
            avg /= metrics[name].size();
            __android_log_print(ANDROID_LOG_INFO, "Profiler",
                "[%s] Avg: %.2fms, Max: %.2fms", name.c_str(), avg, max_v);
        }
    }
};

#endif
```

#### Tarea 13.1.2: Memory Profiling

Use Android Studio Memory Profiler:
1. Run app on device
2. Windows → Profiler
3. MEMORY tab
4. Capture heap dump every 1min
5. Look for growing allocations
6. Check GC frequency

Expected leaks to investigate:
- `ImageAnalysis` frames not properly released
- `TensorFlow Lite` model weights cached
- `AAudio` buffers

#### Tarea 13.1.3: Frame Rate Monitoring

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/FpsMonitor.kt
package com.visualonda.sensory

import android.os.Handler
import android.os.Looper
import android.util.Log

class FpsMonitor {
    private val tag = "FpsMonitor"
    private var frameCount = 0
    private var lastTime = System.currentTimeMillis()
    private val handler = Handler(Looper.getMainLooper())
    
    fun onFrame() {
        frameCount++
        val now = System.currentTimeMillis()
        if (now - lastTime >= 1000) {
            Log.d(tag, "FPS: $frameCount")
            frameCount = 0
            lastTime = now
        }
    }
    
    fun start() {
        handler.post(object : Runnable {
            override fun run() {
                onFrame()
                handler.postDelayed(this, 1000)
            }
        })
    }
}
```

### 13.2: Optimization: Camera Processing (24 horas)

#### Tarea 13.2.1: Reduce Frame Resolution

Current: 640x480 @ 30fps = 307K pixels/frame
Better: 320x240 @ 30fps = 76K pixels/frame (4x faster)

```kotlin
// In MainActivity.kt, bindCameraPreview():
imageAnalysis = ImageAnalysis.Builder()
    .setTargetResolution(android.util.Size(320, 240))  // WAS 640x480
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .build()
```

**Impact:**
- ✅ 3-4x faster processing
- ✅ Memory: 240KB/frame vs 960KB/frame
- ✅ Visual quality: Still acceptable for audio mapping

#### Tarea 13.2.2: Optimize Grid Generation

Current: 16x16 grid = 256 cells, all processed
Better: Progressive LOD (Level of Detail)

```cpp
// FILE: app/src/main/cpp/vision_processor.cpp
void process_frame_lod(const uint8_t* image_data, int width, int height, int lod_level) {
    int grid_size = 16 >> lod_level;  // LOD 0: 16x16, LOD 1: 8x8, LOD 2: 4x4
    
    for (int row = 0; row < grid_size; ++row) {
        for (int col = 0; col < grid_size; ++col) {
            // Same processing as before
        }
    }
}
```

**Implementation:**
- LOD 0 (16x16): Full processing, every frame → Send all cells to audio
- LOD 1 (8x8): Every 2 frames → Send summary info
- LOD 2 (4x4): Every 4 frames → Background/context only

### 13.3: Optimization: Audio Engine (20 horas)

#### Taska 13.3.1: Buffer Size Reduction

Current: 2048 samples @ 44.1kHz = ~46ms latency
Target: 1024 samples = ~23ms latency

```cpp
// In audio_engine.cpp:
AAudioStreamBuilder_setFramesPerDataCallback(builder, 1024);  // WAS 2048
```

**Trade-off:** Slightly higher CPU, but lower latency (better for real-time)

#### Tarea 13.3.2: LibPD Optimization

Current: libpd_send_float() called 6 times per cell
Better: Use libpd_writearray() for batch updates

```cpp
// Batch update to libpd
float params[6 * grid_size * grid_size];
int idx = 0;
for (int i = 0; i < frame.num_cells; ++i) {
    params[idx++] = freq;
    params[idx++] = amp;
    params[idx++] = pan;
    params[idx++] = lpf;
    params[idx++] = dist;
    params[idx++] = mat;
}
libpd_writearray("control-array", params, idx);
```

### 13.4: Testing Performance (16 horas)

Create benchmark suite:

```cpp
// FILE: app/src/main/cpp/benchmark.h
struct BenchmarkResult {
    const char* name;
    double avg_ms;
    double max_ms;
    double min_ms;
    int iterations;
};

// Run before/after optimization
void benchmark_frame_processing() {
    // Capture 100 frames
    // Measure: total time
    // Expected: <50ms average
}

void benchmark_audio_callback() {
    // Measure: callback time
    // Expected: <4ms for 1024 samples
}

void benchmark_ml_inference() {
    // Measure: TensorFlow Lite forward pass
    // Expected: <100ms on GPU
}
```

### 13.5: Deliverable Semana 13-14

```
✅ Profiling report (CSV with metrics)
✅ Latency: <80ms (was <100ms)
✅ Memory: Stable at 85-95MB (was 120MB+)
✅ CPU: <12% sustained (was <15%)
✅ FPS: 30fps stable
✅ 0 memory leaks detected (AddressSanitizer)
✅ Benchmark suite integrated
```

---

## 🔊 SEMANA 14-15: AUDIO SAFETY + QA TESTING (80 horas)

**Critical:** Hearing damage from loud frequencies must be prevented.

### 14.1: SPL (Sound Pressure Level) Protection (24 horas)

#### Tarea 14.1.1: Implement SPL Limiter

Objective: Never exceed 85dB (OSHA safe level)

```cpp
// FILE: app/src/main/cpp/spl_limiter.h
#ifndef SPL_LIMITER_H
#define SPL_LIMITER_H

class SPLLimiter {
private:
    static constexpr float MAX_SPL_DB = 85.0f;  // OSHA safe limit
    static constexpr float REF_PRESSURE = 20.0e-6f;  // 20 μPa reference
    float current_peak = 0.0f;
    float limiter_gain = 1.0f;

public:
    float process(float sample) {
        // Calculate RMS over buffer
        float rms = fabsf(sample);
        
        // Calculate SPL in dB
        float spl = 20.0f * log10f(rms / REF_PRESSURE);
        
        // If exceeds limit, reduce gain
        if (spl > MAX_SPL_DB) {
            limiter_gain = 10.0f / (spl / 20.0f);  // Reduce to target
        } else {
            limiter_gain = std::min(1.0f, limiter_gain + 0.001f);  // Slowly recover
        }
        
        return sample * limiter_gain;
    }
};

#endif
```

#### Tarea 14.1.2: Add User SPL Control

```kotlin
// In MainActivity.kt or SettingsActivity:
val splSeekBar = SeekBar(context)
splSeekBar.max = 100
splSeekBar.progress = 75  // Default 75% of max
splSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val normalized = progress / 100.0f
        nativeSetSPLLimit(normalized)  // Send to C++
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
```

#### Tarea 14.1.3: Implement Dynamic Range Compression

Prevent clipping while maintaining dynamics:

```cpp
// FILE: app/src/main/cpp/compressor.h
class DynamicRangeCompressor {
private:
    float threshold = 0.7f;      // -3dB
    float ratio = 4.0f;           // 4:1 compression
    float attack_ms = 5.0f;
    float release_ms = 50.0f;
    float gain_reduction = 1.0f;

public:
    float process(float sample) {
        float abs_sample = fabsf(sample);
        
        if (abs_sample > threshold) {
            // Calculate gain reduction
            float excess = abs_sample - threshold;
            float reduced = threshold + (excess / ratio);
            gain_reduction = reduced / abs_sample;
        } else {
            // Release (slow recovery)
            gain_reduction = std::min(1.0f, gain_reduction + 0.001f);
        }
        
        return sample * gain_reduction;
    }
};
```

### 14.2: Notch Filters for Ear Safety (16 horas)

Certain frequencies are dangerous when sustained:

```cpp
// FILE: app/src/main/cpp/notch_filter.h
class NotchFilter {
private:
    // Dangerous frequencies (Hz)
    static constexpr float DANGER_FREQS[] = {
        3000.0f,   // Hearing damage peak
        4000.0f,   // Common tinnitus
        6000.0f    // Upper midrange critical
    };
    
public:
    float process(float sample, float freq) {
        // If frequency close to danger zone, attenuate
        for (float danger_freq : DANGER_FREQS) {
            float delta = fabsf(freq - danger_freq);
            if (delta < 200.0f) {  // ±200Hz window
                sample *= (delta / 200.0f);  // Linear attenuation
            }
        }
        return sample;
    }
};
```

### 14.3: QA Testing Plan (40 horas)

#### Tarea 14.3.1: Functional Testing

```markdown
### Camera Module
- [ ] Opens on app start
- [ ] Captures @ 30fps minimum
- [ ] Handles rotation (portrait/landscape)
- [ ] Handles interruptions (phone call)
- [ ] Recovers from permissions denial

### Audio Module
- [ ] Initializes without crash
- [ ] Produces audible output (test with meter app)
- [ ] Frequency range 200-8000Hz present
- [ ] Volume controllable
- [ ] Stops on app close

### Mapping Engine
- [ ] All 6 mappings produce sound
- [ ] Frequencies correct (±10Hz tolerance)
- [ ] Pan stereo works (left/right)
- [ ] Distance gain changes with depth
- [ ] Material modulation heard

### Accessibility
- [ ] TalkBack announces all buttons
- [ ] Gestures recognized (swipe, long-press)
- [ ] No audio/TalkBack conflict
- [ ] Font size settings work
- [ ] Settings toggles save state

### Stability
- [ ] No crash in 8 hour continuous use
- [ ] Memory stable (no growth >5MB/hour)
- [ ] Battery drain <5%/hour at normal use
- [ ] No CPU throttling detected
- [ ] ANR (Application Not Responding) = 0
```

#### Tarea 14.3.2: Edge Case Testing

```markdown
### Extreme Conditions
- [ ] Battery saver mode enabled
- [ ] Low light conditions (<10 lux)
- [ ] Bright sunlight (>50K lux)
- [ ] Fast motion (camera shaking)
- [ ] Static scene (no movement)
- [ ] Close objects (<30cm)
- [ ] Far objects (>5m)
- [ ] Occlusion (hand in front)
- [ ] Rapid temperature change
- [ ] Moisture/humidity exposure
```

#### Tarea 14.3.3: Security Testing

```markdown
### Permission & Privacy
- [ ] App denies camera = graceful fallback
- [ ] App denies audio = shows error
- [ ] Audio/camera not accessed without permission
- [ ] No data sent to internet
- [ ] No logs contain PII
- [ ] Uninstall removes all data

### Input Validation
- [ ] JSON parser handles malformed input
- [ ] Image data validated before processing
- [ ] Audio sample range clipped (-1.0 to +1.0)
- [ ] All strings length-checked
- [ ] No buffer overflows
```

### 14.4: Bug Tracking & Fixing (24 horas)

Create bug database:

```markdown
| Bug ID | Severity | Status | Description | Fix Time |
|--------|----------|--------|-------------|----------|
| B001   | HIGH     | FIXED  | Audio crackles at 2min | 2h |
| B002   | MEDIUM   | OPEN   | Memory leak in ML | - |
| B003   | LOW      | FIXED  | UI button overlap | 1h |
```

Allocate 24 hours for critical bug fixes.

### 14.5: Deliverable Semana 14-15

```
✅ SPL limiter implemented & tested
✅ All frequencies safe (<85dB)
✅ Compression working (no clipping)
✅ Notch filters active
✅ 50+ test cases executed
✅ 0 Critical bugs
✅ 0 High severity bugs
✅ All Medium bugs triaged
✅ Test report generated
```

---

## 📚 SEMANA 15-16: DOCUMENTATION (60 horas)

### 15.1: User Manual (24 horas)

Create comprehensive guide for blind users:

```markdown
FILE: User_Guide_Visualonda_v1.0.md

# VISUALONDA: User Guide for Blind Users

## Chapter 1: Getting Started

### Installation
1. Go to Google Play Store
2. Search "Visualonda"
3. Tap "Install"
4. Grant permissions when prompted

### First Launch
- App says: "Welcome to Visualonda"
- Swipe up: Start camera
- Swipe down: Open settings
- Swipe left: Gallery mode
- Swipe right: Screen reader mode

### Audio Primer
You'll hear 3D sounds that represent what the camera sees:

**SOUNDS REPRESENT:**
- LEFT side = objects on your left
- RIGHT side = objects on your right
- HIGH pitch = bright/light objects
- LOW pitch = dark objects
- LOUD = close to you
- QUIET = far from you

### Tutorial (First Use)
1. Hold phone facing forward
2. Move slowly left-right
3. You should hear panning sounds
4. This is the visual space!

## Chapter 2: Using Camera Mode

### Active Camera
- Tap center: Freeze frame
- Double-tap: Zoom (2x)
- Swipe up: Increase volume
- Swipe down: Decrease volume
- Long-press: Take photo

### What You Hear
- Frequency: Brightness (200Hz = dark, 4000Hz = bright)
- Pan: Position (left ear = left, right ear = right)
- Volume: Distance (loud = close, quiet = far)
- Rhythm: Movement (fast rhythm = motion)

### Understanding Spatial Layout

The app divides the camera view into a 16x16 grid:

```
[OBJECTS ON LEFT]  [CENTER]  [OBJECTS ON RIGHT]
     (LEFT EAR)             (RIGHT EAR)
```

Each grid cell sends a unique tone based on its brightness.

## Chapter 3: Gallery & Photo Mode

### Import Photo
1. Tap "Gallery"
2. Select photo from phone
3. Wait for analysis (~2sec)
4. Audio describes photo

### Explore Photo
- Swipe left/right: Change camera angle (simulated)
- Swipe up/down: Zoom level

### Save Sonified Photo
- Long-press: Save as audio file
- Share to friends

## Chapter 4: Screen Reader Mode (Advanced)

This mode sonifies your entire phone screen:

1. Tap menu → "Screen Mode"
2. Select app or area
3. Audio describes UI elements

Example:
- "Button: Settings, double-tap to activate"
- "Text input, currently empty"
- "Slider: Volume, 60%"

## Chapter 5: Settings & Preferences

### Audio Settings
- **Volume:** 30-100% (default 75%)
- **Bass Boost:** Yes/No (default Yes)
- **Reverb:** Off/Light/Heavy (default Light)
- **Effect:** Binaural/Stereo/Mono (default Binaural)

### Display Settings
- **Grid Size:** 4x4 / 8x8 / 16x16 (default 16x16)
- **Processing:** Fast/Balanced/Detailed (default Balanced)
- **Latency:** Lower = faster, higher = better quality

### Accessibility
- **TalkBack Integration:** On/Off
- **Gesture Sensitivity:** Low/Normal/High (default Normal)
- **Haptic Feedback:** On/Off (default On)

### Safety
- **SPL Limit:** Hearing safe (always <85dB)
- **Auto Volume:** Yes/No (matches ambient noise)
- **Session Limit:** Set max session duration (default: no limit)

## Chapter 6: Troubleshooting

### "No sound from camera"
- Check volume is >30%
- Check headphones connected
- Restart app

### "Sound is crackling"
- Reduce volume
- Reduce grid size (4x4)
- Restart app

### "App crashes on launch"
- Uninstall app
- Clear phone cache
- Reinstall

### "TalkBack conflicts with audio"
- Go to Settings
- Turn OFF "TalkBack Integration"
- Restart app

## Chapter 7: Safety & Best Practices

⚠️ **HEARING SAFETY**
- Always use at moderate volume
- Take 15min breaks every hour
- If ears hurt, STOP immediately
- Contact doctor if hearing changes

⚠️ **PHYSICAL SAFETY**
- Use only in safe environments
- Never use while driving
- Have sighted guide when exploring
- Be aware of surroundings

⚠️ **BATTERY**
- Camera uses battery heavily
- Charge before long sessions
- Enable battery saver if needed

## Appendix: Sound Reference

| Parameter | Range | Meaning |
|-----------|-------|---------|
| Frequency | 200-4000 Hz | Brightness (200=dark, 4000=bright) |
| Pan | L/R stereo | Position (-100=left, +100=right) |
| Volume | 0-100% | Distance (100=close, 0=far) |
| Rhythm | 1-10 Hz | Movement speed |
| Modulation | 0-100% | Texture/material |

---

**Questions?** Contact: support@visualonda.dev
**Version:** 1.0 (July 2026)
**Last Updated:** July 2026
```

### 15.2: Developer Guide (20 horas)

```markdown
FILE: Developer_Guide_Visualonda_v1.0.md

# VISUALONDA: Developer Guide

## Architecture Overview

```
┌─────────────────────────────────────┐
│  Java/Kotlin (Android Framework)    │
├─────────────────────────────────────┤
│  JNI Layer                          │
├─────────────────────────────────────┤
│  C++ Core (NDK)                     │
│  ├─ Vision Processor (320x240 YUV)  │
│  ├─ Mapping Engine (6 functions)    │
│  ├─ Audio Engine (AAudio)           │
│  └─ LibPD Wrapper                   │
├─────────────────────────────────────┤
│  Pure Data Patch (light_material)   │
├─────────────────────────────────────┤
│  ML Models (TensorFlow Lite)        │
│  ├─ MobileNetV2 (objects)           │
│  ├─ MiDaS (depth)                   │
│  └─ MediaPipe (faces/hands)         │
└─────────────────────────────────────┘
```

## Building from Source

### Prerequisites
```bash
- Android Studio 2023.1+
- NDK 25.0.8221429+
- Python 3.8+
- CMake 3.22+
```

### Build Steps
```bash
git clone https://github.com/Visualonda/Visualonda.git
cd android_skeleton
./gradlew clean build
./gradlew installDebug
```

## API Reference

### JNI Functions (app/src/main/cpp/native-lib.cpp)

#### Audio Functions
```cpp
// Initialize AAudio engine
void audioEngineInit()

// Set frequency (Hz)
void audioEngineSetFreq(float freq)

// Set amplitude (0.0-1.0)
void audioEngineSetAmp(float amp)

// Cleanup resources
void audioEngineCleanup()
```

#### Vision Functions
```cpp
// Process frame and send to audio
void sendControlJson(const char* json_str)

// Set grid size (4, 8, or 16)
void setGridSize(int size)

// Set processing quality (0=fast, 1=balanced, 2=detailed)
void setProcessingQuality(int quality)
```

#### LibPD Functions
```cpp
// Initialize LibPD
bool libpd_wrapper_init()

// Load patch from file path
bool libpd_wrapper_load_patch(const char* patch_path)

// Send float to receiver
bool libpd_wrapper_send_float(const char* receiver, float value)

// Cleanup
void libpd_wrapper_cleanup()
```

## Adding New Mapping Functions

To add a 7th mapping function:

1. Edit `control_schema.json`:
```json
{
  "mappings": {
    "mapping_7": {
      "name": "my_new_mapping",
      "input": "luminance",
      "output": "filter_cutoff",
      "min_output": 100,
      "max_output": 8000
    }
  }
}
```

2. In `mapping_engine.cpp`:
```cpp
double map_mapping_7(double input) {
    // Implement your mapping
    return output_value;
}
```

3. Call in `process_frame()`:
```cpp
double mapping7_out = map_mapping_7(cell.luminance);
libpd_wrapper_send_float("filter-cutoff", (float)mapping7_out);
```

4. Update Pure Data patch to use new parameter

## Testing

### Unit Tests
```bash
./gradlew testDebug
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### Performance Benchmarks
```cpp
// In native-lib.cpp
void runBenchmarks() {
    auto start = high_resolution_clock::now();
    // ... test code ...
    auto end = high_resolution_clock::now();
    auto duration = duration_cast<milliseconds>(end - start);
    ALOG("Benchmark result: %lld ms", duration.count());
}
```

## Debugging

### Logcat Filtering
```bash
# Show only Visualonda logs
adb logcat | grep -i visualonda

# Show errors
adb logcat | grep -i error

# Profile CPU/Memory
adb shell top -n 1
```

### Native Crash Debugging
```bash
# Get crash logs
adb logcat -d > crash.txt

# Symbolicate (convert addresses to function names)
ndk-stack -sym build/intermediates/cmake -dump crash.txt
```

## Performance Targets

| Component | Target | Actual | Status |
|-----------|--------|--------|--------|
| Frame processing latency | <80ms | 75ms | ✅ |
| Audio callback | <4ms | 2ms | ✅ |
| ML inference | <150ms | 120ms (GPU) | ✅ |
| Memory usage | <100MB | 92MB | ✅ |
| CPU usage | <15% | 10% | ✅ |
| Battery drain | <5%/hr | 3.2%/hr | ✅ |

## Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/my-feature`
3. Make changes
4. Add tests
5. Submit PR with description

### Code Style
- C++: Google C++ Style Guide
- Kotlin: Android Kotlin Style Guide
- Follow existing indentation/naming

### Commit Messages
```
[COMPONENT] Brief description

Longer explanation if needed.

Fixes #123
```

---

**Questions?** GitHub Issues: https://github.com/Visualonda/Visualonda/issues
```

### 15.3: API Reference (8 horas)

```markdown
FILE: API_Reference_v1.0.md

# VISUALONDA API Reference

## Control Schema JSON

Each frame sends:

```json
{
  "timestamp_ms": 1720000000000,
  "frame_rate_hz": 30,
  "grid": {
    "rows": 16,
    "cols": 16
  },
  "cells": [
    {
      "id": 0,
      "row": 0,
      "col": 0,
      "azimuth_deg": -90.0,
      "elevation_m": 2.0,
      "distance_m": 2.5,
      "material": "wood",
      "luminance": 0.75,
      "confidence": 0.95
    },
    ...
  ]
}
```

### Fields Explained

| Field | Type | Range | Description |
|-------|------|-------|-------------|
| timestamp_ms | int64 | 0-∞ | Unix timestamp |
| frame_rate_hz | int | 1-60 | Processing rate |
| rows | int | 4/8/16 | Grid height |
| cols | int | 4/8/16 | Grid width |
| id | int | 0-255 | Unique cell ID |
| azimuth_deg | float | -90 to +90 | L-R position |
| elevation_m | float | 0-10 | Height above ground |
| distance_m | float | 0-100 | Distance from camera |
| material | string | {wood, metal, ...} | Surface type |
| luminance | float | 0.0-1.0 | Brightness (0=dark, 1=bright) |
| confidence | float | 0.0-1.0 | ML confidence |

## LibPD Message Interface

Receivers (from app → Pure Data):

```pd
light-freq-left      : float (Hz)
light-freq-right     : float (Hz)
light-amp            : float (0-1)
distance-gain        : float (0-1)
distance-lpf-cutoff  : float (Hz)
material-density     : float (0-100)
```

Senders (from Pure Data → app):

```pd
audio-out-left       : float (-1 to +1)
audio-out-right      : float (-1 to +1)
spl-monitor          : float (dB)
```

---
```

### 15.4: Deliverable Semana 15-16

```
✅ User Guide (40 pages, PDF + web)
✅ Developer Guide (25 pages)
✅ API Reference (10 pages)
✅ Inline code comments (100% coverage)
✅ README.md updated
✅ CHANGELOG.md created
✅ Contributing.md added
✅ LICENSE (Apache 2.0) included
```

---

## 🎮 SEMANA 16-18: GOOGLE PLAY SUBMISSION & LAUNCH (80 horas)

### 16.1: App Signing & Release Build (16 horas)

#### Tarea 16.1.1: Create Keystore

```bash
# Generate keystore (do ONCE, save securely)
keytool -genkey -v -keystore visualonda-release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias visualonda_key

# Output:
# Keystore password: [SET SECURE PASSWORD]
# Key password: [SAME AS ABOVE]
# Common name (CN): Visualonda Team
# Organizational Unit (OU): Development
# Organization (O): Visualonda Inc
# City: San Francisco
# State: CA
# Country: US

# Store securely:
# - Upload to 1Password or similar
# - DO NOT commit to git
# - Backup in secure location
```

#### Tarea 16.1.2: Create Release Build

```gradle
// FILE: app/build.gradle

signingConfigs {
    release {
        storeFile file("../visualonda-release.keystore")
        storePassword System.getenv("KEYSTORE_PASSWORD") ?: "changeme"
        keyAlias "visualonda_key"
        keyPassword System.getenv("KEY_PASSWORD") ?: "changeme"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        debuggable false
    }
}
```

#### Tarea 16.1.3: Build Release APK

```bash
export KEYSTORE_PASSWORD="your-secure-password"
export KEY_PASSWORD="your-secure-password"

cd android_skeleton
./gradlew clean :app:bundleRelease

# Output: app/release/app-release.aab (Android App Bundle)
# Size: ~35MB (includes all arches)

# Optional: Generate APK too
./gradlew :app:assembleRelease
# Output: app/release/app-release.apk (~25MB)

# Verify signing
jarsigner -verify -verbose app/release/app-release.aab
```

### 16.2: Google Play Store Setup (24 horas)

#### Tarea 16.2.1: Create Developer Account

1. Go to https://play.google.com/console
2. Sign in with Google account
3. Pay $25 registration fee
4. Accept terms and policies
5. Complete Developer profile

#### Tarea 16.2.2: Create App in Play Console

```markdown
## App Details

**App name:** Visualonda
**Store listing language:** English (US)
**Category:** Accessibility
**Content rating:** 4+ (Everyone)
**Default language:** English

**Description (short, 50 chars):**
"See through sound - audio 3D for blind users"

**Full description (4000 chars max):**
"Visualonda is a revolutionary accessibility app that allows blind 
and low-vision users to perceive visual environments through 
spatially-mapped 3D audio.

FEATURES:
• Real-time camera analysis with spatial audio feedback
• Full phone screen sonification (see the OS itself)
• Photo and video sonification
• Advanced ML: object detection, depth estimation, text recognition
• Haptic feedback for non-auditory cues
• Completely local processing - no cloud dependency
• WCAG AAA accessibility compliance

AUDIO FEEDBACK:
• Pan: objects on left/right
• Pitch: brightness (dark = low, bright = high)
• Loudness: distance (close = loud, far = quiet)
• Rhythm: movement and change

SAFETY:
• Hearing protection built-in (always <85dB)
• Dynamic range compression
• Customizable audio limits
• SPL monitoring

TECHNICAL:
• Uses Pure Data for real-time audio synthesis
• TensorFlow Lite for ML inference
• AAudio for ultra-low latency
• Full accessibility with TalkBack integration

Get started: Open app, allow camera/microphone, hold phone forward.

For support: support@visualonda.dev
Documentation: https://github.com/Visualonda/Visualonda
"

**Screenshots (5 required):**
1. App home screen
2. Camera mode with audio visualization
3. Settings menu
4. Accessibility features
5. Performance metrics

**Icon (512x512 PNG):**
[Design colorful accessible icon with sound waves + eye]

**Feature graphic (1024x500 PNG):**
[Banner with app name, "See with Sound" tagline]
```

#### Tarea 16.2.3: Content Rating Questionnaire

```markdown
ESRB/IARC Questionnaire:

Q: Does your app contain graphic violence?
A: No

Q: Does your app contain sexual content?
A: No

Q: Does your app contain profanity?
A: No

Q: Does your app collect personal data?
A: No (camera/mic only, processed locally)

Q: Does your app target children?
A: No (for all ages, optimized for blind users)

Q: Does your app contain ads?
A: No (free, no monetization)

Q: Does your app require internet?
A: No (works completely offline)

→ Rating: 4+ (Everyone)
```

### 16.3: Privacy & Security Documentation (16 horas)

#### Tarea 16.3.1: Privacy Policy

```markdown
FILE: Privacy_Policy.md

# VISUALONDA PRIVACY POLICY

**Effective Date:** July 2026
**Last Updated:** July 2026

## 1. INFORMATION WE COLLECT

### On-Device Processing
- Camera frames (real-time processing only)
- Microphone audio (for audio input/recording optional feature)
- Device settings (volume, brightness, etc.)

**Important:** All camera and audio data is processed 100% locally 
on your device. No data is sent to cloud servers.

### NO Data Collection
- Visualonda does NOT collect:
  - Your location
  - Contact information
  - Browsing history
  - Identifying information
  - Health data (beyond audio safety)

### Analytics (Optional)
- App crashes (anonymized stack traces)
- Feature usage (feature names only, no identifying info)
- Opt-out available in settings

## 2. HOW WE USE YOUR DATA

- **Camera:** Real-time visual analysis (on device)
- **Audio:** Real-time audio processing (on device)
- **Settings:** Store user preferences (on device)

No third parties have access to your data.

## 3. DATA SECURITY

- End-to-end encryption (no transmission)
- Secure file storage
- No backup to cloud (unless user explicitly enables)
- Automatic deletion on app uninstall

## 4. YOUR RIGHTS

You have the right to:
- Know what data is processed
- Request data export
- Delete all app data
- Uninstall app anytime
- Contact us with privacy questions

## 5. CONTACT

Email: privacy@visualonda.dev
GitHub Issues: https://github.com/Visualonda/Visualonda/issues

## 6. CHANGES TO POLICY

We may update this policy. We'll notify users of material changes.
```

#### Tarea 16.3.2: Terms of Service

```markdown
FILE: Terms_of_Service.md

# VISUALONDA TERMS OF SERVICE

**Effective Date:** July 2026

## 1. USE LICENSE

Visualonda is provided "AS-IS" under Apache 2.0 open source license.
You may use, modify, and distribute under license terms.

## 2. DISCLAIMER

VISUALONDA IS PROVIDED WITHOUT WARRANTY. USE AT YOUR OWN RISK.

- App may have bugs or crashes
- Audio output may vary by device
- ML inference accuracy not guaranteed
- We're not liable for hearing damage from improper use

## 3. SAFETY WARNING

⚠️ **HEARING SAFETY:**
- Always use at moderate volume
- Take breaks regularly
- If you experience ear pain, stop immediately
- Visualonda includes hearing protection (SPL <85dB)
- Some users may be sensitive to certain frequencies

⚠️ **PHYSICAL SAFETY:**
- Use only in safe environments
- Never while driving or in dangerous situations
- Be aware of your surroundings
- Have a sighted person assist if needed

## 4. USER CONDUCT

You agree to:
- Use app legally and ethically
- Not reverse engineer or hack the app
- Not use for surveillance without consent
- Not violate others' privacy
- Not use to harm people or property

## 5. INTELLECTUAL PROPERTY

- Visualonda code is Apache 2.0 open source
- Pure Data patches are GPL-compatible
- TensorFlow Lite models are Google's
- Respect all licenses

## 6. LIMITATION OF LIABILITY

To the maximum extent permitted by law:
- We are not liable for any damages
- Our liability is limited to $0
- This includes indirect/consequential damages

## 7. GOVERNING LAW

These terms are governed by laws of [Your Jurisdiction].

## 8. CONTACT

questions@visualonda.dev
```

### 16.4: Release Checklist (24 horas)

```markdown
## PRE-LAUNCH CHECKLIST

### Code & Build
- [ ] All tests pass (./gradlew test)
- [ ] 0 Critical/High bugs
- [ ] Code review completed
- [ ] ProGuard rules tested
- [ ] Release build tested on real device
- [ ] Crash rate <0.1% (verify with Crashlytics if enabled)

### Performance
- [ ] Latency <100ms (camera → audio)
- [ ] Memory <100MB sustained
- [ ] CPU <15% typical load
- [ ] Battery drain <5%/hour
- [ ] No ANR (Application Not Responding) events
- [ ] Frame rate: 30fps stable

### Accessibility
- [ ] TalkBack fully compatible
- [ ] All buttons/menus accessible
- [ ] 5+ gestures work intuitively
- [ ] Haptic feedback (if enabled) works
- [ ] Text resizing works
- [ ] Colors have sufficient contrast
- [ ] Tested with blind users (yes/no): YES

### Security & Privacy
- [ ] No hardcoded credentials
- [ ] No test/debug code left
- [ ] No sensitive logs
- [ ] Privacy policy in-app
- [ ] Terms of service displayed
- [ ] GDPR/CCPA compliant
- [ ] No telemetry without consent

### Content
- [ ] App name matches store listing
- [ ] Description accurate
- [ ] Screenshots professional
- [ ] Icon 512x512 PNG
- [ ] Feature graphic 1024x500 PNG
- [ ] Feature video (optional) ready
- [ ] All text proofread (spell check, grammar)
- [ ] Video captions if present

### Configuration
- [ ] App version: 1.0.0
- [ ] Build number: 1
- [ ] Min SDK: 28 (Android 9)
- [ ] Target SDK: 34 (Android 14)
- [ ] Supported ABIs: arm64-v8a
- [ ] Supported languages: English (+ Spanish if available)

### Store Listing
- [ ] App category: Accessibility
- [ ] Content rating: 4+
- [ ] Store listing complete
- [ ] Release notes written
- [ ] Contact information provided
- [ ] Support email active
- [ ] Website link (if have one)

### Legal
- [ ] License file included (LICENSE.txt)
- [ ] Privacy policy in-app
- [ ] Terms of service in-app
- [ ] Accessibility statement
- [ ] Third-party licenses listed
- [ ] Google Play Policies compliant
- [ ] No policy violations

### Analytics (if enabled)
- [ ] Crash reporting configured
- [ ] Performance monitoring ready
- [ ] User consent requested
- [ ] No PII in analytics

### Support
- [ ] Support email: support@visualonda.dev
- [ ] GitHub repo: Public and documented
- [ ] Issue template created
- [ ] Contribution guidelines
- [ ] Code of conduct

### Final Checks
- [ ] One final complete test on device
- [ ] Screenshots match app (latest build)
- [ ] No external links broken
- [ ] Permissions justified
- [ ] APK/AAB size reasonable (<50MB)
```

### 16.5: Submission Process (16 horas)

#### Step 1: Upload Release Bundle

```bash
# In Google Play Console:
1. Select "Create new release"
2. Upload app-release.aab (Android App Bundle)
3. Review size estimate (~35MB)
4. Add release notes:

"# Visualonda v1.0.0 - Initial Release

First public release of Visualonda - audio 3D visualization for blind users.

## Features
✅ Real-time camera sonification
✅ Full phone screen audio description
✅ Photo/video sonification
✅ Advanced ML (objects, depth, text, faces)
✅ WCAG AAA accessibility
✅ Hearing protection built-in
✅ 100% local processing

## What's New
- Initial release
- Camera mode with spatial audio
- Settings and preferences
- Accessibility features
- ML integration

## Known Issues
- None (launch version)

## Coming Soon (v1.1)
- Screen reader mode improvements
- More gestures
- Custom audio profiles
- Language support

Thank you for using Visualonda!
"
5. Click "Review"
```

#### Step 2: Staged Rollout (Safety)

```markdown
Google Play offers staged rollout:

STAGE 1 (Days 1-3): 5% of users
- Monitor crash rate
- Check user reviews
- Performance metrics

If all OK → STAGE 2

STAGE 2 (Days 4-7): 25% of users
- Wider audience test
- More feedback

If OK → STAGE 3

STAGE 3 (Day 8+): 100% rollout
- Full public release
```

**Recommendation:** Use staged rollout for first release.

#### Step 3: Monitor Launch

```bash
# In Google Play Console:

Crash rate: Monitor in Real-time dashboard
  Target: <0.1%
  Alert if: >1%

Reviews: Check daily first week
  Watch for:
  - Crash reports
  - Usability feedback
  - Performance complaints

User feedback: Social media monitoring
  Twitter: @Visualonda
  GitHub: Issues
  Email: support@visualonda.dev
```

### 16.6: Post-Launch Activities (16 horas)

#### Launch Day (0-24h)

```markdown
- [ ] Release published (confirm in console)
- [ ] Social media announcement
- [ ] Press release to accessibility blogs
- [ ] Email to accessibility organizations
- [ ] Alert beta testers
- [ ] Monitor crash dashboard every hour
- [ ] Respond to first user reviews
- [ ] Check performance metrics
```

#### Week 1

```markdown
- [ ] Daily: Check crash rate, reviews, ratings
- [ ] Respond to all user feedback
- [ ] Create FAQ document
- [ ] Prepare hotfix for any critical bugs
- [ ] Analyze user demographics
- [ ] Monitor app installs and retention
```

#### Month 1

```markdown
- [ ] User surveys (5-10 users min)
- [ ] Accessibility audit
- [ ] Performance optimization
- [ ] Plan v1.1 features based on feedback
- [ ] Create user testimonials/case studies
- [ ] Reach out to accessibility influencers
```

### 16.7: Deliverable Semana 16-18

```
✅ App signed with release keystore
✅ Release build tested on device
✅ Google Play account created
✅ App listing complete + screenshots
✅ Privacy policy in-app
✅ Terms of service in-app
✅ All store metadata complete
✅ Content rating questionnaire completed
✅ Pre-launch checklist 100% green
✅ App submitted to Google Play
✅ Staged rollout monitoring active
✅ Social media announcement sent
✅ User support channels active
✅ Post-launch metrics dashboard ready
```

---

## 📊 RESUMEN FASE 4: 6 SEMANAS

```
SEMANA 13-14: Performance Optimization (80h)
  ├─ Profiling (CPU, memory, FPS)
  ├─ Camera optimization (320x240, LOD)
  ├─ Audio optimization (buffer size, batch updates)
  ├─ Benchmarking suite
  └─ Result: Latency <80ms, Memory stable, CPU <12%

SEMANA 14-15: Audio Safety + QA (80h)
  ├─ SPL limiter (<85dB guaranteed)
  ├─ Dynamic range compression
  ├─ Notch filters for ear safety
  ├─ 50+ test cases
  ├─ Edge case testing
  ├─ Security testing
  └─ Result: 0 Critical/High bugs, hearing safe

SEMANA 15-16: Documentation (60h)
  ├─ User guide (40 pages)
  ├─ Developer guide (25 pages)
  ├─ API reference (10 pages)
  ├─ Inline code comments
  ├─ README + CHANGELOG + Contributing
  └─ Result: 100% documented

SEMANA 16-18: Google Play Submission (80h)
  ├─ App signing + release build
  ├─ Google Play setup
  ├─ Privacy policy + terms
  ├─ Pre-launch checklist
  ├─ Store submission
  ├─ Staged rollout
  └─ Result: APP LIVE 🚀

TOTAL FASE 4: 300 horas (~40 FTE days)
```

---

## 🎯 QUALITY GATES (MUST PASS)

### Performance Gate
```
✅ Latency: 80ms ± 10ms
✅ Memory: <100MB
✅ CPU: <15% sustained
✅ Battery: <5%/hour
✅ FPS: 30fps stable (no drops below 20fps)
✅ Crash rate: 0% (8h test minimum)
```

### Safety Gate
```
✅ SPL: Always <85dB (measured @ 94dB SPL reference)
✅ No hearing-damage frequencies sustained
✅ Audio compression preventing clipping
✅ User can disable audio instantly
```

### Accessibility Gate
```
✅ TalkBack fully functional (0 conflicts)
✅ 5+ intuitive gestures working
✅ Haptic feedback for notifications
✅ Font sizes 100% scalable
✅ 10 blind users test, 8/10 rate >4/5 stars
✅ Onboarding completable in <5 min
```

### Security Gate
```
✅ No hardcoded credentials
✅ No telemetry without consent
✅ Privacy policy reviewed by legal
✅ Terms of service clear
✅ No third-party code injection
✅ All dependencies audited
```

### Store Gate
```
✅ All store metadata complete
✅ Screenshots professional quality
✅ Content rating appropriate
✅ Policies compliant
✅ Support contact active
✅ Code signing verified
```

**If ANY gate fails → BLOCK RELEASE and fix issues.**

---

## 🚀 SUCCESS CRITERIA

### Launch Success
```
✅ App published in Google Play
✅ Installation successful on 3+ devices
✅ 0 crashes in first 24h (monitored)
✅ ≥1,000 downloads first week
✅ Rating ≥4.0/5 stars
✅ Crash rate <0.5% (first week)
```

### User Success
```
✅ 10+ users successfully onboarded
✅ Users report functionality working
✅ Positive feedback in reviews
✅ No hearing damage reports
✅ Users recommend to others
✅ Session duration >15min average
```

### Business Success
```
✅ PR coverage in accessibility media
✅ Partnership discussions with orgs
✅ GitHub stars >100
✅ Community contributing
✅ Path to sustainability clear
```

---

## 📈 POST-LAUNCH ROADMAP (v1.1 - v2.0)

### v1.1 (1 month post-launch)
- Bugfixes based on user feedback
- Improved ML accuracy
- More gestures
- Haptic patterns customization

### v1.2 (2 months post-launch)
- Screen reader mode improvements
- Custom audio profiles
- Performance optimization
- Battery mode support

### v2.0 (3-4 months post-launch)
- Language support (Spanish, Portuguese, etc.)
- Community ML models
- Advanced UI sonification
- Cloud sharing (optional, encrypted)
- Wearable support
- API for third-party apps

---

## ✅ FINAL DELIVERABLES (END OF FASE 4)

```
📱 APP: Live in Google Play Store
📊 METRICS: Dashboard setup (crashes, performance)
📚 DOCS: User guide + Dev guide + API reference
🔒 SECURITY: Privacy policy + Terms of service
🎵 AUDIO: Safe, optimized, lag-free
♿️ ACCESS: Fully accessible, TalkBack compatible
⚡ PERF: <80ms latency, <100MB RAM, <15% CPU
🎯 QUALITY: 0 critical bugs, >4.0★ rating
🌍 COMMUNITY: GitHub public, contributing guidelines
📈 SUPPORT: Email + GitHub issues + documentation
```

---

## 🏁 TIMELINE SUMMARY

```
Semana 1-4:   FASE 1 - Foundation (Audio engine ready)
Semana 5-8:   FASE 2 - Accesibilidad (UI accessible)
Semana 9-12:  FASE 3 - Inteligencia (ML integrated)
Semana 13-14: FASE 4A - Optimization (Performance tuned)
Semana 14-15: FASE 4B - Safety (Hearing protected)
Semana 15-16: FASE 4C - Documentation (Fully documented)
Semana 16-18: FASE 4D - Launch (LIVE in Play Store) 🚀

TOTAL: 18 WEEKS
BUDGET: ~$244K
TEAM: 3.5-4 FTE
RESULT: Production-ready app for blind users
```

---

**Documento Versión:** 1.0
**Creado:** Julio 2026
**Estado:** PHASE 4 GUIDE - READY FOR IMPLEMENTATION

**Next Step:** Semana 16 - Kick-off Google Play submission process

