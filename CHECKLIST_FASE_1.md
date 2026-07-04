# CHECKLIST FASE 1: Foundation (Semanas 1-4)

## 📋 SEMANA 1: LibPD Integration + AAudio Setup

### 1.1 LibPD Setup
- [ ] Descargar libpd prebuilt ARM64 de https://github.com/libpd/libpd/releases
- [ ] Crear carpeta: `android_skeleton/app/src/main/jniLibs/arm64-v8a/`
- [ ] Copiar `libpd.so` a `jniLibs/arm64-v8a/`
- [ ] Crear carpeta: `android_skeleton/app/src/main/cpp/libpd/include/`
- [ ] Descargar headers (libpd.h, pd.h, m_pd.h)
- [ ] Copiar headers a `cpp/libpd/include/`

### 1.2 CMakeLists.txt Update
- [ ] Reemplazar `app/CMakeLists.txt` con versión que enlaza libpd
- [ ] Verificar rutas relativas de libpd.so
- [ ] Incluir `-std=c++17` flag

### 1.3 Crear libpd_wrapper.cpp
- [ ] Crear `app/src/main/cpp/libpd_wrapper.cpp`
  - [ ] Función `libpd_init()`
  - [ ] Función `libpd_load_patch()`
  - [ ] Función `libpd_send_float()`
  - [ ] Función `libpd_cleanup()`
  - [ ] Thread-safe con mutex

### 1.4 Actualizar native-lib.cpp
- [ ] Reemplazar `pdInit()` stub
- [ ] Reemplazar `pdOpenPatch()` stub
- [ ] Reemplazar `pdSendFloat()` stub
- [ ] Incluir header `libpd_wrapper.h`
- [ ] Compilar y verificar sin errores

### 1.5 Testing Semana 1
- [ ] Build sin errores
- [ ] LogCat muestra "LibPD initialized"
- [ ] Cargar patch.pd sin crash
- [ ] pdSendFloat() registra logs

**MILESTONE:** LibPD integrado ✅

---

## 📋 SEMANA 2: Audio Engine (AAudio)

### 2.1 Crear audio_engine.cpp
- [ ] Crear `app/src/main/cpp/audio_engine.cpp`
  - [ ] `audio_callback()` función
  - [ ] `audio_engine_init()` setup AAudio
  - [ ] `audio_engine_cleanup()` cleanup
  - [ ] Generar onda de prueba (sine @ 4000 Hz)

### 2.2 CMakeLists.txt Audio
- [ ] Agregar `audio_engine.cpp` a sources
- [ ] Link android audio library (`aaudio`)
- [ ] Include AAudio headers

### 2.3 Crear audio_engine.h
- [ ] Header guard
- [ ] Declarar funciones públicas:
  ```cpp
  extern "C" {
    bool audio_engine_init();
    void audio_engine_cleanup();
  }
  ```

### 2.4 Actualizar native-lib.cpp para AAudio
- [ ] Agregar JNI functions:
  - [ ] `Java_com_visualonda_sensory_MainActivity_audioEngineInit`
  - [ ] `Java_com_visualonda_sensory_MainActivity_audioEngineCleanup`
- [ ] Conectar a libpd_wrapper

### 2.5 Actualizar build.gradle
- [ ] Agregar NDK ABI filter: `arm64-v8a`
- [ ] Verificar C++ standard: 17
- [ ] Agregar CMake path

### 2.6 Testing Semana 2
- [ ] Build sin errores
- [ ] audioEngineInit() no crashea
- [ ] AAudio stream abierto (verificar en Logcat)
- [ ] Conectar auriculares → se escucha tono @ 4000 Hz
- [ ] audioEngineCleanup() cierra stream

**MILESTONE:** Audio engine funcional ✅

---

## 📋 SEMANA 3: Vision Frontend

### 3.1 Actualizar MainActivity.kt
- [ ] Agregar permisos: CAMERA + RECORD_AUDIO
- [ ] Crear funciones de captura de cámara
- [ ] Usar CameraX library (AndroidX)
- [ ] ImageAnalysis @ 30 fps
- [ ] Generar grid 16x16

### 3.2 Actualizar AndroidManifest.xml
- [ ] Verificar `CAMERA` permission ✓
- [ ] Verificar `RECORD_AUDIO` permission ✓

### 3.3 Crear json_parser.cpp (C++)
- [ ] Parsear JSON robusto
- [ ] Extraer: azimuth, elevation, distance, luminance
- [ ] Generar schema JSON desde frame
- [ ] Error handling

### 3.4 Integración: Camera → JSON → Mapeo
- [ ] MainActivity captura frame
- [ ] Genera JSON (control_schema.json)
- [ ] Envía a JNI → native-lib.cpp
- [ ] Mapeos calculan parámetros
- [ ] Parámetros enviados a LibPD

### 3.5 Testing Semana 3
- [ ] Permisos solicitados en runtime
- [ ] Cámara abre sin crash
- [ ] Frames capturados @ 30 fps
- [ ] JSON generado correctamente (verificar logs)
- [ ] Parámetros mapeados (verificar logs)
- [ ] Audio cambia con luz de la escena

**MILESTONE:** Visión integrada ✅

---

## 📋 SEMANA 4: Mapeos Completos + Testing

### 4.1 Completar 6 mapeos en C++
- [ ] elevation_to_freq() ✓ (ya existe)
- [ ] distance_gain() ✓ (ya existe)
- [ ] distance_lpf_cutoff() ✓ (ya existe)
- [ ] luminance_to_binaural_beat() ✓ (ya existe)
- [ ] material_to_synthesis() (NUEVO)
  - [ ] Mapeo material string → parámetros síntesis
  - [ ] Metal → FM synthesis
  - [ ] Wood → Aditiva
  - [ ] Stone → Granular
- [ ] confidence_to_mixure() (NUEVO)
  - [ ] Confianza → fade/amplitud

### 4.2 Crear mapping_engine.cpp
- [ ] Clase `MappingEngine`
- [ ] Métodos para cada mapeo
- [ ] Validación de rangos
- [ ] Documentación inline

### 4.3 Integración en native-lib.cpp
- [ ] Llamar mapeos desde JSON parser
- [ ] Generar lista de parámetros
- [ ] Enviar a LibPD via pdSendFloat()

### 4.4 Testing & Benchmarking
- [ ] Latencia: cámara → audio (<100ms)
- [ ] CPU usage (<15%)
- [ ] Memory footprint (<100MB)
- [ ] Estabilidad: 30 min de funcionamiento sin crash

### 4.5 Calibración Inicial
- [ ] Ajustar constantes de mapeo
- [ ] Validar audio range (no clipping)
- [ ] Verificar binaural beat safety (5-12 Hz)
- [ ] Nota: documentar en config_default.json

### 4.6 Bug Fixes & Polishing
- [ ] Resolver cualquier error de compilación
- [ ] Limpiar logs de debug
- [ ] Documentar decisiones de diseño

**MILESTONE:** MVP Fase 1 Completo ✅

---

## 🔧 ARCHIVOS A CREAR/MODIFICAR

### Nuevos archivos:
```
app/src/main/
├── cpp/
│   ├── libpd_wrapper.cpp          (NEW)
│   ├── libpd_wrapper.h             (NEW)
│   ├── audio_engine.cpp            (NEW)
│   ├── audio_engine.h              (NEW)
│   ├── mapping_engine.cpp          (NEW)
│   ├── mapping_engine.h            (NEW)
│   ├── json_parser.cpp             (NEW)
│   ├── json_parser.h               (NEW)
│   └── libpd/
│       └── include/                (NEW - headers descargados)
│           ├── libpd.h
│           ├── pd.h
│           └── m_pd.h
├── jniLibs/
│   └── arm64-v8a/
│       └── libpd.so                (NEW - binario descargado)
└── assets/
    ├── config_default.json         (NEW)
    └── patches/
        └── light_material_patch.pd (EXISTING - copiar si no existe)
```

### Archivos a modificar:
```
app/
├── CMakeLists.txt                  (UPDATE - libpd linking)
├── build.gradle                    (UPDATE - NDK, C++ std)
├── src/main/
│   ├── java/com/visualonda/sensory/
│   │   └── MainActivity.kt          (UPDATE - Camera + Audio)
│   └── AndroidManifest.xml         (VERIFY - permisos)
```

---

## 📊 DEPENDENCIAS EXTERNAS

### Gradle (Kotlin/Android)
```gradle
implementation 'androidx.camera:camera-core:1.2.3'
implementation 'androidx.camera:camera-camera2:1.2.3'
implementation 'androidx.camera:camera-lifecycle:1.2.3'
```

### NDK/CMake
- Android NDK r23c o más nuevo
- CMake 3.10.2+

### Librerías nativas
- libpd (prebuilt ARM64)
- AAudio (incluido en Android 8.0+)

---

## ✅ CRITERIOS DE ACEPTACIÓN (Fase 1 DONE)

1. **Audio funcional**: Auriculares reproducen sonido @ 44.1kHz
2. **Cámara viva**: ImageAnalysis captura @ 30fps sin lag
3. **Mapeos**: Parámetros calculados correctamente (verificar logs)
4. **Latencia**: <100ms cámara → audio
5. **Compilación**: Cero errores, zero warnings importantes
6. **Estabilidad**: 30+ min sin crash
7. **Documentación**: Código comentado, README actualizado

---

## 🐛 COMMON ISSUES & SOLUTIONS

### Problema: CMake error "libpd.so not found"
**Solución:** Verificar ruta exacta en CMakeLists.txt:
```cmake
message(STATUS "Looking for libpd at: ${LIBPD_PATH}/../../../jniLibs/${ANDROID_ABI}")
```

### Problema: AAudio callback no se ejecuta
**Solución:** Verificar que `requestStart()` retorna `AAUDIO_OK`

### Problema: MainActivity compila pero camera no abre
**Solución:** Verificar permisos en runtime (minSdk 24+)

### Problema: Logcat lleno de warnings irrelevantes
**Solución:** Agregar filtro: `-v brief AndroidRuntime`

