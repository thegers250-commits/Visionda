# 🚀 PLAN PARA COMPLETAR VISUALONDA - 18 SEMANAS

**Documento de acción: Qué falta, cómo hacerlo, en qué orden**

---

## 📊 ESTADO ACTUAL vs META

### **HOY (70% incompleto)**
```
✅ Arquitectura MVVM + Hilt
✅ Build configurado
✅ 6 mapeos matemáticos (en C++)
✅ Modelos de datos

❌ Cámara NO funciona
❌ Audio NO funciona
❌ Síntesis NO ocurre
❌ Accesibilidad NO existe
❌ Tests NO hay
❌ ML NO está
```

### **META (100% completo)**
```
✅ Cámara capturando 30fps
✅ Audio sintetizando en tiempo real
✅ Latencia <100ms
✅ TalkBack + gestos accesibles
✅ ML: detectar objetos, profundidad, texto, caras
✅ 80%+ cobertura de tests
✅ APK en Google Play Store
```

---

## 🎯 PLAN DE 18 SEMANAS (4 FASES)

---

## **FASE 0: SETUP (3-5 DÍAS) ← YA CASI LISTO**

### Qué falta:
```
❌ Compilar el proyecto sin errores
❌ Descargar libpd.so
❌ Verificar que todo compila
```

### Cómo hacerlo:
```
1. Descargar libpd-android de GitHub
2. Copiar libpd.so a: app/src/main/jniLibs/arm64-v8a/
3. Ejecutar: ./gradlew clean build
4. Resultado: BUILD SUCCESSFUL ✓
```

### Tiempo: **1 día**

---

## **FASE 1: AUDIO ENGINE (SEMANAS 1-4)** ← CRÍTICA

### Semana 1: Inicialización LibPD

**Qué falta:**
```
❌ libpd_wrapper.cpp (180 líneas)
   - Inicializar LibPD
   - Cargar patch
   - Thread-safe binding

❌ audio_engine.cpp (225 líneas)
   - AAudio initialization
   - Callback real-time
   - Ring buffer

❌ mapping_engine.cpp (340 líneas)
   - Implementar 6 mapeos
   - Procesamiento de parámetros
```

**Cómo hacerlo:**
```
1. Crear archivo: app/src/main/cpp/libpd_wrapper.cpp
   ├─ Función: pd_initialize()
   ├─ Función: pd_open_patch(path)
   ├─ Función: pd_send_float(name, value)
   ├─ Thread-safe con mutex
   └─ Error handling

2. Crear archivo: app/src/main/cpp/mapping_engine.cpp
   ├─ elevation_to_freq()      ✓ (ya existe)
   ├─ distance_gain()          ✓ (ya existe)
   ├─ distance_lpf_cutoff()    ✓ (ya existe)
   ├─ azimuth_to_pan()         (nueva)
   ├─ luminance_to_modulation() (nueva)
   └─ material_to_timbre()     (nueva)

3. Crear archivo: app/src/main/cpp/audio_engine.cpp
   ├─ AAudioStreamBuilder setup
   ├─ Callback function (real-time)
   ├─ Ring buffer para visión ↔ audio
   └─ SPL limiter
```

**Archivos a crear:**
- libpd_wrapper.h (25 líneas)
- libpd_wrapper.cpp (180 líneas) 
- mapping_engine.h (30 líneas)
- mapping_engine.cpp (340 líneas)
- audio_engine.h (40 líneas)
- audio_engine.cpp (225 líneas)
- json_parser.cpp (285 líneas)

**Resultado esperado:**
```
App compila sin errores
LibPD inicializa en MainActivity
Botón "Init PD" funciona
Logs muestran: "[PD] Initialized successfully"
```

**Tiempo: 40 horas (4-5 días para 1 dev)**

---

### Semana 2: Cámara + Frame Processing

**Qué falta:**
```
❌ CameraRepositoryImpl.startCapture()
   - CameraX integration
   - 320x240 resolution
   - 30fps target
   - YUV frame extraction

❌ Frame processor
   - Grid generation (16x16)
   - Luminance extraction
   - Position calculation
   - JSON generation
```

**Cómo hacerlo:**
```
1. Actualizar CameraRepositoryImpl.kt (~150 líneas)
   ├─ CameraX Provider
   ├─ ImageAnalysis Executor
   ├─ BackpressureStrategy
   ├─ emit(ControlFrame) via Flow
   └─ Error handling

2. Crear frame_processor.cpp (~250 líneas)
   ├─ Grid generation algorithm
   ├─ YUV to luminance conversion
   ├─ Cell feature extraction
   └─ JSON serialization

3. Crear ControlFrame data class (si no existe)
   ├─ timestamp: Long
   ├─ frameRate: Int
   ├─ cells: List<ControlCell>
   └─ confidence: Float
```

**Resultado esperado:**
```
App captura frames @ 30fps
Grid 16x16 generado
JSON con parámetros visuales
Logs muestran: "[FRAME] 320x240 @ 30fps, 256 cells"
```

**Tiempo: 40 horas (4-5 días)**

---

### Semana 3: Integration + End-to-End

**Qué falta:**
```
❌ Conectar Camera → Mapeo → Audio
❌ MainActivity: botones funcionales
❌ Medir latencia
❌ Optimizar buffer sizes
```

**Cómo hacerlo:**
```
1. MainActivity.kt (~200 líneas)
   ├─ Botón "Iniciar Audio" → audio_engine.start()
   ├─ Botón "Iniciar Cámara" → camera.startCapture()
   ├─ Botón "Enviar Frame" → test con frame fake
   ├─ Mostrar parámetros en Log
   └─ Error handling

2. Crear ProcessFrameUseCase.kt (~100 líneas)
   ├─ Camera frame input
   ├─ Call mapping_engine
   ├─ Send to audio_engine
   └─ Measure latency

3. Tests unitarios (~300 líneas)
   ├─ Test elevation_to_freq()
   ├─ Test distance_gain()
   ├─ Test JSON parsing
   ├─ Test latency <100ms
   └─ Integration tests
```

**Resultado esperado:**
```
Camera → Mapeo → Audio end-to-end funciona
Latencia medida: ~95ms (target <100ms) ✓
Audio reproducido sin crashes
```

**Tiempo: 40 horas**

---

### Semana 4: Performance Tuning

**Qué falta:**
```
❌ Optimizar grid processing
❌ Reducir jitter en audio
❌ Memory profiling
❌ Battery impact analysis
```

**Cómo hacerlo:**
```
1. Profiling:
   - Android Studio Profiler
   - CPU time per frame
   - Memory allocation
   - Battery drain

2. Optimizaciones:
   - Reduce grid size (16→8 para test)
   - Optimize JSON parsing
   - Ring buffer tuning
   - Reduce latency to <80ms

3. Testing:
   - 1+ hour continuous run
   - Memory leak detection
   - Crash handling
   - Edge cases
```

**Resultado esperado:**
```
Latencia: <80ms
Memory: <100MB
CPU: <30% sustained
Battery: <5%/hour
0 crashes en 1 hora de uso
```

**Tiempo: 40 horas**

---

## **FASE 2: ACCESIBILIDAD (SEMANAS 5-8)**

### Semana 5-6: TalkBack + Gestos

**Qué falta:**
```
❌ TalkBack integration
❌ Screen reader announcements
❌ Custom gesture recognition
❌ Haptic feedback
```

**Cómo hacerlo:**
```
1. Accessibility features (~300 líneas Kotlin)
   ├─ AccessibilityService integration
   ├─ Custom gesture detector
   ├─ Haptic feedback patterns
   ├─ Live announcements
   └─ contentDescription for all views

2. Gesture recognition (~150 líneas)
   ├─ Swipe up: volumen +
   ├─ Swipe down: volumen -
   ├─ Tap 2x: iniciar/parar
   ├─ Hold 3s: cambiar modo
   └─ Custom patterns

3. Testing (~200 líneas)
   ├─ Accessibility validator
   ├─ Gesture tests
   ├─ User testing con ciegos
```

**Resultado esperado:**
```
TalkBack funciona 100%
Gestos reconocidos correctamente
Haptic feedback percibido
5+ blind users test OK
```

**Tiempo: 80 horas (2 devs)**

---

### Semana 7-8: User Testing & Iteration

**Qué falta:**
```
❌ Beta testing con 10+ ciegos
❌ Feedback gathering
❌ UX improvements
❌ Documentation
```

**Cómo hacerlo:**
```
1. Recruit beta testers:
   - Contact blind organizations
   - 10-15 people
   - Diverse backgrounds

2. Testing protocol:
   - 2-hour sessions
   - Structured tasks
   - Open-ended feedback
   - Think-aloud protocol

3. Iterate based on feedback:
   - Fix bugs
   - Adjust latency
   - Improve audio quality
   - Add features
```

**Resultado esperado:**
```
User feedback: "Easy to use"
Rating: >4/5 stars
Bugs: 0 blockers
Ready for Phase 3
```

**Tiempo: 80 horas**

---

## **FASE 3: INTELIGENCIA (SEMANAS 9-12)**

### Semana 9: Object Detection

**Qué falta:**
```
❌ TensorFlow Lite integration
❌ Object detection model
❌ Real-time inference
❌ Bounding box → ControlCell
```

**Cómo hacerlo:**
```
1. Add TensorFlow Lite (~150 líneas)
   ├─ gradle: org.tensorflow:tensorflow-lite:+latest
   ├─ Model: MobileNetV3 (COCO dataset)
   ├─ Interpreter setup
   ├─ Input/output binding

2. Object detection (~200 líneas)
   ├─ Frame preprocessing
   ├─ Run inference
   ├─ Extract bounding boxes
   ├─ Filter by confidence >0.5
   └─ Map to grid cells

3. Integration:
   ├─ Add to frame processor
   ├─ Update ControlCell with class_name
   ├─ Time the inference (<50ms)
```

**Resultado esperado:**
```
Detects: person, dog, car, chair, etc.
Accuracy: >80%
Latency: <50ms per frame
Confidence: >0.5
```

**Tiempo: 60 horas**

---

### Semana 10: Depth Estimation

**Qué falta:**
```
❌ Depth estimation model
❌ Per-pixel depth map
❌ Distance calculation
```

**Cómo hacerlo:**
```
1. Add depth model:
   ├─ Download: MiDaS or LeNet
   ├─ Convert to TFLite
   ├─ Add to app/assets

2. Depth inference (~150 líneas):
   ├─ Run model on 320x240
   ├─ Get depth map
   ├─ Per-cell average depth
   ├─ Convert to meters
   └─ Clamp to 0-10m range

3. Integration:
   ├─ Replace fake distance
   ├─ Update ControlCell.distance_m
   ├─ Measure accuracy
```

**Resultado esperado:**
```
Depth map generated @ 30fps
Distance accuracy: ±0.5m
Latency: <30ms
No jitter (smooth transitions)
```

**Tiempo: 40 horas**

---

### Semana 11: OCR + Face Recognition

**Qué falta:**
```
❌ OCR model (Google Lens)
❌ Face detection
❌ Face recognition (optional)
```

**Cómo hacerlo:**
```
1. OCR (~100 líneas):
   ├─ Use Google ML Kit Text Recognition
   ├─ Per-frame detection
   ├─ Extract bounding boxes
   ├─ Add text → ControlCell

2. Face detection (~80 líneas):
   ├─ Google ML Kit Face Detection
   ├─ Per-frame faces found
   ├─ Bounding box → ControlCell
   ├─ Optional: emotional estimation

3. Integration:
   ├─ Add to frame processor
   ├─ Priority: Objects > Faces > Text
   ├─ Measure latency
```

**Resultado esperado:**
```
OCR: Reads text >80% accuracy
Faces: Detects >95% accuracy
Text alerts user when text appears
Face alerts user when face appears
Total latency: <100ms
```

**Tiempo: 40 horas**

---

### Semana 12: Performance Optimization

**Qué falta:**
```
❌ Model quantization
❌ Latency optimization
❌ Memory optimization
❌ Battery impact
```

**Cómo hacerlo:**
```
1. Quantization:
   - Convert models to int8
   - Reduce size 4x
   - Faster inference

2. Caching:
   - Cache recent detections
   - Skip redundant frames
   - Reduce CPU

3. Testing:
   - Latency: target <80ms
   - Memory: <150MB
   - Battery: <8%/hour
   - CPU: <40% sustained
```

**Resultado esperado:**
```
Latency: <80ms ✓
Memory: <150MB ✓
Battery: <8%/hour ✓
CPU: <40% ✓
All models running
```

**Tiempo: 40 horas**

---

## **FASE 4: RELEASE (SEMANAS 13-18)**

### Semana 13-14: Testing & Quality Gate

**Qué falta:**
```
❌ 80%+ unit test coverage
❌ Integration tests
❌ UI/Accessibility tests
❌ Performance tests
❌ Security tests
```

**Cómo hacerlo:**
```
1. Unit tests (~2000 líneas):
   - Test all mappings
   - Test JSON parsing
   - Test models
   - >80% coverage

2. Integration tests (~800 líneas):
   - End-to-end pipeline
   - Latency validation
   - Crash handling

3. Accessibility tests (~400 líneas):
   - TalkBack scenarios
   - Gesture recognition
   - Screen reader output

4. Performance tests (~600 líneas):
   - Latency benchmarks
   - Memory profiling
   - Battery drain measurement
```

**Result:**
```
Coverage: 82%
All tests passing
0 crashes
Latency: <80ms
Memory: <150MB
```

**Tiempo: 80 horas**

---

### Semana 15: Documentation

**Qué falta:**
```
❌ User guide (40 páginas)
❌ Developer guide (25 páginas)
❌ API documentation
❌ Privacy policy
❌ Terms of service
```

**Cómo hacerlo:**
```
1. User guide:
   - How to install
   - How to use gestures
   - Troubleshooting
   - Examples

2. Developer guide:
   - Architecture
   - How to add features
   - Testing
   - Deployment

3. Legal:
   - Privacy policy
   - Terms of service
   - Data handling
```

**Result:**
```
65 pages total
All sections complete
Reviewed by legal
Ready for store
```

**Tiempo: 40 horas**

---

### Semana 16: Hearing Safety & Final QA

**Qué falta:**
```
❌ SPL measurement validation
❌ Hearing damage prevention
❌ Final bug fixes
❌ Final performance tuning
```

**Cómo hacerlo:**
```
1. Hearing safety:
   - SPL limiter validation
   - Test at maximum volume
   - Ensure <85dB SPL
   - Add safety warnings

2. Final QA:
   - 48-hour soak test
   - Edge cases
   - Device compatibility
   - Battery drain

3. Final tuning:
   - Latency: target <50ms
   - Memory: <150MB
   - CPU: <35%
```

**Result:**
```
SPL: <85dB guaranteed ✓
0 crashes in 48h
Latency: <50ms
Memory: <150MB
Battery: <7%/hour
Ready for store submission
```

**Tiempo: 40 horas**

---

### Semana 17-18: App Store Submission

**Qué falta:**
```
❌ Build release APK
❌ Create store listing
❌ Upload to Google Play
❌ Wait for review
❌ Monitor initial feedback
```

**Cómo hacerlo:**
```
1. Build APK:
   - ./gradlew assembleRelease
   - Sign with release keystore
   - Test on real devices

2. Store listing:
   - Screenshots (5x)
   - Description (80 chars)
   - Full description (4000 chars)
   - Category: Accessibility
   - Rating: 18+ (optional)

3. Upload:
   - Console.google.com/google play
   - Upload APK + metadata
   - Set launch date
   - Submit for review

4. Monitor:
   - Check review status
   - Fix any rejection issues
   - Respond to user reviews
   - Track downloads
```

**Result:**
```
📱 APP LIVE IN GOOGLE PLAY STORE 🎉

✅ Published
✅ Available worldwide
✅ Free download
✅ Open source on GitHub
✅ Community feedback incoming
```

**Tiempo: 40 horas + waiting for review**

---

## 📋 RESUMEN DE TAREAS POR FASE

| Fase | Semanas | Tareas | LOC | Horas | Dev |
|------|---------|--------|-----|-------|-----|
| 0 | 0-1 | Setup | 500 | 8 | 1 |
| 1 | 1-4 | Audio Engine | 1,200 | 160 | 2 |
| 2 | 5-8 | Accessibility | 800 | 160 | 1 |
| 3 | 9-12 | ML & Intelligence | 1,500 | 180 | 2 |
| 4 | 13-18 | Testing & Release | 3,500 | 240 | 2 |
| **TOTAL** | **18** | **-** | **~7,500** | **~800** | **2-3 FTE** |

---

## 🎯 HITOS CRÍTICOS (No pasar sin cumplir)

```
✓ Semana 1: Audio engine compila sin errores
✓ Semana 2: Cámara captura @ 30fps
✓ Semana 4: Latencia <100ms end-to-end
✓ Semana 8: 10+ blind users test OK, >4★
✓ Semana 12: ML models integrated, latency <80ms
✓ Semana 14: 80%+ test coverage, 0 crashes
✓ Semana 18: APP IN GOOGLE PLAY STORE 🎉
```

---

## 💰 RECURSOS REQUERIDOS

### **Equipo:**
```
- Android Developer (2 FTE) - Kotlin, JNI
- ML Engineer (1 FTE) - TensorFlow, optimization
- QA/Accessibility (1 FTE) - Testing, blind user coordination
- Tech Lead (0.5 FTE) - Architecture, decisions
```

### **Budget:**
```
Salaries (18 weeks, 3.5 FTE):     $210,000
Infrastructure (CI/CD, tools):     $10,000
Testing (devices, lab):             $15,000
Legal (privacy, terms):             $5,000
Marketing/Community:                $5,000
TOTAL:                              $245,000
```

### **Infrastructure:**
```
- CI/CD: GitHub Actions (free)
- Testing: Android Emulator + Real Devices (6+)
- Analytics: Firebase (free tier)
- Distribution: Google Play (one-time $25)
- Community: GitHub + Discord (free)
```

---

## ✅ SUCCESS CRITERIA (18 WEEKS)

```
✓ App published in Google Play Store
✓ >1,000 downloads
✓ >4.0★ rating
✓ 0 crashes
✓ <80ms latency
✓ <85dB SPL (hearing safe)
✓ 80%+ test coverage
✓ 100% documented
✓ Open source
✓ Blind users can use independently
```

---

## 🚀 START NOW

**Hoy (Semana 0):**
1. ✅ Compilar proyecto
2. ✅ Descargar libpd.so
3. ⏳ Ready for Semana 1

**Mañana (Semana 1 Día 1):**
1. Crear libpd_wrapper.cpp
2. Crear audio_engine.cpp
3. Crear mapping_engine.cpp (expandir stubs existentes)
4. Empezar tests

---

**PRÓXIMO PASO:** ¿Cuál es tu rol? Te doy las tareas específicas para esta semana.

