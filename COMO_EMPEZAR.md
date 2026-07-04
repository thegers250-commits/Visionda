# ¿CÓMO EMPEZAR CON VISUALONDA?

## 🎯 Si eres TECH LEAD / ARQUITECTO

1. **Lee primero:**
   - [RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md) — 10 min
   - [VISION_Y_ROADMAP.md](VISION_Y_ROADMAP.md) — 30 min
   - [ESTRATEGIA_EQUIPO.md](ESTRATEGIA_EQUIPO.md) — 20 min

2. **Valida con stakeholders:**
   - ¿Budget aprobado (~$244K)?
   - ¿Team disponible (3.5-4 FTE)?
   - ¿Timeline realista (18 semanas)?
   - ¿Acceso a usuarios ciegos para testing?

3. **Setup inicial:**
   - Crear repo GitHub privado
   - Setup GitHub Projects para Jira-like management
   - Crear canales de comunicación (Slack)
   - Invitar tech team

4. **Kick-off Fase 1:**
   - Review [FASE_1_IMPLEMENTATION_PLAN.md](FASE_1_IMPLEMENTATION_PLAN.md)
   - Asignar tasks del [CHECKLIST_FASE_1.md](CHECKLIST_FASE_1.md)
   - Schedule daily standups
   - Start Semana 1

---

## 🔧 Si eres ANDROID/NDK ENGINEER

1. **Requisitos previos:**
   - Android Studio Dolphin+ instalado
   - NDK r23c+ installed
   - CMake 3.10.2+
   - Git

2. **Setup local (30 min):**

   ```bash
   # Clone repo
   git clone https://github.com/visualonda/visualonda.git
   cd visualonda/android_skeleton
   
   # Open in Android Studio
   open -a "Android Studio" .
   
   # Sync Gradle
   # File > Sync Now
   ```

3. **Primera tarea (Semana 1):**
   - Ir a [CHECKLIST_FASE_1.md](CHECKLIST_FASE_1.md), Sección "SEMANA 1"
   - Descargar libpd (2-3 horas)
   - Actualizar CMakeLists.txt (1 hora)
   - Crear libpd_wrapper.cpp (3-4 horas)
   - Testing & commit

4. **Tips:**
   - Usar LogCat para debugging: `adb logcat | grep Visualonda`
   - Compile frequently para detectar errores temprano
   - Preguntar en #visualonda-dev Slack si atascado

---

## 🤖 Si eres ML/VISION ENGINEER

1. **Espera hasta Semana 9** (Fase 3)
   - La base Android debe estar estable primero

2. **Entretanto:**
   - Lee [FASE_1_IMPLEMENTATION_PLAN.md](FASE_1_IMPLEMENTATION_PLAN.md) Semana 3-4
   - Familiarízate con TensorFlow Lite + Android
   - Prepara modelos:
     - MobileNetV2 (object detection)
     - MiDaS (depth estimation)
     - Tesseract (OCR)
   - Setup ambiente de desarrollo ML

3. **Semana 9 tasks:**
   - Integrar TensorFlow Lite en app
   - Implementar ObjectDetector.kt
   - Calibrar latencia (<30ms inferencia)
   - Testing

---

## 🎛️ Si eres AUDIO/DSP ENGINEER

1. **Empieza Semana 1 (part-time):**
   - Revisar `light_material_patch.pd`
   - Asegurar patch es optimizado
   - Preparar alternativas si Pure Data es lento

2. **Semana 2:**
   - Colaborar con Android engineer en audio_engine.cpp
   - Verificar que AAudio thread safety
   - Tuning de buffer sizes

3. **Fase 4 (Semana 13-18):**
   - SPL safety hardening
   - Binaural beat safety checks
   - Audio profiling
   - Performance optimization

---

## 📚 Si eres DOCUMENTACIÓN

1. **Ahora (inicio):**
   - Revisar documentos creados
   - Feedback a Tech Lead
   - Setup Doxygen/Javadoc

2. **Ongoing:**
   - Documentar decisiones de diseño
   - Crear API reference (Javadoc + Doxygen)
   - User Guide en formato accesible
   - Developer Guide para extensiones

3. **Antes de release:**
   - Manual completo
   - FAQ/Troubleshooting
   - Video tutorials (TBD)

---

## 👤 Si eres USUARIO CIEGO (Tester)

1. **Fase Beta (Semana 8):**
   - Te contactaremos para testing
   - Probar navegación accesible
   - Feedback verbal/escrito
   - Sesiones 1h bi-weekly

2. **Cómo participar:**
   - Email: [contact@visualonda.dev]
   - Subject: "Interesado en beta testing"
   - Incluir: dispositivo Android, SO, experiencia con apps

3. **What to expect:**
   - App beta gratis
   - Supporto directo del team
   - Tu feedback shaped el product

---

## 📋 CHECKLIST PARA HOY

- [ ] Leí [RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md)
- [ ] Confirmé rol en el team
- [ ] Configuré ambiente local (si aplica)
- [ ] Creé cuenta GitHub (si aplica)
- [ ] Joiné Slack workspace
- [ ] Asigné primera tarea
- [ ] Scheduled first meeting
- [ ] Preguntaré en Slack si tengo dudas

---

## 🔗 DOCUMENTOS PRINCIPALES

| Documento | Propósito | Audiencia |
|-----------|----------|-----------|
| [RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md) | Visión + plan alto nivel | C-level, sponsors |
| [VISION_Y_ROADMAP.md](VISION_Y_ROADMAP.md) | Roadmap 18 semanas | Tech lead, team |
| [FASE_1_IMPLEMENTATION_PLAN.md](FASE_1_IMPLEMENTATION_PLAN.md) | Plan técnico detallado | Developers |
| [CHECKLIST_FASE_1.md](CHECKLIST_FASE_1.md) | Tareas ejecutables | Developers |
| [ESTRATEGIA_EQUIPO.md](ESTRATEGIA_EQUIPO.md) | Team, presupuesto, riesgos | Tech lead, HR |

---

## ⚡ QUICK COMMANDS

```bash
# Clone repo
git clone https://github.com/visualonda/visualonda.git

# Enter android dir
cd visualonda/android_skeleton

# Build APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Compile + run
./gradlew build && ./gradlew installDebug

# View logs
adb logcat | grep Visualonda

# Clean build
./gradlew clean

# Update dependencies
./gradlew --refresh-dependencies
```

---

## 🆘 STUCK?

1. **Pregunta en Slack** → #visualonda-dev
2. **Busca en GitHub Issues**
3. **Revisa [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)**
4. **Email:** [support@visualonda.dev]

---

## 🎓 RECURSOS RECOMENDADOS

### Android
- [Android Developers](https://developer.android.com)
- [CameraX documentation](https://developer.android.com/training/camerax)
- [JNI Guide](https://developer.android.com/training/articles/perf-jni)

### NDK & C++
- [Android NDK documentation](https://developer.android.com/ndk)
- [CMake for Android](https://cmake.org/cmake/help/latest/manual/cmake-toolchains.7.html#cross-compiling-for-android)
- [Modern C++ (cppreference)](https://en.cppreference.com)

### Audio
- [AAudio documentation](https://developer.android.com/ndk/guides/audio/aaudio)
- [OpenSLES Guide](https://www.khronos.org/opensles/)
- [Pure Data manual](http://msp.ucsd.edu/Pd_documentation/)

### ML
- [TensorFlow Lite for Android](https://www.tensorflow.org/lite/android)
- [MediaPipe](https://mediapipe.dev)
- [ML Kit](https://developers.google.com/ml-kit)

### Accessibility
- [WCAG 2.1](https://www.w3.org/WAI/WCAG21/quickref/)
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [TalkBack Guide](https://support.google.com/accessibility/android/answer/6283677)

---

## 📞 TEAM CONTACTS

| Role | Slack | Email |
|------|-------|-------|
| Tech Lead | @tech-lead | tech-lead@visualonda.dev |
| Android/NDK | @android-eng | android@visualonda.dev |
| ML/Vision | @ml-eng | ml@visualonda.dev |
| Audio/DSP | @audio-eng | audio@visualonda.dev |
| QA | @qa | qa@visualonda.dev |
| Documentation | @docs | docs@visualonda.dev |

---

## 🚀 ¡VAMOS A HACERLO!

Este es un proyecto transformador. Vamos a crear una plataforma que cambie cómo los ciegos ven el mundo.

**Next step:** Junta kick-off el [próximo lunes] a las 10am.

---

**Última actualización:** Julio 2026
**Creado por:** Visualonda Team

