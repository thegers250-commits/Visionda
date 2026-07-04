# ARQUITECTURA TÉCNICA: VISUALONDA

## 🏗️ Arquitectura End-to-End

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          VISUALONDA ARCHITECTURE                             │
└─────────────────────────────────────────────────────────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════╗
║                    CAPA DE ENTRADA: CAPTURA DE DATOS                      ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  📱 CÁMARA EN VIVO (Kotlin)          🖼️ GALERÍA (Kotlin)      🎥 VIDEO  ║
║  ├─ Camera2/CameraX                  ├─ Load from storage    ├─ Extract ║
║  ├─ 640×480 @ 30fps                  ├─ Formato: JPEG/PNG   │  frames  ║
║  ├─ RGB/YUV frame stream             └─ Procesamiento igual  └─ @ 30fps║
║  └─ Real-time processing              a cámara              │           ║
║                                                              │           ║
║                       🖥️ PANTALLA (Accessibility)        │           ║
║                       ├─ AccessibilityService            │           ║
║                       ├─ Read UI hierarchy               │           ║
║                       └─ Element bounds + types          │           ║
║                                                           ↓           ║
╚═══════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE PROCESAMIENTO: JVM (Kotlin)                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  MainActivity.kt                                                             │
│  ├─ Intent handling                                                          │
│  ├─ Permission management (CAMERA + RECORD_AUDIO)                           │
│  └─ UI delegation                                                            │
│                                                                              │
│  VisionEngine.kt (Thread 1)                                                  │
│  ├─ Camera frame analysis                                                    │
│  ├─ Grid generation (16×16)                                                 │
│  ├─ Cell descriptor extraction:                                              │
│  │  ├─ Position (row, col) → azimuth, elevation                             │
│  │  ├─ Luminance (Y channel in YUV)                                          │
│  │  ├─ (Futuro) Depth estimation                                             │
│  │  ├─ (Futuro) Object detection                                             │
│  │  └─ (Futuro) Material classification                                      │
│  └─ Generate control_schema.json                                             │
│                                                                              │
│  RingBuffer (Thread-safe)                                                    │
│  ├─ Producedor: VisionEngine (30 Hz) → JSON                                  │
│  └─ Consumidor: NativeJNI (audio thread) → C++                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                      CAPA DE MAPEO: NATIVE (C++ / JNI)                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  native-lib.cpp                                                              │
│  ├─ JNI entry points:                                                        │
│  │  ├─ sendControlJson(json) ← Kotlin                                        │
│  │  ├─ pdInit() ← Kotlin                                                     │
│  │  ├─ pdOpenPatch(path) ← Kotlin                                            │
│  │  └─ pdSendFloat(name, value) ← Kotlin                                     │
│  └─ Thread-safe callback management                                          │
│                                                                              │
│  json_parser.cpp                                                             │
│  ├─ Parse control_schema.json                                                │
│  ├─ Extract per-cell:                                                        │
│  │  ├─ azimuth_deg [-90, 90]                                                 │
│  │  ├─ elevation_m [0, 2.5]                                                  │
│  │  ├─ distance_m [0.5, 5+]                                                  │
│  │  ├─ luminance [0, 1]                                                      │
│  │  ├─ material (string)                                                     │
│  │  └─ confidence [0, 1]                                                     │
│  └─ Error handling & validation                                              │
│                                                                              │
│  mapping_engine.cpp ← MAPEOS MATEMÁTICOS                                     │
│  ├─ MAPEO 1: Elevación → Frecuencia (logarítmico)                            │
│  │  └─ f(h) = 60 * exp(1.7685 * h)                                           │
│  ├─ MAPEO 2: Distancia → Ganancia + LPF                                      │
│  │  ├─ G(r) = 1 / (1 + (r/1.0)²)                                             │
│  │  └─ fc(r) = 12000 * exp(-0.18*r)                                          │
│  ├─ MAPEO 3: Azimut → Paneo Binaural (ITD/ILD)                               │
│  │  ├─ Pan L/R (equal-power)                                                 │
│  │  ├─ ITD delay (0.8ms max)                                                 │
│  │  └─ ILD atenuación (20dB max)                                             │
│  ├─ MAPEO 4: Luminancia → Binaural beats                                     │
│  │  ├─ Δ = 5 + 7*L (Hz) → [5-12 Hz]                                          │
│  │  ├─ Left freq = 4000 + Δ/2                                                │
│  │  └─ Right freq = 4000 - Δ/2                                               │
│  ├─ MAPEO 5: Material → Síntesis (FUTURO FASE 3)                             │
│  │  ├─ Metal → FM synthesis (carrier 3-6kHz)                                 │
│  │  ├─ Wood → Additive (150-800Hz)                                           │
│  │  └─ Stone → Granular (10-40ms grains)                                     │
│  └─ MAPEO 6: Confidence → Mixtura (fade)                                     │
│     └─ A *= confidence                                                       │
│                                                                              │
│  libpd_wrapper.cpp                                                           │
│  ├─ libpd_init()                                                              │
│  ├─ libpd_load_patch(path)                                                    │
│  └─ libpd_send_float(name, value) × N parámetros/frame                       │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│               CAPA DE SÍNTESIS: AUDIO DSP (LibPD + Pure Data)               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  light_material_patch.pd (Pure Data)                                         │
│                                                                              │
│  Receivers (entrada de parámetros):                                          │
│  ├─ light-freq-left (Hz) ← mapping_engine                                    │
│  ├─ light-freq-right (Hz) ← mapping_engine                                   │
│  ├─ light-amp (0..1) ← mapping_engine                                        │
│  ├─ material-density (0..1) ← mapping_engine                                 │
│  ├─ distance-gain (0..1) ← mapping_engine                                    │
│  └─ distance-lpf-cutoff (Hz) ← mapping_engine                                │
│                                                                              │
│  Signal Processing Graph:                                                    │
│  ├─ OSC 1: light-freq-left → [osc~] → *~ 0.6 ┐                              │
│  │                                             ├─→ +~ → LPF → DAC (L)       │
│  ├─ OSC 2: light-freq-right → [osc~] → *~ 0.6 ┘                             │
│  ├─ Noise: [noise~] → [lop~ distance-lpf-cutoff] → *~ distance-gain ┐       │
│  │                                                                    ├─→ +~ │
│  │                                                                    │   DAC│
│  └─ Material envelope (FUTURO)                                        └─(R) │
│                                                                              │
│  Output:                                                                     │
│  ├─ Estéreo 44.1 kHz                                                         │
│  ├─ 2-channel DAC stream                                                     │
│  └─ → AAudio callback                                                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│               CAPA DE AUDIO: ANDROID AUDIO STACK (AAudio)                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  audio_engine.cpp                                                            │
│  ├─ AAudioStreamBuilder                                                      │
│  │  ├─ Direction: OUTPUT                                                     │
│  │  ├─ Sample rate: 44100 Hz                                                 │
│  │  ├─ Channels: 2 (stereo)                                                  │
│  │  ├─ Format: FLOAT                                                         │
│  │  └─ Buffer size: 64-256 samples (1.5-6ms latency)                         │
│  └─ audio_callback() (Audio Thread - critical latency)                       │
│     ├─ Called @ ~44100/64 = ~688 times/sec (~1.5ms per call)                 │
│     ├─ Query LibPD for next audio block                                      │
│     ├─ Copy to output buffer                                                 │
│     ├─ SPL protection (clipping prevention)                                  │
│     └─ Return AAUDIO_CALLBACK_RESULT_CONTINUE                                │
│                                                                              │
│  OpenSLES (fallback para Android <8.0)                                       │
│  ├─ SLObjectItf engine, outputMix                                            │
│  ├─ SLPlayItf player                                                         │
│  └─ PCM buffers enqueue/dequeue                                              │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                   CAPA DE SALIDA: DISPOSITIVO DE AUDIO                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  📱 Speaker / 🎧 Headphones (Binaural)                                      │
│  ├─ Estéreo 44.1 kHz                                                         │
│  ├─ Latencia total: <100ms (captura → salida)                                │
│  └─ Recomendado: Auriculares over-ear para mejor HRTF                        │
│                                                                              │
│  🦴 Bone Conduction (Futuro - no requiere audio hardware)                   │
│  └─ Integración con headphones especializados                                │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│            CAPA TRANSVERSAL: ACCESIBILIDAD & CONFIGURACIÓN                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  AccessibilityService.kt                                                     │
│  ├─ TalkBack integration                                                     │
│  ├─ Custom announcements                                                     │
│  ├─ Voice feedback (text-to-speech)                                          │
│  └─ Screen reader hints                                                      │
│                                                                              │
│  GestureDetector.kt                                                          │
│  ├─ Swipe recognition                                                        │
│  ├─ Pinch zoom                                                               │
│  ├─ Long-press                                                               │
│  └─ Double-tap                                                               │
│                                                                              │
│  HapticFeedback.kt                                                           │
│  ├─ VibrationEffect patterns                                                 │
│  └─ Haptic timing sync with audio                                            │
│                                                                              │
│  ConfigManager.kt                                                            │
│  ├─ JSON config (mappings, safety limits)                                    │
│  ├─ SharedPreferences (user prefs)                                           │
│  ├─ Runtime parameter adjustment                                             │
│  └─ Profiles/presets                                                         │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════╗
║                      LÍNEAS DE TIEMPO & SINCRONIZACIÓN                    ║
╠═══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  Thread 1: UI/Vision (Kotlin)                 Thread 2: Audio (C++/JNI)  ║
║  ┌─────────────────────────────────┐         ┌──────────────────────┐   ║
║  │ Frame 0 (t=0ms)                 │         │ Audio frame 0        │   ║
║  │ ├─ Capture frame                │         │ ├─ Read params       │   ║
║  │ ├─ Analyze (20ms)               │         │ ├─ Query LibPD       │   ║
║  │ ├─ Generate grid                │         │ ├─ Output buffer     │   ║
║  │ ├─ JSON schema                  │         │ └─ ~1.5ms latency   │   ║
║  │ └─ RingBuffer.put(json)         │         │                      │   ║
║  │                                  │         │ Audio frame 1        │   ║
║  │ Frame 1 (t=33ms)                │         │ ...                  │   ║
║  │ ├─ ...similar...                │         │ Audio frame N        │   ║
║  │ └─ RingBuffer.put(json)         │         │ (688 frames/sec)    │   ║
║  │                                  │         └──────────────────────┘   ║
║  │                                  │                                     ║
║  │ @ 30 fps: 33ms per frame         │         @ 44.1kHz: 1.5ms/block     ║
║  │ Vision latency: 20-30ms          │         Total latency: ~70ms       ║
║  │ → Total: <100ms ✓               │                                     ║
║  └─────────────────────────────────┘                                     ║
║                                                                            ║
╚═══════════════════════════════════════════════════════════════════════════╝
```

---

## 📊 FLUJO DE DATOS: EJEMPLO REAL

```
ENTRADA: Cámara mira una MONEDA METÁLICA a 1.5m de distancia

1. CAPTURA (t=0ms)
   Frame RGB 640×480 pixels
   ↓

2. ANÁLISIS (t=0-20ms)
   Grid 16×16 (40×30 pixels each)
   Cell [8,8] (centro, donde está moneda):
   ├─ Azimuth: 0° (centro)
   ├─ Elevation: 1.5m
   ├─ Distance: 1.5m
   ├─ Luminance: 0.85 (objeto brillante)
   ├─ Material: "metal" (futuro: IA detection)
   └─ Confidence: 0.92
   ↓

3. MAPEO (t=20-40ms)
   elevation_to_freq(1.5):
   → f = 60 * exp(1.7685*1.5) = 2,197 Hz
   
   distance_gain(1.5):
   → G = 1 / (1 + (1.5/1.0)²) = 0.307
   
   distance_lpf(1.5):
   → fc = 12000 * exp(-0.18*1.5) = 7,788 Hz
   
   azimuth_to_pan(0°):
   → L = 1.0, R = 1.0 (centered)
   
   luminance_to_binaural(0.85):
   → Δ = 5 + 7*0.85 = 10.95 Hz
   → Left: 4000 + 5.475 = 4,005.5 Hz
   → Right: 4000 - 5.475 = 3,994.5 Hz
   ↓

4. SÍNTESIS (t=40-60ms)
   Pure Data patch receives:
   ├─ light-freq-left: 4005.5
   ├─ light-freq-right: 3994.5
   ├─ light-amp: 0.85 * 0.307 = 0.261
   ├─ distance-lpf-cutoff: 7788
   └─ distance-gain: 0.307
   
   Generates audio samples @ 44.1kHz
   ↓

5. OUTPUT (t=60-100ms)
   AAudio callback streams samples
   → Auriculares producen:
      • Left ear: 4005.5 Hz sine
      • Right ear: 3994.5 Hz sine
      • Ambos @ 0.26 amplitud
      • 10.95 Hz binaural beat
      → Sonido: "tono brillante, metálico, a media distancia, centro"
   ↓

TOTAL LATENCIA: ~100ms
```

---

## 🔒 SAFETY & SPL PROTECTION

```
Maximum SPL Limits
├─ Default: <75 dB (safe for continuous)
├─ Peak: <85 dB (OSHA 8-hour limit)
└─ Emergency: >90 dB → automatic cutoff

Protection Mechanisms
├─ Dynamic Range Compression
│  ├─ Threshold: -20 dB
│  ├─ Ratio: 4:1
│  └─ Attack/Release: 1ms/10ms
├─ Peak Limiter
│  ├─ Threshold: -3 dB (relative to max)
│  └─ Hard clipping prevention
├─ Notch Filters (future)
│  ├─ Remove problematic frequencies
│  └─ Prevent listener fatigue
└─ User Warnings
   ├─ Volume exceeds safe → voice alert
   └─ Duration >2h → health notification
```

---

## 📈 PERFORMANCE TARGETS

```
Metric                    Target      Achieved (Phase 1)
──────────────────────────────────────────────────────
Vision Analysis           <30ms       Goal: 20-25ms
JSON Generation           <5ms        Goal: <5ms
C++ Mapping              <10ms        Goal: <8ms
LibPD Synthesis          <30ms        Goal: <25ms
Total Latency            <100ms       Goal: 60-80ms
──────────────────────────────────────────────────────
CPU Usage                <15%         Goal: 8-12%
Memory (resident)        <100MB       Goal: 50-80MB
Frame drops              0%           Goal: 0%
Audio underruns          0%           Goal: 0%
```

---

## 🔧 BUILD CONFIGURATION

```
app/build.gradle
├── compileSdk: 33
├── targetSdk: 33
├── minSdk: 24
├── ndk {
│   abiFilters: 'arm64-v8a'  # Primary (most devices)
│   # Optional: 'armeabi-v7a' (older), 'x86_64' (emulator)
│ }
└── externalNativeBuild {
    cmake {
        cppFlags: "-std=c++17 -O3"
    }
  }
```

---

## 📞 COMPONENTES CRÍTICOS

### Must-Have (Fase 1)
- ✅ AAudio engine
- ✅ LibPD integration
- ✅ JSON parser
- ✅ 6 mapping functions
- ✅ Camera capture

### Nice-to-Have (Fase 3)
- 🔲 Object detection
- 🔲 Depth estimation
- 🔲 OCR
- 🔲 Material classification

### Future (Post-release)
- 🔲 GPU acceleration
- 🔲 Bone conduction support
- 🔲 Wearable integration
- 🔲 Cloud backend (optional)

