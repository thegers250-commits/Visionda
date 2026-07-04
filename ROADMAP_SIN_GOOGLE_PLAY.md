# 🚀 ROADMAP AJUSTADO - SIN GOOGLE PLAY

**Para:** Desarrollo + Testing Interno (sin publicar en Play Store)
**Duración:** 12-16 semanas (vs 18 con Play Store)
**Enfoque:** Funcionalidad + Calidad + Testing con usuarios ciegos

---

## 📋 CAMBIOS RESPECTO AL PLAN ORIGINAL

### QUÉ SE ELIMINA (Fase 4 parcial)

```
❌ NO HACER:
  - Firma de app para release
  - Preparación de Play Store
  - Screenshots profesionales
  - Proceso de aprobación Google
  - Privacy policy formal
  - Terms of service legal
  - Marketing materials

AHORRO: ~40-60 horas
```

### QUÉ SE MANTIENE (TODO LO IMPORTANTE)

```
✅ SÍ HACER:
  - Audio engine funcional
  - Captura de cámara
  - ML integrado
  - Accesibilidad (TalkBack, gestos)
  - Testing con usuarios ciegos
  - Performance optimization
  - Documentation
  - APK para testing interno
```

---

## 📅 NUEVO ROADMAP: 14 SEMANAS

### FASE 0: SETUP (3-5 días)

**Igual que antes.**

```
✅ Descargar libpd.so
✅ Setup directories
✅ CMakeLists.txt + build.gradle
✅ Compilar proyecto

Tiempo: 3-5 días
Resultado: Proyecto compilable
```

---

### FASE 1: FOUNDATION (Semanas 1-4) - 160 HORAS

**Igual que antes.**

```
SEMANA 1: LibPD + Audio Engine (40h)
  ├─ libpd_wrapper.cpp
  ├─ audio_engine.cpp (AAudio)
  └─ Testing audio

SEMANA 2: Camera + Frame Processing (40h)
  ├─ CameraX integration
  ├─ Grid generation (16x16)
  └─ Real-time processing

SEMANA 3-4: Integration + Testing (80h)
  ├─ End-to-end: camera → audio
  ├─ Performance optimization
  └─ Latency <100ms

Deliverable: Audio funcional end-to-end ✅
```

---

### FASE 2: ACCESIBILIDAD (Semanas 5-8) - 160 HORAS

**Igual que antes, CON ÉNFASIS en usuarios ciegos.**

```
SEMANA 5: TalkBack + Gestures (40h)
  ├─ TalkBack integration
  ├─ 5+ gestures (swipe, tap, long-press)
  └─ Screen reader compatibility

SEMANA 6-7: Haptic + Settings (60h)
  ├─ Haptic feedback patterns
  ├─ Settings Activity
  └─ Accessible UI

SEMANA 8: BETA TESTING CON USUARIOS CIEGOS (20h)
  ├─ 5-10 usuarios ciegos
  ├─ 2-3 horas testing cada uno
  ├─ Recopilación feedback
  └─ Iteración rápida

Deliverable: App navegable por ciegos ✅
```

---

### FASE 3: INTELIGENCIA (Semanas 9-12) - 160 HORAS

**Igual que antes.**

```
SEMANA 9-10: Object Detection (50h)
  ├─ MobileNetV2 + TensorFlow Lite
  └─ >80% accuracy

SEMANA 10-11: Depth + OCR (55h)
  ├─ Monocular depth estimation
  ├─ Text recognition (OCR)
  └─ Audio announcement of text

SEMANA 11-12: Face/Hand + Optimization (55h)
  ├─ MediaPipe Face Detection
  ├─ GPU acceleration
  └─ Caching strategies

Deliverable: ML models integrated ✅
```

---

### FASE 4: INTERNAL RELEASE (Semanas 13-14) - 80 HORAS

**MODIFICADO: Sin Google Play, pero profesional para testing**

#### Semana 13: Performance + Safety (40 horas)

```
☐ CPU/Memory profiling
☐ SPL limiter (<85dB guaranteed)
☐ Dynamic range compression
☐ 50+ test cases
☐ Edge case testing
☐ Bug fixes

Target:
✅ Latency <80ms
✅ Memory <100MB
✅ CPU <15%
✅ SPL <85dB
✅ 0 Critical bugs
```

#### Semana 14: Documentation + Internal Packaging (40 horas)

```
☐ User guide (for blind users)
☐ Developer documentation
☐ API reference
☐ Troubleshooting guide
☐ Setup guide for testing

☐ APK preparation:
  - Sign APK with debug key
  - Create APK distribution package
  - Setup internal testing infrastructure

☐ Testing infrastructure:
  - Firebase Test Lab setup (optional)
  - Internal beta group in Google Play Console (NO public)
  - APK direct sharing via:
    * GitHub releases
    * Email distribution
    * USB/ADB installation
    * QR codes for easy install

Deliverable: Professional APK ready for internal testing ✅
```

---

## 🎯 INTERNAL TESTING STRATEGY

### Option 1: GITHUB RELEASES (Recommended)

```
1. En GitHub, crear "Release" tags:
   v0.1.0-alpha (Semana 4 - Audio basic)
   v0.2.0-alpha (Semana 8 - Accessible)
   v0.3.0-alpha (Semana 12 - ML integrated)
   v0.4.0-rc (Semana 14 - Release candidate)

2. Cada release incluye:
   - APK file (app-debug.apk)
   - Release notes (qué funciona, qué falta)
   - Installation guide
   - Known issues

3. Usuarios descargan:
   $ git clone https://github.com/[org]/Visualonda
   $ cd Visualonda
   $ ./gradlew installDebug
   
   O descargan APK directamente desde:
   GitHub → Releases → app-debug.apk → descargar
```

### Option 2: GOOGLE PLAY INTERNAL TESTING (No public)

```
1. Crear app en Google Play Console (private)
2. Setup "Internal Testing" track (NOT public)
3. Agregar 10-20 testers (email addresses)
4. Publicar en track interno:
   - Usuarios reciben link privado
   - Instalan desde Play Store (seguro)
   - Automático updates
   - Built-in crash reporting

Ventaja: Crash reporting automático
Desventaja: Requiere cuenta Google Play ($25)
```

### Option 3: DIRECT DISTRIBUTION

```
1. Generar APK: ./gradlew assembleDebug
2. Enviar vía:
   - Email (APK attachment)
   - Dropbox/Google Drive
   - QR code (linking to download)
   - USB drive (physical)

3. Usuarios instalan:
   adb install app-debug.apk
   O: Settings → Install from unknown source

Ventaja: Totalmente libre, sin dependencias
Desventaja: Sin crash reporting automático
```

**RECOMENDACIÓN:** Usar GitHub Releases + Google Play Internal Testing

---

## 📊 NUEVO TIMELINE

```
Fase 0:     3-5 días    (Setup)
Fase 1:     4 semanas   (Audio + Camera)
Fase 2:     4 semanas   (Accessibility + Beta Testing)
Fase 3:     4 semanas   (ML)
Fase 4:     2 semanas   (Polish + Internal Release)

TOTAL:      14-16 semanas (vs 18 con Play Store)
AHORRO:     2-4 semanas
```

---

## 👥 BETA TESTING CON USUARIOS CIEGOS

### Week 8: First Beta Release

**Importante:** Esto es el punto de validación más crítico.

#### Setup

```
1. Recrutar 5-10 usuarios ciegos:
   - Contactar: Organizaciones para ciegos
   - Escuelas para ciegos
   - Comunidades online
   - Amigos/referidos

2. Preparar testing environment:
   - Crear beta group en GitHub
   - Enviar APK + instrucciones
   - Teléfono Android (si no tienen)

3. Instrucciones simples (en audio si es posible):
   "Abre app → Usa gestos → Cuéntanos qué funciona"

4. Recopilar feedback:
   - Cuestionario (Google Forms con audio)
   - Video calls (Zoom con screen sharing)
   - WhatsApp messages
   - Directa en persona
```

#### Key Questions para Users

```
1. ¿Puedes abrir la app sin documentación?
2. ¿Los gestos (swipe, tap) son intuitivos?
3. ¿El audio es claro?
4. ¿TalkBack funciona sin conflictos?
5. ¿Qué es lo que MÁS te gusta?
6. ¿Qué es lo que MENOS te gusta?
7. ¿Qué funcionalidad falta?
8. ¿Vuelves a usar la app?
```

#### Iteration Plan

```
Week 8: Collect feedback
Week 8-9: Fix critical issues
Week 9: Re-test with users
Week 10-11: Integrate feedback into Fase 3
```

---

## 📱 APK DISTRIBUTION WORKFLOW

### Weekly Build Process

```
Monday:     Code review + merge to main
Tuesday:    Build APK: ./gradlew assembleDebug
Wednesday:  Testing on device
Thursday:   Upload to GitHub Releases
Friday:     Announce in team channel
```

### Release Notes Template

```markdown
# Visualonda v0.2.0-alpha

**Release Date:** [Date]
**Build:** app-debug-v0.2.0.apk

## What's New
- TalkBack integration
- 5 gestures (swipe, long-press, etc)
- Settings activity

## What Works ✅
- Audio: Yes (30fps, <100ms latency)
- Camera: Yes (real-time)
- Accessibility: Partial (TalkBack + gestures)
- ML: Not yet

## What's Missing ❌
- Object detection (coming week 10)
- Depth estimation (coming week 11)
- Screen reader mode (coming week 12)

## Known Issues ⚠️
- [ ] Bug: Crash on rotation (fixed in next build)
- [ ] Issue: Audio glitch at startup (investigating)

## Installation

### Option A: ADB (Linux/Mac/Windows)
\`\`\`bash
adb install app-debug-v0.2.0.apk
\`\`\`

### Option B: Manual
1. Download APK
2. Transfer to phone via USB
3. Open file manager
4. Tap APK
5. Install

## Feedback

Please report bugs:
- GitHub Issues: [link]
- Email: [email]
- WhatsApp: [number]

Thank you for testing!
```

---

## 📊 SUCCESS METRICS (Sin Google Play)

### Instead of Download Numbers

```
✅ Beta Testers: 10+ blind users
✅ Daily Active Users (during beta): 80%+
✅ Session Duration: >15 min average
✅ User Rating: 4+ stars (internal feedback)
✅ Retention Week 2: >50% of beta testers
✅ Bug Reports: Clear + actionable feedback
✅ User Quotes: Testimonials from blind users
```

### Instead of Play Store Metrics

```
✅ Performance: <80ms latency
✅ Stability: 0 crashes (8h continuous use)
✅ Accessibility: WCAG AAA compliance
✅ Code Quality: >80% test coverage
✅ Documentation: Complete + tested
```

---

## 🎯 INTERNAL vs EXTERNAL DIFFERENCES

| Aspecto | Internal (Current) | External (Google Play) |
|--------|-------------------|------------------------|
| Testing | Beta testers internos | Público abierto |
| Crash Reporting | GitHub issues | Play Store console |
| Updates | Manual releases | Auto updates |
| Privacy | No requerido formalmente | Política privacidad requerida |
| Seguridad | Debug APK OK | Release APK con firma |
| Versioning | v0.1-alpha | v1.0 release |
| Feedback | Directo de users | Reviews en Play Store |

---

## ✅ NUEVA FASE 4: CHECKLIST

### Week 13: Performance (40 hours)

```
[ ] CPU profiling
[ ] Memory profiling
[ ] Audio latency: <80ms
[ ] Camera: 30fps stable
[ ] SPL: <85dB always
[ ] 50+ test cases passing
[ ] Bug fixes: critical only
```

### Week 14: Internal Release (40 hours)

```
[ ] Documentation complete
[ ] User guide written
[ ] Developer guide written
[ ] API reference documented
[ ] APK built and signed
[ ] GitHub releases setup
[ ] Internal testing group ready
[ ] Installation guide clear
```

---

## 🚀 POST-RELEASE PLAN (Optional)

### After 14 weeks (without Google Play):

```
OPTION A: Mantener en beta indefinidamente
  - Releases mensuales
  - Bug fixes
  - Community driven

OPTION B: Transicionar a Google Play Play (later)
  - Cuando esté estable (v1.0)
  - Release APK con firma
  - Privacy policy + terms
  - Full app store process

OPTION C: Open Source release
  - Publicar en GitHub público
  - Licencia Apache 2.0
  - Community contributions
  - Fork + improvements
```

---

## 📌 RESUMEN

```
TIMELINE:  14-16 semanas (sin Play Store)
FOCUS:     Funcionalidad + Testing + Usuarios ciegos
TESTING:   Beta testers internos (5-10 personas)
RELEASE:   GitHub + APK directo
GOAL:      App funcional, accesible, professional
```

---

## 🎯 PRÓXIMOS PASOS

### Hoy:
```
[ ] Instalar app en teléfono
[ ] Verificar funciona
```

### Mañana:
```
[ ] Leer este documento
[ ] Decidir: ¿Opción A (6-8w MVP) o B (14-16w full)?
[ ] Decidir: ¿Usar GitHub o Google Play Internal?
[ ] Asignar roles al equipo
```

### Próxima semana:
```
[ ] Comenzar FASE 1 Week 1
[ ] Audio Engine (60 hours)
[ ] Daily standups
```

---

**Documento:** ROADMAP_SIN_GOOGLE_PLAY.md
**Estado:** Listo para ejecutar sin Google Play
**Timeline:** 14-16 semanas → Release profesional interno
**Acción Requerida:** Reunión de equipo para confirmar plan

