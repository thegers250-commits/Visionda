# ✅ FASE 0: SETUP CHECKLIST - EMPEZAR AHORA

**Duration:** 3-5 days (parallel to documentation review)
**Objetivo:** Proyecto compilable con libpd listo
**Team:** Tech Lead + 1 Android Engineer

---

## 🎯 META FASE 0

```
ANTES:  Project doesn't compile (stubs, missing libpd.so)
DESPUÉS: Project compiles, libpd.so in place, ready Week 1
```

---

## 📋 CHECKLIST FASE 0

### DÍA 1: Descargar & Validar LibPD

#### [ ] Tarea 0.1.1: Descargar LibPD

```bash
# 1. Ir a:
https://github.com/libpd/libpd/releases

# 2. Descargar la versión más reciente para Android (arm64)
# Buscar: "libpd-X.XX-X-android.zip" (arm64-v8a)
# Ejemplo: libpd-0.12.1-android.zip

# 3. Guardar en carpeta temporal
C:\Temp\libpd-android.zip

# 4. Extraer
unzip libpd-android.zip
# Resultado: libpd-0.12/
```

**✅ Éxito:** Tienes carpeta libpd-0.12 con contenido

---

#### [ ] Tarea 0.1.2: Verificar Contenido LibPD

```bash
# En carpeta libpd-0.12, deberías ver:

android/
├── arm64-v8a/
│   ├── libpd.so         ← NECESARIO (binario)
│   └── [otros archivos]
├── armeabi-v7a/         (opcional)
└── x86_64/              (opcional)

pure-data/
└── src/
    ├── libpd.h          ← NECESARIO (header)
    ├── pd.h             ← NECESARIO (header)
    ├── m_pd.h           ← NECESARIO (header)
    └── [otros]

# Verificar en Windows (cmd o PowerShell):
dir libpd-0.12\android\arm64-v8a\libpd.so
dir libpd-0.12\pure-data\src\libpd.h

# Esperado: Archivos existen
```

**✅ Éxito:** Encontraste libpd.so + headers

---

#### [ ] Tarea 0.1.3: Copiar Binarios a Proyecto

```bash
# 1. Abrir PowerShell como Admin (Windows)

# 2. Navegar a proyecto:
cd "f:\Programas de  github\Visualonda\android_skeleton"

# 3. Crear directorio para JNI libs:
mkdir -p app\src\main\jniLibs\arm64-v8a

# 4. Copiar libpd.so
copy "C:\Temp\libpd-0.12\android\arm64-v8a\libpd.so" `
      "app\src\main\jniLibs\arm64-v8a\libpd.so"

# 5. Verificar copia exitosa:
dir app\src\main\jniLibs\arm64-v8a\libpd.so

# Esperado: libpd.so (~1.2 MB)
```

**✅ Éxito:** `libpd.so` está en `android_skeleton/app/src/main/jniLibs/arm64-v8a/`

---

#### [ ] Tarea 0.1.4: Copiar Headers a Proyecto

```bash
# 1. Crear directorio para headers:
mkdir -p app\src\main\cpp\libpd\include

# 2. Copiar 3 headers principales:
copy "C:\Temp\libpd-0.12\pure-data\src\libpd.h" `
      "app\src\main\cpp\libpd\include\libpd.h"

copy "C:\Temp\libpd-0.12\pure-data\src\pd.h" `
      "app\src\main\cpp\libpd\include\pd.h"

copy "C:\Temp\libpd-0.12\pure-data\src\m_pd.h" `
      "app\src\main\cpp\libpd\include\m_pd.h"

# 3. Verificar:
dir app\src\main\cpp\libpd\include\*.h

# Esperado: 3 archivos .h
```

**✅ Éxito:** Headers están en `android_skeleton/app/src/main/cpp/libpd/include/`

---

#### [ ] Tarea 0.1.5: Copiar Pure Data Patch

```bash
# 1. Crear directorio assets:
mkdir -p app\src\main\assets\patches

# 2. Copiar patch:
copy "sensory-language\light_material_patch.pd" `
      "app\src\main\assets\patches\light_material_patch.pd"

# 3. Verificar:
dir app\src\main\assets\patches\*.pd

# Esperado: light_material_patch.pd presente
```

**✅ Éxito:** Patch está en `android_skeleton/app/src/main/assets/patches/`

---

### DÍA 2: Actualizar Build Configuration

#### [ ] Tarea 0.2.1: Verificar CMakeLists.txt Actual

```bash
# 1. Leer CMakeLists.txt actual:
cat app\CMakeLists.txt

# Debería mostrar algo como:
# cmake_minimum_required(VERSION 3.22.1)
# project("native_lib")
# add_library(native-lib SHARED native-lib.cpp)

# 2. Notar qué está FALTANDO:
# - include_directories para libpd
# - find_library para aaudio
# - target_link_libraries
```

**✅ Éxito:** Entiendes estado actual de CMakeLists.txt

---

#### [ ] Tarea 0.2.2: Crear CMakeLists.txt ACTUALIZADO

Crear/reemplazar: `f:\Programas de  github\Visualonda\android_skeleton\app\CMakeLists.txt`

```cmake
# FILE: app/CMakeLists.txt
cmake_minimum_required(VERSION 3.22.1)
project("native_lib")

# ============================================
# LibPD Configuration
# ============================================
set(LIBPD_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/libpd/include")
include_directories(${LIBPD_INCLUDE_DIR})

# ============================================
# Source Files
# ============================================
add_library(native-lib SHARED
    src/main/cpp/native-lib.cpp
)

# ============================================
# Link LibPD
# ============================================
set(LIBPD_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}")

# Verify libpd.so exists
if(NOT EXISTS "${LIBPD_LIB_DIR}/libpd.so")
    message(FATAL_ERROR "libpd.so not found at: ${LIBPD_LIB_DIR}/libpd.so")
endif()

# Link pre-built libpd
add_library(libpd SHARED IMPORTED)
set_target_properties(libpd PROPERTIES
    IMPORTED_LOCATION "${LIBPD_LIB_DIR}/libpd.so"
)

# ============================================
# Link AAudio (Android native audio)
# ============================================
find_library(aaudio-lib aaudio)
if(NOT aaudio-lib)
    message(WARNING "AAudio not found, audio features may not work")
endif()

# ============================================
# Final Link
# ============================================
target_link_libraries(native-lib
    libpd
    ${aaudio-lib}
    log
)

# ============================================
# Compiler Flags
# ============================================
target_compile_options(native-lib PRIVATE
    -Wall
    -Wextra
    -O3
)
```

**✅ Éxito:** CMakeLists.txt actualizado con libpd config

---

#### [ ] Tarea 0.2.3: Actualizar build.gradle (App-level)

Abrir: `f:\Programas de  github\Visualonda\android_skeleton\app\build.gradle`

Buscar sección `defaultConfig { }` y AGREGAR:

```gradle
defaultConfig {
    applicationId "com.visualonda.sensory"
    minSdk 28              // Android 9
    targetSdk 34           // Android 14 (latest)
    versionCode 1
    versionName "0.1.0-alpha"

    // ADD THESE LINES:
    ndk {
        abiFilters 'arm64-v8a'   // Primary target
        // Optional:
        // abiFilters 'arm64-v8a', 'armeabi-v7a'
    }

    externalNativeBuild {
        cmake {
            cppFlags "-fexceptions -frtti"
            cFlags "-fexceptions"
        }
    }
}
```

Buscar sección `externalNativeBuild { }` y VERIFICAR/AGREGAR:

```gradle
externalNativeBuild {
    cmake {
        path "CMakeLists.txt"
        version "3.22.1"
    }
}
```

Buscar sección `dependencies { }` y AGREGAR:

```gradle
dependencies {
    // Existing dependencies...
    
    // ADD THESE:
    // CameraX for camera capture
    implementation 'androidx.camera:camera-core:1.2.3'
    implementation 'androidx.camera:camera-camera2:1.2.3'
    implementation 'androidx.camera:camera-lifecycle:1.2.3'
    
    // UI
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Logging
    implementation 'androidx.appcompat:appcompat:1.6.1'
}
```

**✅ Éxito:** build.gradle actualizado con NDK + CameraX

---

### DÍA 2-3: Verificar Estructura de Directorios

#### [ ] Tarea 0.3.1: Verificar Estructura Completa

```bash
# Estructura esperada después de Día 1-2:
# (Verificar que TODO existe)

android_skeleton/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml       ✓
│   │   │   ├── assets/
│   │   │   │   └── patches/
│   │   │   │       └── light_material_patch.pd    ✓ NEW
│   │   │   ├── cpp/
│   │   │   │   ├── native-lib.cpp        ✓
│   │   │   │   └── libpd/
│   │   │   │       └── include/
│   │   │   │           ├── libpd.h       ✓ NEW
│   │   │   │           ├── pd.h          ✓ NEW
│   │   │   │           └── m_pd.h        ✓ NEW
│   │   │   ├── java/
│   │   │   │   └── com/visualonda/sensory/
│   │   │   │       └── MainActivity.kt   ✓
│   │   │   └── jniLibs/
│   │   │       └── arm64-v8a/
│   │   │           └── libpd.so          ✓ NEW (~1.2 MB)
│   ├── build.gradle                      ✓ UPDATED
│   └── CMakeLists.txt                    ✓ UPDATED
├── build.gradle                          ✓
├── settings.gradle                       ✓
└── CMakeLists.txt                        ✓ (root, optional)

# Verificar en PowerShell:
dir -Recurse -Path "app\src\main\jniLibs" -Filter "libpd.so"
# Esperado: Find 1 item

dir -Recurse -Path "app\src\main\cpp\libpd\include" -Filter "*.h"
# Esperado: Find 3 items

dir -Path "app\src\main\assets\patches" -Filter "*.pd"
# Esperado: Find 1 item
```

**✅ Éxito:** Estructura correcta, todos archivos en lugar

---

#### [ ] Tarea 0.3.2: Verificar build.gradle Raíz (Project-level)

Abrir: `f:\Programas de  github\Visualonda\android_skeleton\build.gradle`

Verificar que contenga (agregar si falta):

```gradle
plugins {
    id 'com.android.application' version '8.1.0' apply false
    id 'com.android.library' version '8.1.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.0' apply false
}

android {
    compileSdk 34
}
```

**✅ Éxito:** Root build.gradle compatible

---

### DÍA 3: Compilación Test

#### [ ] Tarea 0.4.1: Limpiar & Construir

```bash
# 1. Navegar a proyecto
cd f:\Programas\ de\ \ github\Visualonda\android_skeleton

# 2. Limpiar builds anteriores
./gradlew clean

# Esperado: BUILD SUCCESSFUL

# 3. Construir debug
./gradlew build

# Esto tardará 2-5 minutos la primera vez
```

**Posibles Errores & Soluciones:**

```bash
ERROR 1: "Cannot find libpd.so"
SOLUCIÓN: Verificar ruta exacta en CMakeLists.txt
./gradlew build --info | grep -i libpd

ERROR 2: "Headers not found"
SOLUCIÓN: Verificar include_directories en CMakeLists.txt
Comando: dir app\src\main\cpp\libpd\include\

ERROR 3: "NDK not found"
SOLUCIÓN: Instalar NDK 25.0+ en Android Studio
File → Settings → Appearance & Behavior → System Settings → Android SDK
→ SDK Tools → NDK (side by side) → Install
```

**✅ Éxito:** BUILD SUCCESSFUL (puede haber warnings, pero NO errores)

---

#### [ ] Tarea 0.4.2: Verificar Artefactos de Build

```bash
# Si build fue exitoso, deberías ver:

app/build/outputs/apk/debug/app-debug.apk (15-25 MB)

# Verificar:
dir app\build\outputs\apk\debug\

# Si existe → Éxito ✅
# Si no existe → Rerun ./gradlew build
```

**✅ Éxito:** APK generado sin errores

---

#### [ ] Tarea 0.4.3: Conectar Dispositivo & Instalar

```bash
# 1. Conectar teléfono Android via USB
# 2. Habilitar "Developer Mode" en teléfono:
#    Settings → About → Build Number (tap 7x)
#    Settings → Developer Options → USB Debugging → ON

# 3. En PC, verificar conexión:
./gradlew devices

# Esperado: Listar tu dispositivo
# Ej: emulator-5554 offline/online

# 4. Instalar APK:
./gradlew installDebug

# Esperado: Installing APK 'app-debug.apk' on 'emulator-5554'
#          Installed 'app-debug.apk'
#          BUILD SUCCESSFUL
```

**✅ Éxito:** App instalada en dispositivo

---

#### [ ] Tarea 0.4.4: Verificar App en Dispositivo

```bash
# En el teléfono:
# 1. Buscar app "Visualonda" o "sensory"
# 2. Tap para abrir
# 3. Permitir permisos (camera, microphone) cuando pregunte

# En PC, revisar logs:
./gradlew logcat

# O con adb directamente:
adb logcat | grep -i "visualonda\|native"

# Esperado: Ver logs de la app
# Ej: [native] LibPD initialized
```

**✅ Éxito:** App corre, no tiene crash inmediato

---

#### [ ] Tarea 0.4.5: Primera Prueba Manual

```bash
# En app:
# 1. Tap botón "Init Audio" (si existe)
#    → Logcat debería mostrar: "[JNI] Audio engine initialized"
#
# 2. Conectar auriculares
#    → Debería escuchar tono (sin silencio)
#
# 3. Si no hay botones, solo verifica en logcat:
#    adb logcat | grep native
#    → Debería haber líneas sin ERROR

# Si ves esto → Fase 0 EXITOSA ✅
```

**✅ Éxito:** No hay crashes, logcat muestra actividad

---

### DÍA 3-4: Troubleshooting & Validation

#### [ ] Tarea 0.5.1: Validar Compilación

```bash
# Verificar ausencia de errores críticos
./gradlew build --stacktrace

# Debería terminar con:
# BUILD SUCCESSFUL in Xs
# (warnings están OK, errores NO)
```

**✅ Éxito:** BUILD SUCCESSFUL

---

#### [ ] Tarea 0.5.2: Validar Linker

```bash
# Si hay linker errors, comando para debugging:
./gradlew :app:assembleDebug -Pandroid.enableCMakeLogging=true

# Buscar en output:
# - "libpd.so" → debe ser encontrado
# - "aaudio" → debe ser encontrado
# - "-llog" → debe estar presente
```

**✅ Éxito:** No hay undefined references

---

#### [ ] Tarea 0.5.3: Validar JNI

```bash
# Verificar que JNI carga correctamente:
adb logcat | grep "java.lang.UnsatisfiedLinkError"

# Si ves ese error:
# 1. Verificar libpd.so es ARM64:
#    file app/src/main/jniLibs/arm64-v8a/libpd.so
#    → Debería mostrar: ELF 64-bit

# 2. Verificar ABIs coinciden:
#    grep "abiFilters" app/build.gradle
#    → Debe ser: arm64-v8a

# 3. Recompilar si cambió CMakeLists.txt:
#    ./gradlew clean build
```

**✅ Éxito:** No hay UnsatisfiedLinkError

---

#### [ ] Tarea 0.5.4: Validar Assets

```bash
# Verificar patch.pd está en APK:
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep "\.pd$"

# Esperado: assets/patches/light_material_patch.pd

# Si no ve:
# - Verificar: app/src/main/assets/patches/light_material_patch.pd existe
# - Si no existe, copiar desde: sensory-language/light_material_patch.pd
```

**✅ Éxito:** Patch en APK

---

### DÍA 4-5: Final Validation & Documentation

#### [ ] Tarea 0.6.1: Crear Build Summary

```bash
# Ejecutar y guardar output:
./gradlew build > build_summary.txt 2>&1

# Revisar build_summary.txt:
type build_summary.txt | grep -i "BUILD\|error\|warning"

# Guardar como evidencia de Fase 0 completada
```

**✅ Éxito:** Build summary generado sin errores

---

#### [ ] Tarea 0.6.2: Documentar Versiones

Crear archivo: `FASE_0_VERSIONS.txt`

```txt
FASE 0 COMPLETION - Version Documentation
==========================================

Date Completed: [TODAY'S DATE]
Team: [NAMES]

LibPD Version:
- URL: https://github.com/libpd/libpd/releases
- Version: [E.g., 0.12.1]
- File: libpd-0.12.1-android.zip
- Binary Size: 1.2 MB
- Location: app/src/main/jniLibs/arm64-v8a/libpd.so

Android SDK:
- Target SDK: 34 (Android 14)
- Min SDK: 28 (Android 9)
- NDK Version: 25.0.8221429+
- Gradle: 8.1.0+

Build Result:
- Status: ✅ SUCCESSFUL
- APK Size: [X MB]
- Output: app/build/outputs/apk/debug/app-debug.apk

Testing:
- Device/Emulator: [MODEL]
- Android Version: [VERSION]
- Installation: ✅ Success
- No Crashes: ✅ Yes (initial test)

Notes:
- Project compiles without errors
- libpd.so loads successfully
- Ready for Fase 1
```

**✅ Éxito:** Documentación de Fase 0 guardada

---

#### [ ] Tarea 0.6.3: Checklist Final

```markdown
# FASE 0 COMPLETION CHECKLIST

## Directory Structure
- [x] app/src/main/jniLibs/arm64-v8a/libpd.so exists
- [x] app/src/main/cpp/libpd/include/*.h (3 headers)
- [x] app/src/main/assets/patches/light_material_patch.pd
- [x] CMakeLists.txt updated with libpd config
- [x] build.gradle (app-level) updated
- [x] build.gradle (root-level) compatible

## Compilation
- [x] ./gradlew clean succeeds
- [x] ./gradlew build succeeds (BUILD SUCCESSFUL)
- [x] No errors (warnings OK)
- [x] APK generated successfully

## Installation & Runtime
- [x] APK installs on device/emulator
- [x] App starts without crash
- [x] No "UnsatisfiedLinkError"
- [x] Logcat shows normal activity

## Documentation
- [x] FASE_0_VERSIONS.txt created
- [x] Build summary saved
- [x] Notes taken for Fase 1

## Gate Criteria (ALL MUST PASS)
- [x] Project compiles: YES
- [x] libpd.so in correct location: YES
- [x] Headers found: YES
- [x] APK installs: YES
- [x] No crashes on startup: YES
- [x] Ready for Fase 1: YES

✅ FASE 0 COMPLETE - READY FOR FASE 1
```

**✅ Éxito:** Todo verificado y documentado

---

## 🎯 GATE CRITERIA FASE 0

**TODOS estos deben ser ✅:**

```
✅ Proyecto compila: ./gradlew build → BUILD SUCCESSFUL
✅ libpd.so presente: app/src/main/jniLibs/arm64-v8a/libpd.so (~1.2 MB)
✅ Headers presentes: 3 archivos en app/src/main/cpp/libpd/include/
✅ CMakeLists.txt actualizado: include_directories + target_link_libraries
✅ build.gradle actualizado: ndk.abiFilters + CameraX deps
✅ APK genera: app/build/outputs/apk/debug/app-debug.apk
✅ APK instala: sin errores en dispositivo/emulador
✅ App corre: sin crashes inmediatos
✅ Logcat limpio: sin "UnsatisfiedLinkError" o "cannot load"
✅ Documentación: FASE_0_VERSIONS.txt creado
```

**Si ALGUNO está ❌ → RESOLVER antes de proceder a Fase 1**

---

## ⚠️ TROUBLESHOOTING RÁPIDO

| Problema | Solución | Comando |
|----------|----------|---------|
| libpd.so no encontrado | Verificar ruta CMakeLists.txt | `dir app\src\main\jniLibs\arm64-v8a\libpd.so` |
| Headers missing | Copiar de nuevo desde libpd-0.12 | `copy C:\Temp\...` |
| NDK no encontrado | Instalar NDK 25+ en Android Studio | Android Studio → SDK Tools |
| Linker error | Verificar CMakeLists.txt target_link_libraries | Reread CMakeLists.txt |
| APK muy grande | Normal (~20MB debug) | Será más pequeño en release |
| App crashes en startup | Revisar logcat | `adb logcat \| grep native` |
| UnsatisfiedLinkError | libpd.so no es ARM64 | `file libpd.so` |

---

## 📞 CONTACTO & SOPORTE

Si tienes problemas en Fase 0:

1. **Google** el error exacto
2. **StackOverflow** search Android + libpd + NDK
3. **GitHub Issues**: libpd repository
4. **This team**: tech-lead@visualonda.dev

---

## ✅ SIGUIENTE PASO

Una vez completada Fase 0:

```
1. Commit cambios a git:
   git add -A
   git commit -m "Fase 0: libpd setup + CMakeLists.txt + build.gradle"
   git push origin main

2. Documentar en PROJECT_STATUS.md:
   - Fase 0: ✅ COMPLETE (date)
   - Fase 1: ⏳ READY TO START

3. Schedule: Fase 1 kickoff (Lunes)
   - Enviar FASE_1_IMPLEMENTATION_PLAN.md a team
   - Schedule daily standup
   - Assign: libpd_wrapper.cpp task

4. Monday: Begin Fase 1 Week 1
```

---

**Documento:** FASE_0_SETUP_CHECKLIST.md
**Versión:** 1.0
**Estado:** READY FOR EXECUTION
**Duración Estimada:** 3-5 días
**Team:** Tech Lead + 1 Android Engineer

**EMPEZAR AHORA** ✅

