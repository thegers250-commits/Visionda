# VISUALONDA: Android Application

> Plataforma de accesibilidad que traduce información visual en sonificación espacial 3D para personas ciegas.

## 🚀 Quick Start

```bash
# 1. Clonar repo
git clone https://github.com/visualonda/visualonda.git
cd visualonda/android_skeleton

# 2. Abrir en Android Studio
# File > Open > seleccionar esta carpeta

# 3. Sincronizar Gradle
# Build > Clean Project > Rebuild Project

# 4. Compilar
# Build > Build Bundle(s) / APK(s)

# 5. Ejecutar
# Run > Run 'app'
```

## 📚 DOCUMENTACIÓN COMPLETA

Antes de empezar, lee:

1. **[RESUMEN_EJECUTIVO.md](../RESUMEN_EJECUTIVO.md)** — Visión de producto completo
2. **[VISION_Y_ROADMAP.md](../VISION_Y_ROADMAP.md)** — Roadmap 18 semanas, arquitectura
3. **[FASE_1_IMPLEMENTATION_PLAN.md](../FASE_1_IMPLEMENTATION_PLAN.md)** — Plan técnico detallado
4. **[CHECKLIST_FASE_1.md](../CHECKLIST_FASE_1.md)** — Checklist ejecutable
5. **[ESTRATEGIA_EQUIPO.md](../ESTRATEGIA_EQUIPO.md)** — Team structure, presupuesto, riesgos

## 🏗️ ESTRUCTURA DEL PROYECTO

```
android_skeleton/
├── app/
│   ├── src/main/
│   │   ├── java/com/visualonda/sensory/
│   │   │   └── MainActivity.kt           # Launcher activity
│   │   ├── cpp/
│   │   │   ├── native-lib.cpp            # JNI main
│   │   │   ├── mapping_engine.cpp        # 6 mapeos matemáticos
│   │   │   ├── audio_engine.cpp          # AAudio callbacks
│   │   │   └── libpd_wrapper.cpp         # LibPD bindings (TBD)
│   │   ├── jniLibs/
│   │   │   └── arm64-v8a/
│   │   │       └── libpd.so              # Librería LibPD (TBD)
│   │   ├── assets/
│   │   │   └── patches/
│   │   │       └── light_material_patch.pd
│   │   └── AndroidManifest.xml
│   ├── CMakeLists.txt
│   └── build.gradle
├── build.gradle
└── settings.gradle
```

## ⚙️ REQUISITOS

- **Android Studio**: Dolphin 2021.3.1 o más nuevo
- **Android NDK**: r23c o más nuevo
- **CMake**: 3.10.2+
- **Gradle**: 7.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 33 (Android 13)

## 🎯 ESTADO ACTUAL

- ✅ Especificación matemática completa
- ✅ Mapeos en C++ implementados
- ✅ Schema JSON definido
- ⚠️ LibPD: stubs (implementación en Fase 1)
- ⚠️ Audio engine: stubs (implementación en Fase 1)
- ❌ Visión: no implementado (Fase 1)
- ❌ ML: no implementado (Fase 3)
- ❌ Accesibilidad: no implementado (Fase 2)

## 🔧 PRÓXIMOS PASOS (FASE 1)

### Semana 1: LibPD Integration
```
1. Descargar libpd prebuilt para Android ARM64
2. Copiar libpd.so a jniLibs/arm64-v8a/
3. Actualizar CMakeLists.txt para enlazar libpd
4. Implementar pdInit(), pdOpenPatch(), pdSendFloat()
5. Testing: patch carga sin crash
```

### Semana 2: Audio Engine
```
1. Implementar AAudio callback loop
2. Generar onda de prueba @ 4000 Hz
3. Conectar a MainActivity
4. Testing: escuchar sonido en auriculares
```

### Semana 3-4: Vision Frontend
```
1. Captura de cámara @ 30 fps
2. Análisis: generar grid 16x16
3. Generar control_schema.json
4. Enviar parámetros a síntesis
5. Testing end-to-end: cámara → sonido
```

Ver **[CHECKLIST_FASE_1.md](../CHECKLIST_FASE_1.md)** para detalles completos.

## 📖 ARCHIVOS CLAVE

| Archivo | Propósito |
|---------|-----------|
| `MainActivity.kt` | Launcher + UI |
| `native-lib.cpp` | JNI main entry points |
| `mapping_engine.cpp` | 6 mapeos matemáticos (elevación, distancia, azimut, luz, material, confidence) |
| `audio_engine.cpp` | AAudio I/O (TBD) |
| `libpd_wrapper.cpp` | LibPD bindings (TBD) |
| `CMakeLists.txt` | Build configuration |
| `build.gradle` | Gradle configuration |

## 🔐 PERMISOS

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

*Nota: Solicitud de permisos en runtime (Android 6+)*

## 📊 ESPECIFICACIÓN TÉCNICA

### Audio
- **Sample Rate**: 44.1 kHz
- **Channels**: 2 (estéreo binaural)
- **Buffer Size**: 64-256 samples
- **Latency Target**: <100 ms

### Visión
- **Camera Resolution**: 640×480
- **Frame Rate**: 30 fps
- **Grid**: 16×16 celdas
- **Latency**: <70ms análisis

### Mapeos Matemáticos
1. **Elevación → Frecuencia**: f(h) = 60 * exp(1.7685 * h) Hz
2. **Distancia → Ganancia + LPF**: G(r) = 1 / (1 + (r/1.0)²), fc(r) = 12000 * exp(-0.18*r) Hz
3. **Azimut → Paneo binaural**: ITD + ILD + equal-power panning
4. **Luminancia → Binaural beats**: Δ = 5 + 7*L (Hz)
5. **Material → Síntesis**: FM (metal), aditiva (madera), granular (piedra)
6. **Confidence → Mixtura**: Fade basado en confianza de detección

## 🧪 TESTING

```bash
# Build
./gradlew build

# Unit tests
./gradlew test

# Instrumented tests (dispositivo/emulador)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

## 📱 DEPLOYMENT

### Local Testing
```bash
# Connect device o abrir emulador
adb devices

# Install APK
./gradlew installDebug

# Run
./gradlew assembleDebug
```

### Google Play Store (Futuro)
- [ ] Sign APK con release key
- [ ] Create app listing en Play Console
- [ ] Subir APK + metadata
- [ ] Configurar pricing (free)
- [ ] Submit para review

## 🚨 TROUBLESHOOTING

### CMake error: "libpd.so not found"
```
→ Verificar ruta exacta en CMakeLists.txt
→ Confirmar libpd.so existe en jniLibs/arm64-v8a/
```

### MainActivity crashes on load
```
→ Verificar permisos en runtime
→ Check logcat: adb logcat | grep Visualonda
```

### No audio output
```
→ Verificar AAudio stream initialization
→ Check headphones conectados
→ Verificar volumen no está en 0
```

Ver **[TROUBLESHOOTING.md](../docs/TROUBLESHOOTING.md)** para más.

## 📞 SUPPORT

- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Email**: [contact@visualonda.dev]
- **Community**: [Discord/Slack link TBD]

## 📄 LICENCIA

[Apache 2.0](../../LICENSE) o [GPL 3.0](../../LICENSE.GPL)

## 🙏 CRÉDITOS

- **Visión científica**: Investigación de sustitución sensorial (Meijer, Bach-y-Rita, Merabet)
- **Audio**: LibPD, Pure Data
- **ML**: TensorFlow Lite, MediaPipe
- **Comunidad**: Usuarios ciegos y investigadores colaboradores

---

**Status**: 🚧 En desarrollo (Fase 1)
**Última actualización**: Julio 2026
**Versión**: 0.1.0-alpha

