# 📅 SEMANA 1 - PLAN DETALLADO

**Fase 1, Week 1: LibPD Integration + Foundation**  
**Duración:** 5 días de trabajo (Lunes-Viernes)  
**Objetivo:** LibPD inicializa, carga patch, app NO crashea

---

## 🎯 OBJETIVO SEMANA 1

```
ENTRADA:  Código base + stubs
PROCESO:  Integrar LibPD
SALIDA:   LibPD funcional, app abre, botones responden
```

---

## 📊 TIMELINE DIARIO

### LUNES (Día 1) - Setup LibPD
**Duración:** 6-8 horas  
**Objetivo:** Descargar + configurar LibPD

#### Tarea 1.1: Descargar LibPD (1 hora)
```
1. Ir a: https://github.com/libpd/libpd/releases
2. Buscar: "libpd-0.12.x-android"
3. Descargar: libpd-0.12.x-android.zip
4. Extraer a temp folder
   ├─ libpd-0.12/pure-data/src/{libpd.h, pd.h, m_pd.h}
   └─ libpd-0.12/android/arm64-v8a/libpd.so
```

**Verificación:**
```powershell
# Después de extraer:
ls libpd-0.12/pure-data/src/
# Debe mostrar: libpd.h, pd.h, m_pd.h (3 archivos)

ls libpd-0.12/android/arm64-v8a/
# Debe mostrar: libpd.so (~1.2 MB)
```

#### Tarea 1.2: Copiar a Proyecto (1 hora)
```powershell
# En PowerShell, desde Visualonda root:

# Crear directorios
mkdir -p android_skeleton/app/src/main/cpp/libpd/include
mkdir -p android_skeleton/app/src/main/jniLibs/arm64-v8a

# Copiar headers
cp libpd-0.12/pure-data/src/{libpd.h,pd.h,m_pd.h} `
   android_skeleton/app/src/main/cpp/libpd/include/

# Copiar binaria
cp libpd-0.12/android/arm64-v8a/libpd.so `
   android_skeleton/app/src/main/jniLibs/arm64-v8a/

# Verificar
ls android_skeleton/app/src/main/cpp/libpd/include/
# Debe mostrar: libpd.h, pd.h, m_pd.h

ls android_skeleton/app/src/main/jniLibs/arm64-v8a/
# Debe mostrar: libpd.so
```

**Verificación Visual:**
```
android_skeleton/
└── app/src/main/
    ├── cpp/libpd/include/
    │   ├── libpd.h          ✅
    │   ├── pd.h             ✅
    │   └── m_pd.h           ✅
    └── jniLibs/arm64-v8a/
        └── libpd.so         ✅ (~1.2 MB)
```

#### Tarea 1.3: Crear Pure Data Patch (2 horas)
**Archivo:** `android_skeleton/app/src/main/assets/patches/light_material_patch.pd`

```
#N canvas 0 0 800 600 12;
#X obj 50 50 inlet~;
#X obj 50 100 *~ 0.1;
#X obj 50 150 outlet~;
#X connect 0 0 1 0;
#X connect 1 0 2 0;
```

**Qué hace:** Multiplica audio por 0.1 (volume control)

**Verificación:**
```powershell
ls android_skeleton/app/src/main/assets/patches/
# Debe mostrar: light_material_patch.pd
```

#### Tarea 1.4: Actualizar CMakeLists.txt (1 hora)
**Archivo:** `android_skeleton/app/src/main/cpp/CMakeLists.txt`

Agregar estas líneas después de `cmake_minimum_required`:

```cmake
# LibPD configuration
set(LIBPD_PATH "${CMAKE_CURRENT_SOURCE_DIR}/libpd")
include_directories(${LIBPD_PATH}/include)

# Find all sources
file(GLOB_RECURSE LIBPD_SRCS 
    "${CMAKE_CURRENT_SOURCE_DIR}/*.cpp"
)

# Link libraries
target_link_libraries(native-lib 
    PUBLIC
    ${LIBPD_PATH}/../../jniLibs/${ANDROID_ABI}/libpd.so
    aaudio
)
```

**Testing:**
```powershell
# Verificar sintaxis
# (Se verifica cuando compilas en GitHub Actions)
```

#### 🎯 FIN LUNES
```
✅ LibPD descargado
✅ Headers en lugar correcto
✅ libpd.so en jniLibs
✅ Patch creado
✅ CMakeLists.txt actualizado
```

---

### MARTES (Día 2) - Implementar libpd_wrapper
**Duración:** 6-8 horas  
**Objetivo:** Crear libpd_wrapper.cpp funcional

#### Tarea 2.1: Crear libpd_wrapper.h (1 hora)
**Archivo:** `android_skeleton/app/src/main/cpp/libpd_wrapper.h`

```cpp
#ifndef LIBPD_WRAPPER_H
#define LIBPD_WRAPPER_H

#ifdef __cplusplus
extern "C" {
#endif

// Inicializar LibPD
bool libpd_wrapper_init();

// Cargar patch (ruta debe ser accessible desde assets)
bool libpd_wrapper_load_patch(const char* patch_path);

// Enviar valores flotantes a receptores
bool libpd_wrapper_send_float(const char* receiver, float value);

// Procesar audio (tick - LlamadoRegular)
bool libpd_wrapper_process_tick();

// Cleanup
void libpd_wrapper_cleanup();

// Get/Set estado
bool libpd_wrapper_is_initialized();
int libpd_wrapper_get_sample_rate();

#ifdef __cplusplus
}
#endif

#endif // LIBPD_WRAPPER_H
```

#### Tarea 2.2: Crear libpd_wrapper.cpp (3 horas)
**Archivo:** `android_skeleton/app/src/main/cpp/libpd_wrapper.cpp`

Ver siguiente documento para código completo (es 180 líneas).

#### Tarea 2.3: Testing (2 horas)
```
- Compilación sin errores
- No hay warnings sobre libpd.h
- Link sin missing symbol
```

#### 🎯 FIN MARTES
```
✅ libpd_wrapper.h creado
✅ libpd_wrapper.cpp creado
✅ Compilación exitosa
✅ Sin linker errors
```

---

### MIÉRCOLES (Día 3) - Implementar native-lib JNI
**Duración:** 6-8 horas  
**Objetivo:** JNI functions para Java ↔ C++

#### Tarea 3.1: Crear funciones JNI (3 horas)
**Modificar:** `android_skeleton/app/src/main/cpp/native-lib.cpp`

Agregar después de los includes:

```cpp
#include "libpd_wrapper.h"
#include <android/log.h>

#define LOG_TAG "VisualondaNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// JNI Function: pdInit
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdInit(JNIEnv* env, jobject) {
    LOGI("[JNI] pdInit() called");
    if (libpd_wrapper_init()) {
        LOGI("[JNI] LibPD initialized successfully");
    } else {
        LOGE("[JNI] Failed to initialize LibPD");
    }
}

// JNI Function: pdOpenPatch
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdOpenPatch(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    LOGI("[JNI] pdOpenPatch(%s) called", path);
    
    if (libpd_wrapper_load_patch(path)) {
        LOGI("[JNI] Patch loaded successfully");
    } else {
        LOGE("[JNI] Failed to load patch");
    }
    
    env->ReleaseStringUTFChars(jpath, path);
}

// JNI Function: pdSendFloat
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdSendFloat(JNIEnv* env, jobject, jstring jreceiver, jfloat value) {
    const char* receiver = env->GetStringUTFChars(jreceiver, nullptr);
    LOGI("[JNI] pdSendFloat(%s, %.3f)", receiver, value);
    
    libpd_wrapper_send_float(receiver, (float)value);
    
    env->ReleaseStringUTFChars(jreceiver, receiver);
}

// JNI Function: pdCleanup
extern "C" JNIEXPORT void JNICALL
Java_com_visualonda_sensory_MainActivity_pdCleanup(JNIEnv* env, jobject) {
    LOGI("[JNI] pdCleanup() called");
    libpd_wrapper_cleanup();
}
```

#### Tarea 3.2: Testing JNI (2 horas)
```
- Compilación sin undefined reference
- No hay warnings sobre JNI
- Funciones exportadas correctamente
```

#### 🎯 FIN MIÉRCOLES
```
✅ JNI functions implementadas
✅ MainActivity puede llamar C++
✅ Compilación sin errors
```

---

### JUEVES (Día 4) - Actualizar MainActivity.kt
**Duración:** 4-6 horas  
**Objetivo:** UI funcional con 3 botones

#### Tarea 4.1: Crear Layout (2 horas)
**Archivo:** `android_skeleton/app/src/main/res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="20dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="VISUALONDA"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="30dp" />

    <Button
        android:id="@+id/btnInitAudio"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Init PD"
        android:layout_marginBottom="10dp" />

    <Button
        android:id="@+id/btnLoadPatch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Load Patch"
        android:layout_marginBottom="10dp" />

    <Button
        android:id="@+id/btnSendFloat"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Send Float (1000 Hz)"
        android:layout_marginBottom="10dp" />

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:text="Status: Ready\n"
        android:textSize="12sp"
        android:scrollbars="vertical"
        android:gravity="top|left"
        android:background="#f0f0f0"
        android:padding="10dp" />

</LinearLayout>
```

#### Tarea 4.2: Actualizar MainActivity.kt (2 horas)
**Modificar:** `android_skeleton/app/src/main/java/com/visualonda/sensory/MainActivity.kt`

```kotlin
package com.visualonda.sensory

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {
    
    private val TAG = "VisualondaApp"
    private lateinit var tvStatus: TextView
    
    // JNI external functions
    external fun pdInit()
    external fun pdOpenPatch(path: String)
    external fun pdSendFloat(name: String, value: Float)
    external fun pdCleanup()
    
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        tvStatus = findViewById(R.id.tvStatus)
        
        val btnInit = findViewById<Button>(R.id.btnInitAudio)
        val btnLoad = findViewById<Button>(R.id.btnLoadPatch)
        val btnSend = findViewById<Button>(R.id.btnSendFloat)
        
        btnInit.setOnClickListener {
            log("Calling pdInit()...")
            pdInit()
            log("✅ pdInit() completed")
        }
        
        btnLoad.setOnClickListener {
            log("Calling pdOpenPatch()...")
            val patchPath = filesDir.absolutePath + "/light_material_patch.pd"
            pdOpenPatch(patchPath)
            log("✅ Patch loaded")
        }
        
        btnSend.setOnClickListener {
            log("Sending float 1000.0...")
            pdSendFloat("light-freq", 1000.0f)
            log("✅ Float sent")
        }
        
        log("MainActivity ready")
    }
    
    private fun log(msg: String) {
        Log.i(TAG, msg)
        tvStatus.append(msg + "\n")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        pdCleanup()
    }
}
```

#### 🎯 FIN JUEVES
```
✅ Layout XML creado
✅ MainActivity actualizado
✅ 3 botones funcionales
✅ Logging completo
```

---

### VIERNES (Día 5) - Testing y Gate
**Duración:** 3-4 horas  
**Objetivo:** Verificar Semana 1 completada

#### Tarea 5.1: Compilar (1 hora)
```powershell
cd "f:\Programas de  github\Visualonda\android_skeleton"
# GitHub Actions compila automáticamente en push
git add .
git commit -m "Week 1: LibPD integration - wrapper, JNI, UI"
git push
# Esperar ~15 minutos a que GitHub Actions compile
```

#### Tarea 5.2: Descargar + Instalar (1 hora)
```powershell
# Descargar APK de artifacts
# Instalar: adb install app-debug.apk
```

#### Tarea 5.3: Testing (1 hora)
```
☐ App abre sin crashes
☐ Botón "Init PD" → Logcat muestra "pdInit() completed"
☐ Botón "Load Patch" → Logcat muestra "Patch loaded"
☐ Botón "Send Float" → Logcat muestra "Float sent"
☐ 0 crashes totales
✅ SEMANA 1 COMPLETADA
```

#### 🎯 FIN VIERNES (Gate)
```
✅ GATE WEEK 1:
   ├─ Compilación: EXITOSA
   ├─ App abre: ✅
   ├─ 3 botones: ✅ Funcionales
   ├─ JNI calls: ✅ Exitosas
   ├─ Crashes: 0
   └─ APROBADO PARA WEEK 2
```

---

## 📊 RESUMEN SEMANA 1

```
LUNES:       ✅ Setup LibPD + headers
MARTES:      ✅ libpd_wrapper.cpp
MIÉRCOLES:   ✅ JNI functions
JUEVES:      ✅ UI + MainActivity
VIERNES:     ✅ Testing + Gate

RESULTADO: LibPD funcional, app lista para Week 2
```

---

## 🎯 GATE CRITERIA

**Para PROCEDER a Semana 2:**

```
☐ Compilación: 0 errores críticos
☐ App instala: ✅
☐ App abre: ✅ Sin crash
☐ pdInit(): ✅ Responde
☐ pdOpenPatch(): ✅ Carga
☐ pdSendFloat(): ✅ Envía
☐ Logcat: ✅ Limpio
☐ Testing: 1 hora, 0 crashes

SI ✅ TODOS: PROCEDE WEEK 2
```

---

## 📚 ARCHIVOS A CREAR/MODIFICAR

```
CREAR:
├── android_skeleton/app/src/main/cpp/libpd_wrapper.h
├── android_skeleton/app/src/main/cpp/libpd_wrapper.cpp
├── android_skeleton/app/src/main/assets/patches/light_material_patch.pd
└── android_skeleton/app/src/main/res/layout/activity_main.xml

MODIFICAR:
├── android_skeleton/app/src/main/cpp/CMakeLists.txt
├── android_skeleton/app/src/main/cpp/native-lib.cpp
└── android_skeleton/app/src/main/java/.../MainActivity.kt

DESCARGAR:
└── libpd-0.12.x-android.zip → extract headers + .so
```

---

## ✅ CONCLUSIÓN

**Semana 1 es SETUP.**

En 5 días:
1. Integras LibPD
2. Creas wrapper
3. Conectas JNI
4. Haces UI
5. Verificas todo funciona

**Próxima:** Semana 2 - Audio Engine (AAudio)

