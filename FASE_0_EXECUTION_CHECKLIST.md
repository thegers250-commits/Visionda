# ✅ FASE 0 EXECUTION CHECKLIST

**3-5 días para compilación + arquitectura setup**

---

## TODAY - DECISIONES REQUERIDAS

- [ ] **DECISIÓN 1:** ¿Qué roadmap? (A: 6-8w, B: 14-16w RECOMENDADO, C: 18w)
  - [ ] Confirmar en meeting hoy
  - [ ] Documentar razón

- [ ] **DECISIÓN 2:** Presupuesto confirmado
  - [ ] $25K (A), $30-40K (B rec), $40-50K (C)

- [ ] **DECISIÓN 3:** Equipo confirmado
  - [ ] Tech Lead: __________
  - [ ] Android Lead: __________
  - [ ] Audio/DSP: __________
  - [ ] ML (Fase 3+): __________
  - [ ] QA: __________

- [ ] **READ:** `DECISION_FINAL_ROADMAP.md` (10 min)

---

## DAY 1 (LUNES) - 2 HORAS

### Setup Binarios

- [ ] **Descargar libpd**
  - [ ] Go to https://github.com/libpd/libpd/releases
  - [ ] Download `libpd-0.12.x-android.zip`
  - [ ] Extract to temp folder

- [ ] **Crear directorios**
  ```
  [ ] android_skeleton/app/src/main/jniLibs/arm64-v8a/
  [ ] android_skeleton/app/src/main/cpp/libpd/include/
  [ ] android_skeleton/app/src/main/assets/patches/
  ```

- [ ] **Copiar archivos**
  - [ ] libpd.so → jniLibs/arm64-v8a/
  - [ ] libpd.h, pd.h, m_pd.h → cpp/libpd/include/
  - [ ] light_material_patch.pd → assets/patches/

- [ ] **Verificar estructura**
  ```
  [ ] ls jniLibs/arm64-v8a/libpd.so (exist? ~1.2 MB)
  [ ] ls cpp/libpd/include/ (3 headers present?)
  [ ] ls assets/patches/ (patch present?)
  ```

---

## DAY 2 (MARTES) - 8 HORAS

### Build.gradle Updates

- [ ] **Actualizar app/build.gradle**
  - [ ] Add Hilt plugin: `id 'dagger.hilt.android.plugin'`
  - [ ] Add kotlin plugins: `id 'kotlin-android'`, `id 'kotlin-kapt'`
  - [ ] Update compileSdk → 34
  - [ ] Update targetSdk → 34
  - [ ] Add ndk abiFilters → 'arm64-v8a'
  - [ ] Add buildFeatures → viewBinding, dataBinding
  - [ ] Replace dependencies block (see bridge doc)

- [ ] **Actualizar build.gradle (project)**
  - [ ] Add Hilt gradle plugin to buildscript
  - [ ] Add Kotlin gradle plugin to buildscript

### Crear Estructura de Paquetes

- [ ] **domain/model/** - Crear 3 archivos
  - [ ] ControlFrame.kt
  - [ ] ControlCell.kt
  - [ ] AudioParameters.kt

- [ ] **domain/repository/** - Crear 3 interfaces
  - [ ] ICameraRepository.kt
  - [ ] IAudioRepository.kt
  - [ ] ISettingsRepository.kt

- [ ] **domain/usecase/** - Crear 2 use cases
  - [ ] ProcessFrameUseCase.kt
  - [ ] GenerateAudioUseCase.kt

- [ ] **data/repository/** - Crear 3 implementations
  - [ ] CameraRepositoryImpl.kt
  - [ ] AudioRepositoryImpl.kt
  - [ ] SettingsRepositoryImpl.kt

- [ ] **data/datasource/local/** - Crear 1 data source
  - [ ] PreferenceDataSource.kt

- [ ] **ui/viewmodel/** - Crear 2 ViewModels
  - [ ] CameraViewModel.kt
  - [ ] AudioViewModel.kt

### Crear Hilt DI Modules

- [ ] **di/AppModule.kt**
  - [ ] Provides SharedPreferences

- [ ] **di/RepositoryModule.kt**
  - [ ] Bind all 3 repositories

- [ ] **di/UseCaseModule.kt**
  - [ ] Provides all use cases

- [ ] **di/DataModule.kt**
  - [ ] Provides data sources

### Crear Utility Classes

- [ ] **util/Constants.kt**
  - [ ] Audio constants
  - [ ] Camera constants
  - [ ] SharedPrefs keys

- [ ] **util/Extensions.kt**
  - [ ] Common extensions

### Actualizar MainActivity

- [ ] **Reemplazar MainActivity.kt completamente**
  - [ ] Add @AndroidEntryPoint
  - [ ] Add viewModels (Hilt injection)
  - [ ] Update setupUI() with 3 buttons
  - [ ] Add logging with Timber
  - [ ] Remove old stubs

---

## DAY 3 (MIÉRCOLES) - 3 HORAS

### CMakeLists.txt

- [ ] **Actualizar app/CMakeLists.txt**
  - [ ] Add source files list (placeholder for Week 1-2)
  - [ ] Add include directories
  - [ ] Add libpd.so linking
  - [ ] Add AAudio linking
  - [ ] Add compiler flags

### First Build

- [ ] **Clean & Build**
  ```
  [ ] ./gradlew clean
  [ ] ./gradlew build
  [ ] Result: BUILD SUCCESSFUL?
  ```

- [ ] **Troubleshooting (if needed)**
  - [ ] Check for compiler errors
  - [ ] Check linker errors
  - [ ] Verify all directories exist
  - [ ] Fix any Hilt/Kotlin issues

### Installation & Testing

- [ ] **Install to Device**
  ```
  [ ] ./gradlew installDebug
  [ ] App appears on device?
  ```

- [ ] **Manual Testing**
  - [ ] App opens without crash?
  - [ ] See 3 buttons on screen?
  - [ ] "Init Audio Engine" button → no crash, shows toast
  - [ ] "Init PD" button → no crash, shows toast
  - [ ] "Start Camera" button → no crash, shows toast

- [ ] **Logcat Check**
  ```
  [ ] adb logcat | grep VisualondaNative
  [ ] Check for normal startup messages
  [ ] No crash messages?
  ```

---

## GATE FASE 0 - CRITERIA CHECKLIST

### Arquitectura
- [ ] Package structure complete (domain/data/ui/di)
- [ ] Hilt DI modules configured
- [ ] MVVM ViewModels created
- [ ] Repository interfaces + implementations done
- [ ] Use cases created

### Build System
- [ ] Compilation: 0 errors
- [ ] Warnings: <10 (and not critical)
- [ ] CMakeLists.txt updated for AAudio + libpd
- [ ] build.gradle has all dependencies

### Runtime
- [ ] App installs without errors
- [ ] MainActivity opens
- [ ] No crashes on startup
- [ ] All 3 buttons clickable
- [ ] Toast messages appear on button click
- [ ] Logcat clean (no red error lines)

### Code Quality
- [ ] Inline comments in critical functions
- [ ] Classes have KDoc comments
- [ ] No unused imports
- [ ] Consistent code style

### Documentation
- [ ] README.md updated with architecture
- [ ] All new files have header comments
- [ ] Bridge doc completed

---

## IF CRITERIA NOT MET

```
If compilation fails:
[ ] Check error message carefully
[ ] Google the specific error
[ ] Verify file paths
[ ] Check dependency versions
→ Iterate + try fix

If runtime crash:
[ ] Check logcat for stack trace
[ ] Verify all directories exist
[ ] Check Hilt injection
[ ] Remove & rebuild

If tooling issue:
[ ] Update Android Studio
[ ] Update Gradle
[ ] Clean gradle cache: rm -rf .gradle
```

---

## WHEN CRITERIA MET ✅

```
Proceed to Fase 1 Week 1 Execution
Next doc: FASE_1_IMPLEMENTATION_PLAN.md
Next meeting: Thursday Kick-off
```

---

## TEAM ASSIGNMENTS

| Role | Name | Responsible For |
|------|------|-----------------|
| Tech Lead | ________ | Overview, reviews, decisions |
| Android Dev | ________ | UI, ViewModels, setup |
| Audio Engr | ________ | libpd_wrapper (Week 1) |
| ML Engr | ________ | Fase 3+ |
| QA | ________ | Testing, documentation |

---

## DAILY STANDUP QUESTIONS

**Every morning (starting after Fase 0):**

```
1. What did I finish yesterday?
2. What will I finish today?
3. What's blocking me?
4. Do I need help?
```

---

## CONTACT & ESCALATION

```
Build blocked? → Tech Lead
Architecture questions? → Tech Lead
NDK/C++ issues? → Audio Engineer
Android issues? → Android Developer
```

---

**Documento:** FASE_0_EXECUTION_CHECKLIST.md
**Uso:** Imprime + marca los checkboxes mientras avanzas
**Timeline:** 2-3 días antes de Fase 1

