# ✅ TODO - FASE 1 COMPLETA

**Estado:** Fase 0 completada, Fase 1 en progreso  
**Duración:** 4 semanas (18-20 horas/semana)  
**Objetivo Final:** Audio + Camera + Mapeos funcionales

---

## 📋 CHECKLIST MAESTRA

### SEMANA 1: LibPD Integration
```
DAY 1 (LUNES):
☐ Descargar libpd-0.12.x-android.zip
☐ Extraer headers (libpd.h, pd.h, m_pd.h)
☐ Copiar libpd.so a jniLibs/arm64-v8a/
☐ Copiar headers a cpp/libpd/include/
☐ Crear Pure Data patch (light_material_patch.pd)
☐ Actualizar CMakeLists.txt con rutas LibPD
☐ ✅ DONE: LibPD infrastructure ready

DAY 2 (MARTES):
☐ Crear libpd_wrapper.h
☐ Crear libpd_wrapper.cpp (180 líneas)
   ├─ libpd_wrapper_init()
   ├─ libpd_wrapper_load_patch()
   ├─ libpd_wrapper_send_float()
   └─ libpd_wrapper_cleanup()
☐ ✅ DONE: LibPD wrapper complete

DAY 3 (MIÉRCOLES):
☐ Actualizar native-lib.cpp con JNI functions
   ├─ Java_com_visualonda_sensory_MainActivity_pdInit
   ├─ Java_com_visualonda_sensory_MainActivity_pdOpenPatch
   ├─ Java_com_visualonda_sensory_MainActivity_pdSendFloat
   └─ Java_com_visualonda_sensory_MainActivity_pdCleanup
☐ ✅ DONE: JNI bindings complete

DAY 4 (JUEVES):
☐ Crear layout/activity_main.xml con 3 botones
☐ Actualizar MainActivity.kt
   ├─ Load library (System.loadLibrary)
   ├─ Call pdInit() on button click
   ├─ Call pdOpenPatch() on button click
   ├─ Call pdSendFloat() on button click
   └─ UI logging for verification
☐ ✅ DONE: UI complete

DAY 5 (VIERNES):
☐ Git push a GitHub
☐ GitHub Actions compila
☐ Descargar APK
☐ Instalar en dispositivo
☐ Testing: 3 botones funcionales
☐ Verificación: 0 crashes
☐ ✅ GATE 1: SEMANA 1 APPROVED

ENTREGABLE SEMANA 1:
├─ libpd_wrapper.cpp (180 líneas)
├─ libpd_wrapper.h
├─ native-lib.cpp JNI (150 líneas nuevas)
├─ activity_main.xml (layout)
├─ MainActivity.kt (actualizado)
└─ APP FUNCIONAL: 3 botones, 0 crashes
```

---

### SEMANA 2: Audio Engine (AAudio)
```
DAY 6-7 (LUNES-MARTES):
☐ Crear audio_engine.h
☐ Crear audio_engine.cpp (225 líneas)
   ├─ audio_engine_init() → AAudioStreamBuilder
   ├─ audio_callback() → sine wave generation
   ├─ audio_engine_set_frequency()
   ├─ audio_engine_set_amplitude()
   └─ audio_engine_cleanup()
☐ Actualizar CMakeLists.txt: link aaudio library
☐ Agregar JNI functions en native-lib.cpp
☐ ✅ DONE: Audio engine complete

DAY 8 (MIÉRCOLES):
☐ Actualizar MainActivity UI: add audio buttons
   ├─ "Init Audio Engine"
   ├─ "Set Freq 1000 Hz"
   ├─ "Set Freq 2000 Hz"
   └─ "Stop Audio"
☐ Test: Escuchar tono @ 4kHz default
☐ Test: Cambiar frecuencia con botones
☐ ✅ DONE: Audio UI complete

DAY 9 (JUEVES):
☐ Performance optimization
   ├─ Medir latencia actual (AAudio provides via getFramesPerBurst)
   ├─ Verify <100ms latency
   ├─ Verify <15% CPU usage
   ├─ Verify no audio underruns
☐ ✅ DONE: Performance tuned

DAY 10 (VIERNES):
☐ Testing: AAudio @ 44.1kHz
☐ Testing: Stereo panning funcional
☐ Testing: Amplitude control
☐ Verificación: 0 crashes
☐ ✅ GATE 2: SEMANA 2 APPROVED

ENTREGABLE SEMANA 2:
├─ audio_engine.cpp (225 líneas)
├─ audio_engine.h
├─ native-lib.cpp updates (100 líneas)
├─ MainActivity updates (2 botones)
└─ APP FUNCIONAL: Audio audible, <100ms latency
```

---

### SEMANA 3-4: Vision + Mapeos Completos
```
WEEK 3 (LUNES-VIERNES):
☐ Integrar CameraX (ActivityCompat, permissions)
   ├─ Setup camera capture @ 30fps
   ├─ Procesar frames (ImageAnalysis)
   └─ Extract luminance channel (Y en YUV)

☐ Crear mapping_engine.cpp (340 líneas)
   ├─ map_elevation_to_frequency()
   ├─ map_distance_to_gain()
   ├─ map_distance_to_lpf_cutoff()
   ├─ map_azimuth_to_stereo_pan()
   ├─ map_luminance_to_modulation()
   └─ map_material_to_timbre()

☐ Crear json_parser.cpp (285 líneas)
   ├─ parse_control_schema() → ControlFrame
   ├─ Validate JSON estructura
   └─ Extract cell parameters

☐ Actualizar MainActivity:
   ├─ Add "Start Camera" button
   ├─ Requestpermissions (CAMERA, RECORD_AUDIO)
   ├─ CameraX ImageAnalysis setup
   └─ Generate control_schema.json per frame

☐ Testing:
   ├─ Cámara abre @ 30fps
   ├─ Frames procesados sin lag
   ├─ Mapeos calculados correctamente
   ├─ JSON parsed sin errores
   └─ 0 crashes

WEEK 4 (LUNES-VIERNES):
☐ Integrar TODO en pipeline end-to-end:
   ├─ Camera frame → grid 16x16
   ├─ Grid → JSON control_schema
   ├─ JSON → mapping_engine
   ├─ Mappings → LibPD receivers
   ├─ LibPD → AAudio buffer
   └─ AAudio → Speaker (audible!)

☐ Latency profiling:
   ├─ Camera → JSON: <10ms
   ├─ JSON → Mapping: <5ms
   ├─ Mapping → LibPD: <5ms
   ├─ Total E2E: <50ms (target)

☐ Testing:
   ├─ Move camera: Escuchas cambio de audio en TIEMPO REAL
   ├─ Zoom in/out: Escuchas cambio de ganancia
   ├─ Move left/right: Escuchas pan estéreo
   └─ 0 crashes en 30 min prueba

☐ GATE 3: SEMANA 3-4 APPROVED

ENTREGABLE SEMANA 3-4:
├─ mapping_engine.cpp (340 líneas)
├─ mapping_engine.h
├─ json_parser.cpp (285 líneas)
├─ json_parser.h
├─ MainActivity + CameraX integration
├─ ControlFrame.kt + ControlCell.kt (modelos)
└─ VERSIÓN 1.0 MVP: 
   ├─ Camera input
   ├─ Real-time audio synthesis
   ├─ 6 mappings funcionales
   ├─ <50ms latency
   └─ AUDIBLE END-TO-END
```

---

## 📊 RESUMEN FASE 1 (4 SEMANAS)

```
SEMANA 1 (Days 1-5):      LibPD Integration
   Entrada: Stubs
   Salida: pdInit(), pdOpenPatch(), pdSendFloat()
   
SEMANA 2 (Days 6-10):     Audio Engine (AAudio)
   Entrada: LibPD funcional
   Salida: Audio @ 44.1kHz, <100ms latency
   
SEMANA 3 (Days 11-15):    Vision Frontend + Mapping Engine
   Entrada: Audio funcional
   Salida: Camera → Mapping pipeline
   
SEMANA 4 (Days 16-20):    End-to-End Integration
   Entrada: Audio + Vision + Mappings
   Salida: AUDIBLE REALTIME SYNTHESIS
   
TOTAL: ~160 horas (4 FTE × 5 días/sem × 8h/día)
RESULTADO: MVP COMPLETAMENTE FUNCIONAL
```

---

## 🎯 CÓDIGO FINAL (TOTAL FASE 1)

```
NUEVA CODIGO ESCRITO:
├─ libpd_wrapper.cpp/h        (180 líneas)
├─ audio_engine.cpp/h         (225 líneas)
├─ mapping_engine.cpp/h       (340 líneas)
├─ json_parser.cpp/h          (285 líneas)
├─ native-lib.cpp (updated)   (150 líneas nuevas)
├─ MainActivity.kt (updated)  (100 líneas nuevas)
├─ CMakeLists.txt (updated)   (30 líneas nuevas)
└─ build.gradle (updated)     (10 líneas nuevas)

TOTAL NUEVO: 1,320 líneas código funcional

ARQUITECTURA:
Camera (30fps)
    ↓
Frame Processing (grid 16x16)
    ↓
JSON Generation (control_schema)
    ↓
Mapping Engine (6 mappings)
    ↓
LibPD (audio synthesis)
    ↓
AAudio (real-time playback)
    ↓
Speaker (AUDIBLE!)
```

---

## 📱 FUNCIONALIDADES POR SEMANA

### Después SEMANA 1:
```
✅ Presionar botón: Escuchas voz de LibPD en Pd console
✅ App no crashea
✅ JNI communication funciona
```

### Después SEMANA 2:
```
✅ Presionar botón: Escuchas tono @ 1000Hz
✅ Presionar botón: Cambias a 2000Hz
✅ Latencia <100ms (medible)
✅ Audio estéreo funcional
```

### Después SEMANA 3:
```
✅ Presionar "Start Camera": Cámara abre
✅ Move object frente cámara: Cambia tono
✅ Move left/right: Stereo panning
✅ Move cerca/lejos: Ganancia aumenta/disminuye
```

### Después SEMANA 4:
```
✅ VERSIÓN 1.0 MVP COMPLETA
✅ Camera → Audio en tiempo real (<50ms latency)
✅ 6 mappings funcionales
✅ Accesible: Usuario ciego puede navegar usando audio
✅ Listo para Fase 2 (Accesibilidad)
```

---

## 🎬 ACCIONES INMEDIATAS

### HOY:
```
1. Lee: 🚀_COMIENZA_AQUI.md (5 min)
2. Ve a GitHub Actions: https://github.com/.../Visionda/actions
3. Si APK listo: Descarga y prueba (15 min)
4. Si todavía compilando: Espera + lee documentación
```

### LUNES (Day 1 Week 1):
```
1. Descargar libpd-0.12.x-android.zip
2. Extraer headers + .so
3. Copiar a proyecto
4. Verificar CMakeLists.txt
5. Git push
6. GitHub Actions compila
7. Verificar 0 compilation errors
```

### VIERNES (Day 5 Week 1):
```
1. Descargar APK (compilación exitosa)
2. Instalar en teléfono
3. Presionar 3 botones
4. Verificar 0 crashes
5. ✅ GATE 1 APROBADO
6. Comenzar Semana 2 lunes
```

---

## ✅ CONCLUSIÓN FASE 1

**4 semanas**  
**~160 horas de trabajo**  
**1,320 líneas de código**  
**1 MVP funcional**

Resultado:
- ✅ Audio síntesis en tiempo real
- ✅ Camera input procesado
- ✅ 6 mappings matemáticos
- ✅ Latencia <50ms
- ✅ AUDIBLE a usuario

**Próxima:** Fase 2 - Accesibilidad (TalkBack, gestos, haptics)

---

## 🔗 DOCUMENTACIÓN REFERENCIA

| Necesito | Documento |
|----------|-----------|
| Week 1 detallado | SEMANA_1_PLAN_DETALLADO.md |
| Week 2 detallado | SEMANA_2_PLAN_DETALLADO.md |
| Roadmap completo | PLAN_EJECUCION_COMPLETO.md |
| Empezar HOY | 🚀_COMIENZA_AQUI.md |
| Estado actual | STATUS_FASE_0.md |
| Próximos pasos | PASOS_SIGUIENTES.md |

---

**Creado:** Julio 4, 2026  
**Status:** ✅ Fase 0 Completada, Fase 1 TODO listed  
**Próxima Acción:** Week 1 Day 1 (Monday)

