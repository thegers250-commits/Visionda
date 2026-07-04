# VISUALONDA: Visión de Producto Pulido & Roadmap

## 🎯 VISIÓN DE PRODUCTO

**Visualonda** es una **plataforma de accesibilidad integral** que permite a personas ciegas o con discapacidad visual severa "ver" el mundo en tiempo real a través de **sonificación espacial avanzada**.

### Capacidades Transformadoras

```
┌─────────────────────────────────────────────────────────────┐
│ VISUALONDA: "VISTA POR SONIDO"                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  📱 CÁMARA EN VIVO                                          │
│     → Geometría, objetos, profundidad                       │
│     → Sonido envolvente 3D (azimut, elevación, distancia)   │
│                                                              │
│  🖥️  PANTALLA DEL TELÉFONO                                 │
│     → Navegación por la UI (botones, texto, layouts)        │
│     → Feedback sonoro para cada elemento                    │
│     → Gestos para explorar + TalkBack mejorado              │
│                                                              │
│  🎥 FOTOS / VIDEOS                                          │
│     → Análisis de contenido visual                          │
│     → Sonificación de escenas (personas, objetos, texto)    │
│     → "Ver" un álbum de fotos en sonido                     │
│                                                              │
│  🌍 NAVEGACIÓN (futuro)                                     │
│     → Audiolocalización de rutas                            │
│     → Obstáculos mapeados en sonido 3D                      │
│     → Integración con GPS + mapas                           │
│                                                              │
│  🎓 EDUCACIÓN (futuro)                                      │
│     → Geometría visualizada en sonido                       │
│     → Tablas, gráficos, infografías sonoras                 │
│     → Lectura de código fuente audiolocalizado               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Impacto Potencial

- **Independencia**: Ciegos pueden navegar interfaces sin TalkBack tradicional
- **Inmersión**: Sonido 3D binaural proporciona percepción espacial real
- **Revolucionario**: Primera app que sonifica TODA la actividad visual del teléfono
- **Escalable**: Arquitectura extensible para cámaras, sensores, APIs

---

## 📊 ARQUITECTURA DE PRODUCTO

```
┌──────────────────────────────────────────────────────────────────┐
│                         VISUALONDA                               │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  CAPA DE ENTRADA (Visión)                                       │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • Cámara (en vivo)         → Frame RGB + Depth             │ │
│  │ • Accesibilidad (screen)   → Elemento UI actual            │ │
│  │ • Galería (fotos/videos)   → Imagen/frame estático         │ │
│  │ • GPU Processor            → Geometric analysis            │ │
│  └────────────────────────────────────────────────────────────┘ │
│                            ↓                                    │
│  CAPA DE ANÁLISIS (Inteligencia)                               │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • Object Detection (TensorFlow Lite / MediaPipe)           │ │
│  │ • Scene Understanding (profundidad, layout)                │ │
│  │ • Text Recognition (OCR)                                  │ │
│  │ • Face/Hand Detection (para contexto)                     │ │
│  │ • Feature Extraction (color, textura, luz)                │ │
│  └────────────────────────────────────────────────────────────┘ │
│                            ↓                                    │
│  CAPA DE MAPEO (Sonificación)                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • Control Schema Generator (grid → JSON)                  │ │
│  │ • Mathematical Mapping (6 mapeos del Manifiesto)          │ │
│  │ • Parámetro Synthesis (freq, gain, pan, binaural beats)  │ │
│  │ • Thread-safe Ring Buffer (visión ↔ audio @ 30Hz)        │ │
│  └────────────────────────────────────────────────────────────┘ │
│                            ↓                                    │
│  CAPA DE SÍNTESIS (Audio)                                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • LibPD Engine (Pure Data embedded)                        │ │
│  │ • Oscillators (binaural beats, carriers)                  │ │
│  │ • Granular Synthesis (texturas, materiales)               │ │
│  │ • Spatial Audio (ITD, ILD, HRTF)                          │ │
│  │ • SPL Protection (limiting, compression)                  │ │
│  │ • 44.1kHz @ 64-256 sample blocks                          │ │
│  └────────────────────────────────────────────────────────────┘ │
│                            ↓                                    │
│  CAPA DE SALIDA (Audio)                                        │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • OpenSLES / AAudio (Android audio API)                   │ │
│  │ • Binaural Headphone Output (estéreo espacial)            │ │
│  │ • Bone Conduction Support (futuro)                        │ │
│  └────────────────────────────────────────────────────────────┘ │
│                            ↓                                    │
│  🎧 USUARIO (con auriculares biaurales)                       │
│                                                                  │
│  CAPA TRANSVERSAL (Configuración & Accesibilidad)              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ • Configuración JSON externa (calibración sin recompilar)  │ │
│  │ • TalkBack Integration + Voice Feedback                   │ │
│  │ • Gesture Recognition (swipe, pinch, long-press)         │ │
│  │ • Haptic Feedback (vibración táctil)                      │ │
│  │ • Privacy & Security (procesamiento local)                │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🚀 ROADMAP: 4 FASES (12-18 semanas)

### FASE 1: FOUNDATION (Semanas 1-4) — Integración Base & Audio
**Objetivo:** Audio funcional end-to-end: cámara → síntesis → auriculares

#### 1.1 LibPD Integration (Semana 1)
- [ ] Descargar prebuilt libpd para ARM64
- [ ] Setup `jniLibs/arm64-v8a/libpd.so` + headers
- [ ] Actualizar CMakeLists.txt para enlazar libpd
- [ ] Implementar `pdInit()`, `pdOpenPatch()`, `pdSendFloat()` reales
- [ ] Testing: patch .pd carga y sintetiza

**Deliverable:** Audio synth funcional en Android

#### 1.2 Audio Engine (AAudio/OpenSLES) (Semana 1-2)
- [ ] Setup AAudio callback loop
- [ ] Ring-buffer thread-safe (visión ↔ audio @ 30Hz)
- [ ] Manejo de latencia (target: <100ms)
- [ ] Volume control + SPL limiting

**Deliverable:** Audio stream fluido 44.1kHz

#### 1.3 Vision Frontend Mínimo (Semana 2-3)
- [ ] Captura de cámara Android
- [ ] Frame processing (640×480 @ 30fps)
- [ ] Análisis básico: detección de bordes, luminancia, profundidad (si disponible)
- [ ] Grid generation (16×16 celdas)
- [ ] JSON schema generation

**Deliverable:** Cámara → control_schema.json en tiempo real

#### 1.4 Mapeos Completos (Semana 3)
- [ ] Implementar 6 mapeos completos en C++:
  - Elevación → Frecuencia ✓ (ya existe)
  - Distancia → Ganancia + LPF ✓ (ya existe)
  - Azimut → Paneo binaural ✓ (ya existe)
  - Luminancia → Binaural beats ✓ (ya existe)
  - Material/Textura → Síntesis (NUEVA)
  - Confidence → Mixtura/Fade (NUEVA)
- [ ] Validación de rangos
- [ ] Logging detallado

**Deliverable:** Cámara viva → sonido envolvente 3D

#### 1.5 Testing & Calibración (Semana 4)
- [ ] Benchmark de latencia
- [ ] Tests unitarios (mapeos, parser JSON)
- [ ] Tests de integración (cámara + audio)
- [ ] Calibración inicial con usuarios piloto (si disponible)

**Milestone:** MVP Cámara funcional ✅

---

### FASE 2: ACCESIBILIDAD & NAVEGACIÓN (Semanas 5-8)
**Objetivo:** Ciegos pueden navegar teléfono + interfaz Visualonda con gestos + voz

#### 2.1 TalkBack Integration Avanzada (Semana 5)
- [ ] Accessibility Service bindings
- [ ] Custom TalkBack labels + hints sonoros
- [ ] Announcements automáticos para cambios de modo
- [ ] Screen reader compatibility
- [ ] Voice feedback (sintetizado con VoiceGender, rate, pitch)

**Deliverable:** Navegación accesible de UI completa

#### 2.2 Gesture Recognition (Semana 5-6)
- [ ] Swipe (arriba/abajo = cambiar modo, izq/der = explorar)
- [ ] Pinch (zoom sonoro virtual)
- [ ] Long-press (modo exploración detallada)
- [ ] Double-tap (seleccionar/activar)
- [ ] 3-finger swipe (menú global)

**Deliverable:** Gestos intuitivos para navegación

#### 2.3 Haptic Feedback (Semana 6)
- [ ] Vibration patterns para diferentes eventos:
  - Toque corto: elemento UI detectado
  - Toque doble: cambio de modo
  - Pulso largo: carga procesando
- [ ] Integración con VibrationEffect API
- [ ] Configuración de intensidad

**Deliverable:** Feedback táctil coherente

#### 2.4 Modo Exploración Detallado (Semana 6-7)
- [ ] Finger tracking en tiempo real
- [ ] Sonificación bajo dedo (objeto más cercano)
- [ ] Zoom sonoro (acerca análisis a región)
- [ ] Pause/resume con gesturas

**Deliverable:** Exploración fina del entorno

#### 2.5 Modos Múltiples (Semana 7)
- [ ] Modo Cámara: análisis en vivo
- [ ] Modo Pantalla: navegación UI del teléfono
- [ ] Modo Galería: análisis de fotos/videos
- [ ] Modo Mapas: localización (futuro)
- [ ] Switcher entre modos (gesture + voz)

**Deliverable:** UI modular y navegable

#### 2.6 Settings & Configuration (Semana 7-8)
- [ ] Preferences Activity accesible
  - Volumen maestro, SPL límite
  - Sensibilidad de análisis
  - Selección de mapeos (presets)
  - Idioma/voz
- [ ] External config JSON (calibración)
- [ ] SharedPreferences persistencia

**Deliverable:** Sistema de configuración completo

**Milestone:** Accesibilidad completa ✅

---

### FASE 3: INTELIGENCIA VISUAL (Semanas 9-12)
**Objetivo:** Análisis avanzado: objetos, texto, profundidad, semántica

#### 3.1 Object Detection (TensorFlow Lite) (Semana 9-10)
- [ ] Cargar modelo MobileNetV2 / YoloV5 Lite
- [ ] Inferencia @ 30fps en GPU (delegado NNAPI si disponible)
- [ ] Bounding boxes → Grid cells con "material" = clase objeto
- [ ] Confidence scores → parámetro de control
- [ ] Clases: persona, rostro, mano, auto, gato, silla, mesa, etc.

**Deliverable:** Objetos detectados en sonido 3D

#### 3.2 Depth Estimation (Monocular) (Semana 10-11)
- [ ] Usar TensorFlow Lite MiDaS o MediaPipe Depth
- [ ] Mapeo de profundidad (mapa de disparidad)
- [ ] Integración en grid: `distance_m = 1 + depth_normalized * 5`
- [ ] Refinamiento de distancia-ganancia-LPF

**Deliverable:** Percepción de profundidad sonora

#### 3.3 Text Recognition (OCR) (Semana 11)
- [ ] ML Kit Text Recognition (o Tesseract)
- [ ] Detección de texto en imagen
- [ ] Localización en grid (bounding boxes → azimut, elevación)
- [ ] Modo lectura: texto sonificado + TTS en secuencia
- [ ] Frecuencia según tamaño de fuente

**Deliverable:** Lectura de texto en sonido

#### 3.4 Segmentation & Scene Understanding (Semana 11-12)
- [ ] Semantic segmentation (techo, pared, piso, objetos)
- [ ] Mapping: segmento → "material" → síntesis
- [ ] Detección de luz (brillante/oscuro)
- [ ] Layout analysis (composición escena)

**Deliverable:** Comprensión semántica de escenas

#### 3.5 Face & Hand Detection (Semana 12)
- [ ] MediaPipe Face Mesh / Hand Tracking
- [ ] Localización en grid (cara en frente = centro + arriba)
- [ ] Expresión facial → binaural beat variante
- [ ] Mano → material especial (síntesis "mano")

**Deliverable:** Detección de personas/manos sonorizado

**Milestone:** Visión computacional integrada ✅

---

### FASE 4: PULIDO & RELEASE (Semanas 13-18)
**Objetivo:** Producto listo para mercado: performance, UX, documentación

#### 4.1 Performance Optimization (Semana 13-14)
- [ ] Profiling CPU/memoria (Android Studio Profiler)
- [ ] Optimizaciones SIMD en mapeos críticos
- [ ] GPU acceleration donde sea posible
- [ ] Caché de resultados de IA
- [ ] Adaptive FPS (reducir si sobrecarga)

**Target:** <100ms latencia total, <15% CPU promedio

#### 4.2 Audio Refinement (Semana 14)
- [ ] SPL protection hardening
- [ ] Curva de ganancia dinámica (compresión)
- [ ] Filtros notch contra fatigación
- [ ] Binaural beat safety (verificar rango 5-12 Hz)
- [ ] Recomendación de auriculares bone-conduction

**Deliverable:** Audio 100% seguro

#### 4.3 Testing & QA (Semana 14-15)
- [ ] Test suite completo (unitarios + integración)
- [ ] Regression testing
- [ ] Beta testing con 10-20 usuarios ciegos
- [ ] Feedback collection + iteración
- [ ] Bug fixes

#### 4.4 Documentation (Semana 15)
- [ ] User Manual (formato accesible + audio)
- [ ] Developer Guide (para futuras extensiones)
- [ ] API Reference (Javadoc + Doxygen)
- [ ] Architecture diagrams
- [ ] Troubleshooting guide

#### 4.5 UI/UX Refinement (Semana 15-16)
- [ ] Onboarding flow (tutorial guiado)
- [ ] Tutorial de gestos interactivo
- [ ] About screen + creditos
- [ ] Licencias + compliance
- [ ] Material Design 3 compliance

#### 4.6 Release & Distribution (Semana 16-18)
- [ ] Google Play Store submission
  - App signing + versioning
  - Screenshots + descripciones accesibles
  - Privacy policy + términos
  - Categoría: Accesibilidad
- [ ] Beta en Google Play (si deseado)
- [ ] Publicación oficial
- [ ] Social media + prensa (comunidad ciega)
- [ ] Support + feedback channels

**Milestone:** PRODUCTO EN MERCADO ✅

---

## 📁 ESTRUCTURA DE DIRECTORIO (PROPUESTA)

```
Visualonda/
├── android_skeleton/              # Aplicación Android principal
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/visualonda/sensory/
│   │   │   │   ├── MainActivity.kt              # Launcher
│   │   │   │   ├── ui/
│   │   │   │   │   ├── SettingsActivity.kt      # Configuración
│   │   │   │   │   ├── OnboardingActivity.kt    # Tutorial
│   │   │   │   │   └── CameraFragment.kt        # UI cámara
│   │   │   │   ├── core/
│   │   │   │   │   ├── VisionEngine.kt          # Captura + análisis
│   │   │   │   │   ├── AudioEngine.kt           # AAudio + síntesis
│   │   │   │   │   ├── MappingEngine.kt         # 6 mapeos
│   │   │   │   │   ├── ControlSchema.kt         # Data structures
│   │   │   │   │   └── RingBuffer.kt            # Thread-safe buffer
│   │   │   │   ├── ml/
│   │   │   │   │   ├── ObjectDetector.kt        # TensorFlow Lite
│   │   │   │   │   ├── DepthEstimator.kt        # Profundidad
│   │   │   │   │   ├── TextRecognizer.kt        # OCR
│   │   │   │   │   └── FaceDetector.kt          # Rostros
│   │   │   │   ├── accessibility/
│   │   │   │   │   ├── AccessibilityService.kt  # TalkBack integration
│   │   │   │   │   ├── GestureDetector.kt       # Gestos
│   │   │   │   │   └── HapticFeedback.kt        # Vibración
│   │   │   │   └── utils/
│   │   │   │       ├── Logger.kt
│   │   │   │       ├── ConfigManager.kt
│   │   │   │       └── PermissionManager.kt
│   │   │   ├── cpp/
│   │   │   │   ├── native-lib.cpp               # JNI main
│   │   │   │   ├── mapping_engine.cpp           # 6 mapeos (C++)
│   │   │   │   ├── audio_engine.cpp             # AAudio callbacks
│   │   │   │   ├── json_parser.cpp              # Parser JSON robusto
│   │   │   │   └── libpd_wrapper.cpp            # LibPD bindings
│   │   │   ├── assets/
│   │   │   │   ├── config_default.json          # Config por defecto
│   │   │   │   ├── presets/
│   │   │   │   │   ├── preset_outdoor.json
│   │   │   │   │   ├── preset_indoor.json
│   │   │   │   │   └── preset_minimal.json
│   │   │   │   └── patches/
│   │   │   │       └── light_material_patch.pd  # Pure Data patch
│   │   │   └── res/
│   │   │       ├── values/strings.xml           # Strings (multiidioma)
│   │   │       ├── drawable/
│   │   │       └── layout/ (XML layouts si aplica)
│   │   ├── build.gradle                        # Gradle config
│   │   └── CMakeLists.txt                      # CMake config
│   ├── build.gradle
│   └── settings.gradle
│
├── sensory-language/                # Especificación y referencia
│   ├── libpd_host/
│   │   ├── src/libpd_host.cpp
│   │   ├── CMakeLists.txt
│   │   └── README.md
│   ├── SPECIFICACION.md
│   ├── REFERENCIAS.md
│   ├── control_schema.json
│   ├── light_material_patch.pd
│   └── PD_PATCH_INSTRUCTIONS.txt
│
├── docs/                           # Documentación de producto
│   ├── USER_GUIDE.md               # Guía para usuarios
│   ├── DEVELOPER_GUIDE.md           # Guía para developers
│   ├── ARCHITECTURE.md              # Arquitectura detallada
│   ├── API_REFERENCE.md             # API reference
│   ├── ACCESSIBILITY.md             # Accesibilidad
│   └── TROUBLESHOOTING.md           # FAQ
│
├── tests/                          # Test suite
│   ├── unit/                       # Tests unitarios (C++ + Kotlin)
│   ├── integration/                # Tests de integración
│   └── e2e/                        # End-to-end tests
│
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                  # CI/CD pipeline
│   │   └── release.yml             # Release automation
│   └── ISSUE_TEMPLATE.md
│
├── README.md                       # Overview principal
├── VISION_Y_ROADMAP.md            # Este archivo
└── LICENSE                         # Licencia (recomendado: Apache 2.0 o GPL)
```

---

## 💰 ESTIMACIÓN DE RECURSOS

| Fase | Semanas | Horas | Dev/s | Costo (USD) |
|------|---------|-------|-------|-----------|
| 1 (Foundation) | 4 | 160 | 2 | $8,000 |
| 2 (Accesibilidad) | 4 | 160 | 2 | $8,000 |
| 3 (Inteligencia) | 4 | 160 | 1-2 | $8,000 |
| 4 (Pulido) | 6 | 240 | 1-2 | $12,000 |
| **TOTAL** | **18** | **720** | **2 FTE** | **$36,000** |

*Notas:*
- Asume salario promedio $50-60/hora para developers Android/NDK
- No incluye diseño gráfico, marketing, QA dedicado
- Con 2 developers full-time: 9 semanas (acelerable con más recursos)
- Open source = sin costos de licencia

---

## 🎯 KPIs DE ÉXITO

1. **Latencia:** <100ms (cámara → audio)
2. **Precisión de objetos:** >80% en condiciones de interior
3. **Usabilidad:** Usuarios ciegos navegan app sin guía en <5 min
4. **Seguridad de audio:** 0 dB-SPL warnings, <85 dB promedio
5. **Performance:** <15% CPU, <100 MB RAM
6. **Accesibilidad:** AAA compliance (WCAG 2.1)
7. **Adopción:** 1,000+ descargas en primer mes
8. **Satisfacción:** >4.5/5 en Play Store

---

## 🔐 CONSIDERACIONES DE SEGURIDAD & PRIVACIDAD

1. **Procesamiento Local:** Todo análisis de visión en-device (sin cloud)
2. **Permisos Mínimos:** Solo CAMERA + RECORD_AUDIO requeridos
3. **No Storage:** Frames de cámara nunca persistidos (solo en RAM)
4. **Encrypted Config:** Preferencias encriptadas con Android Keystore
5. **Auditing:** Logs de acceso (opcional, para debugging)

---

## 🚀 PRÓXIMOS PASOS

1. **Validación de requisitos:** ¿Coincide esta visión con tu intención?
2. **Setup de equipo:** ¿Tienes devs Android + NDK disponibles?
3. **Inicio Fase 1:** ¿Empezamos con LibPD integration esta semana?
4. **Beta testers:** Conectar con usuarios ciegos para feedback temprano

---

## 📞 CONTACTO & FEEDBACK

Este roadmap es vivo — ajustable según feedback del equipo y usuarios.

**Preguntas clave:**
- ¿Prioridad: velocidad o perfección?
- ¿Budget/timeline definido?
- ¿Tienes acceso a usuarios ciegos para testing?
- ¿Soporte post-release esperado?

