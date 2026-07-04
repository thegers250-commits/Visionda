# ESTRATEGIA DE EQUIPO & RECOMENDACIONES

## 👥 Estructura de Equipo Recomendada

Para completar las 4 fases (18 semanas) con calidad:

```
┌─────────────────────────────────────────────────────┐
│           VISUALONDA DEV TEAM                        │
├─────────────────────────────────────────────────────┤
│                                                      │
│  👨‍💼 TECH LEAD (1 FTE)                             │
│     ├─ Responsable: Arquitectura + decisiones       │
│     ├─ Skills: Android, NDK, Audio, ML              │
│     └─ Tiempo: Full-time, 18 semanas                │
│                                                      │
│  🔧 ANDROID/NDK ENGINEER (1-2 FTE)                 │
│     ├─ Responsable: Fase 1-2 (Foundation)           │
│     ├─ Skills: Kotlin, JNI, C++, NDK                │
│     └─ Tiempo: Full-time, 8 semanas                 │
│                                                      │
│  🤖 ML/VISION ENGINEER (1 FTE)                      │
│     ├─ Responsable: Fase 3 (Inteligencia)           │
│     ├─ Skills: TensorFlow, OpenCV, MediaPipe        │
│     └─ Tiempo: Full-time, 4-5 semanas              │
│                                                      │
│  🎛️ AUDIO/DSP ENGINEER (0.5-1 FTE)                 │
│     ├─ Responsable: LibPD, Pure Data, Safety       │
│     ├─ Skills: Pure Data, DSP, C++                  │
│     └─ Tiempo: Part-time, 3-4 semanas              │
│                                                      │
│  🧪 QA/TESTING (0.5 FTE)                            │
│     ├─ Responsable: Tests, calibración              │
│     ├─ Skills: JUnit, Espresso, testing             │
│     └─ Tiempo: Part-time, 2-3 semanas              │
│                                                      │
│  📚 TECHNICAL WRITER (0.5 FTE)                      │
│     ├─ Responsable: Documentación, API              │
│     ├─ Skills: Markdown, Doxygen, Accessibility     │
│     └─ Tiempo: Part-time, contínuo                  │
│                                                      │
│  👨‍🔬 USER RESEARCH (0.5 FTE)                        │
│     ├─ Responsable: Feedback usuarios ciegos        │
│     ├─ Skills: UX research, accesibilidad           │
│     └─ Tiempo: Part-time, sesiones beta             │
│                                                      │
│  TOTAL: ~3.5-4 FTE = $180K-240K (18 semanas)       │
│                                                      │
└─────────────────────────────────────────────────────┘
```

---

## 📅 TIMELINE DETALLADO (18 Semanas)

```
SEMANA  FOCUS                  TEAM               DELIVERABLE
─────────────────────────────────────────────────────────────────
1-4     Foundation             Android (2)        Audio end-to-end ✅
        (LibPD+AAudio+Camera)  + Tech Lead

5-8     Accesibilidad          Android (1)        Navegación completa ✅
        (TalkBack+Gestos)      + Tech Lead

9-12    Inteligencia           ML (1)             ML models integrated ✅
        (TF Lite, Depth)       + Audio (0.5)

13-18   Pulido & Release       Todos (1)          RELEASE ✅
        (Perf, QA, Docs)       + QA (0.5)
```

---

## 🔄 WORKFLOW DE DESARROLLO RECOMENDADO

### Git Flow
```
main                    ← Production releases
  ↑
  │
release/v0.1.0         ← Release candidate
  ↑
  │
develop                ← Integration branch
  ↑
  ├── feature/libpd-integration
  ├── feature/audio-engine
  ├── feature/camera-vision
  ├── feature/accessibility
  └── feature/ml-models
```

### Prácticas Recomendadas

1. **Sprint Bi-weekly**
   - 2 semanas de desarrollo
   - Demo de features viernes
   - Retrospectiva & planning lunes

2. **Code Review**
   - Mínimo 2 revisores para cambios críticos (NDK, audio)
   - 1 revisor para cambios menores
   - Target: <24h turnaround

3. **Testing Policy**
   - Unit tests: mínimo 70% coverage
   - Integration tests: cada feature
   - Manual testing: weekly en dispositivo real

4. **Documentation**
   - Inline code comments en C++
   - README actualizado después de cada sprint
   - API docs (Javadoc + Doxygen) en release

---

## 🛠️ STACK TÉCNICO RECOMENDADO

### Android
- **Language**: Kotlin (preferido) + Java legacy (si existe)
- **Build**: Gradle 7.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 33 (Android 13)

### NDK
- **C++ Standard**: C++17
- **Compiler**: Clang (incluido en NDK r23c+)
- **Build**: CMake 3.10+

### Libraries
```
# Audio
- LibPD (Pure Data embedded)
- AAudio / OpenSLES

# Vision
- CameraX (AndroidX)
- TensorFlow Lite
- MediaPipe
- ML Kit (Google Play Services)

# ML Models
- MobileNetV2 (object detection)
- MiDaS (depth)
- Tesseract (OCR)

# Testing
- JUnit 4
- Espresso
- GoogleTest (C++)

# Accessibility
- AndroidX AccessibilityService
```

### Development Tools
```
- Android Studio Dolphin+
- NDK r23c+
- Git + GitHub
- Jira (project management)
- Slack (team communication)
```

---

## 📊 PRESUPUESTO ESTIMADO (18 semanas)

### Costos de Personal
| Rol | FTE | Semanas | Tarifa/hora | Subtotal |
|-----|-----|---------|-----------|----------|
| Tech Lead | 1.0 | 18 | $75 | $54,000 |
| Android/NDK | 1.5 | 18 | $65 | $70,200 |
| ML/Vision | 1.0 | 18 | $70 | $50,400 |
| Audio/DSP | 0.5 | 18 | $65 | $11,700 |
| QA | 0.5 | 18 | $50 | $9,000 |
| Writer | 0.5 | 18 | $45 | $8,100 |
| Research | 0.5 | 18 | $40 | $7,200 |
| **TOTAL PERSONAL** | | | | **$210,600** |

### Costos Infraestructura
| Item | Cantidad | Costo |
|------|----------|-------|
| Android Devices (testing) | 3-5 | $1,500 |
| Cloud Storage (build artifacts) | 500GB | $100 |
| Software Licenses | (Google Play, etc.) | $500 |
| **TOTAL INFRA** | | **$2,100** |

### Costos Indirectos (15%)
| Item | % | Amount |
|------|---|--------|
| Overhead (office, utilities) | 15% | $31,605 |
| **TOTAL INDIRECT** | | **$31,605** |

### **PRESUPUESTO TOTAL: ~$244,305 USD**

*Nota: Asume US market rates. Puede reducirse con equipo en otros mercados.*

---

## ✅ CRITERIOS DE ÉXITO POR FASE

### Fase 1: Foundation ✅
- [ ] Audio synth funcional (>44.1kHz, <100ms latencia)
- [ ] Cámara captura @ 30fps
- [ ] Mapeos calculan sin errores
- [ ] Zero crashes en 1 hora de uso
- [ ] Documentación: README + API basics

### Fase 2: Accesibilidad ✅
- [ ] TalkBack integration funciona
- [ ] 5+ gestos intuitivos
- [ ] Haptic feedback responde
- [ ] 10 usuarios ciegos navegan sin documentación
- [ ] Tutorial onboarding completable en <5 min

### Fase 3: Inteligencia ✅
- [ ] Objetos detectados en sonido real-time
- [ ] Profundidad mapea correctamente
- [ ] Texto leído en audio + TTS
- [ ] Rostros/manos detectados
- [ ] Latencia total: <150ms (cámara → audio)

### Fase 4: Polido ✅
- [ ] CPU <15%, RAM <100MB
- [ ] SPL siempre <85dB
- [ ] 0 crashes en 8 horas beta
- [ ] >80% test coverage
- [ ] Documentación completa (user + dev)
- [ ] Google Play Store LIVE

---

## 🎯 MILESTONES & GATES

```
SEMANA 4
├─ Gate: Fase 1 Review
├─ Requisitos:
│  ├─ Audio + Cámara funcional ✓
│  ├─ No crashes (1h test) ✓
│  ├─ Latencia <100ms ✓
│  ├─ Code review passed ✓
│  └─ Tests green ✓
└─ Decision: PROCEED → Fase 2 o PIVOT

SEMANA 8
├─ Gate: Fase 2 Review
├─ Requisitos:
│  ├─ TalkBack integration ✓
│  ├─ 5+ gestos working ✓
│  ├─ Haptic feedback ✓
│  ├─ Usability test: >3/5 ciegos ✓
│  └─ Tests green ✓
└─ Decision: PROCEED → Fase 3 o ITERATE

SEMANA 12
├─ Gate: Fase 3 Review
├─ Requisitos:
│  ├─ ML models integrated ✓
│  ├─ Depth working ✓
│  ├─ Latencia <150ms ✓
│  ├─ Accuracy >80% ✓
│  └─ Tests green ✓
└─ Decision: PROCEED → Fase 4 o BUG FIX

SEMANA 18
└─ Gate: Release Review
   ├─ Performance OK ✓
   ├─ Security review passed ✓
   ├─ Beta tested (20+ users) ✓
   ├─ Documentation complete ✓
   └─ LAUNCH! 🚀
```

---

## 📞 STAKEHOLDER MANAGEMENT

### Equipo Interno
- **Weekly sync**: Lunes 10am (30 min)
- **Sprint planning**: Lunes 10:30am (1h)
- **Demo/Retro**: Viernes 3pm (1.5h)
- **Ad-hoc technical**: Daily standup (15 min)

### Usuarios Ciegos (Testers)
- **Weekly**: Check-in call (30 min)
- **Bi-weekly**: Usability session (1h)
- **Bug reports**: Tracked in Jira, triaged within 24h

### Leadership/Sponsors
- **Bi-weekly**: Progress update (30 min)
- **After-gate**: Milestone review + decision

---

## 🚨 RIESGOS & MITIGACIÓN

| Riesgo | Impacto | Probabilidad | Mitigación |
|--------|---------|-------------|-----------|
| LibPD integración fallida | CRÍTICO | MEDIA | Compilar libpd desde fuente como backup |
| AAudio latencia >150ms | CRÍTICO | MEDIA | Profiling temprano, buffer optimization |
| TensorFlow Lite inferencia lenta | ALTO | MEDIA | Usar modelo más pequeño, GPU delegate |
| Permisos Android runtime issues | MEDIO | BAJA | Testear en múltiples dispositivos early |
| Usuarios ciegos feedback negativo | ALTO | BAJA | Beta testing temprano (semana 8) |
| Team member depart | MEDIO | BAJA | Documentación exhaustiva, knowledge transfer |

---

## 🎓 TRAINING NEEDS

- **LibPD**: Workshop online (4h)
- **AAudio**: Android documentation + samples
- **TensorFlow Lite**: Google Codelab (8h)
- **Accessibility**: WCAG 2.1 + TalkBack API (4h)
- **Pure Data**: Tutorial básico (2h)

---

## 💡 POST-LAUNCH ROADMAP

Una vez Fase 1 en mercado, considerar:

1. **Navegación Outdoor** (Semanas 19-24)
   - GPS + mapas integración
   - Landmark detection
   - Route guidance

2. **Educación** (Semanas 25-30)
   - Geometría sonora
   - Tablas/gráficos
   - Código fuente

3. **Extensiones**
   - Wearables (smartwatch)
   - Bone conduction headphones
   - Third-party camera integration
   - API pública para developers

