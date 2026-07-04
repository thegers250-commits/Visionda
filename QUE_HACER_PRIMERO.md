# 🎯 QUÉ HACER PRIMERO - PLAN DE ACCIÓN INMEDIATO

**Basado en:** Análisis de Gaps + Propuestas de Arquitectura
**Audiencia:** Tech Lead, Android Lead, Team Manager

---

## 🚨 PRIORIDAD 1: ANTES DE FASE 0 (Esta semana)

### 1.1 ARQUITECTURA - Setup (1-2 horas)

**¿Qué?** Implementar estructura de Dependency Injection + Clean Architecture

**¿Por qué?** Sin esto, Fase 1 será caos. Mejor 1 día de setup que 7 días de refactoring.

**Hacer:**

```bash
# 1. Agregar Hilt a build.gradle
dependencies {
    implementation 'com.google.dagger:hilt-android:2.46'
    kapt 'com.google.dagger:hilt-compiler:2.46'
    implementation 'androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03'
}

# 2. Crear estructura de paquetes:
app/src/main/java/com/visualonda/sensory/
├── ui/                      # Activities, Fragments, ViewModels
├── domain/                  # Use Cases, Repository interfaces
├── data/                    # Repository implementations, DataSources
├── di/                      # Hilt modules
├── util/                    # Helpers, extensions
└── infrastructure/          # Native interfaces
```

**Tiempo:** 2 horas
**Responsable:** Tech Lead
**No es opcional:** SÍ, es bloqueador

---

### 1.2 DATA MODELS - Crear (2 horas)

**¿Qué?** Crear clases de dominio

**Hacer:**

```kotlin
// app/src/main/java/com/visualonda/sensory/domain/model/

// 1. ControlFrame.kt
data class ControlFrame(
    val timestampMs: Long,
    val frameRateHz: Int,
    val gridSize: Int = 16,
    val cells: List<ControlCell>
)

// 2. ControlCell.kt
data class ControlCell(
    val id: Int,
    val row: Int,
    val col: Int,
    val azimuthDeg: Float,
    val elevationM: Float,
    val distanceM: Float,
    val material: String,
    val luminance: Float,
    val confidence: Float
)

// 3. AudioParameters.kt
data class AudioParameters(
    val frequency: Float,
    val amplitude: Float,
    val leftPan: Float,  // -1 to +1
    val rightPan: Float,
    val lpfCutoff: Float,
    val modulation: Float
)

// 4. AppSettings.kt
data class AppSettings(
    val volumePercent: Int = 75,
    val splLimit: Float = 85f,
    val gridSize: Int = 16,
    val talkBackEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val audioProfile: AudioProfile = AudioProfile.BINAURAL
)

enum class AudioProfile {
    BINAURAL, STEREO, MONO
}

// 5. MLDetectionResult.kt
data class MLDetectionResult(
    val objects: List<Detection>,
    val depth: FloatArray,
    val text: List<TextBlock>,
    val faces: List<Face>,
    val timestamp: Long
)

data class Detection(
    val label: String,
    val confidence: Float,
    val box: BoundingBox
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class TextBlock(
    val text: String,
    val box: BoundingBox,
    val language: String
)

data class Face(
    val id: Int,
    val box: BoundingBox,
    val landmarks: List<Float>
)
```

**Tiempo:** 2 horas
**Responsable:** Android Lead
**Bloquea a:** Todos (pero no totalmente)

---

### 1.3 TEST INFRASTRUCTURE - Setup (3 horas)

**¿Qué?** Configurar testing framework

**Hacer:**

```gradle
// app/build.gradle - agregar dependencies
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:5.2.1'
testImplementation 'org.mockito.kotlin:mockito-kotlin:5.1.0'
testImplementation 'io.mockk:mockk:1.13.8'
testImplementation 'androidx.test.ext:junit:1.1.5'
testImplementation 'androidx.test:core:1.5.0'
testImplementation 'kotlinx-coroutines-test:1.7.1'

androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.test:runner:1.5.2'
```

```kotlin
// app/src/test/java/com/visualonda/sensory/util/

// Base test class
abstract class BaseUnitTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    @Before
    open fun setup() {
        // Common setup
    }
}

// app/src/androidTest/java/com/visualonda/sensory/util/

// Base integration test
abstract class BaseIntegrationTest : BaseTest() {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
}
```

**Tiempo:** 3 horas
**Responsable:** QA Lead
**Beneficio:** Tests desde Semana 1

---

### 1.4 ARCHITECTURE DOCUMENTATION - Crear (2 horas)

**¿Qué?** Documentar decisiones arquitectónicas

**Crear archivo:** `docs/architecture/ADR-001-clean-architecture.md`

```markdown
# ADR 001: Clean Architecture + MVVM

## Context
Visualonda tiene múltiples capas (UI, audio, ML, etc).
Necesitamos arquitectura escalable y testeable.

## Decision
Usar Clean Architecture (3 capas) + MVVM (presentation layer).

## Architecture
```
UI Layer (Activities, Fragments, ViewModels)
    ↓
Domain Layer (Use Cases, Repositories interfaces)
    ↓
Data Layer (Repository implementations, Data Sources)
    ↓
Infrastructure (C++ code, external libraries)
```

## Rationale
- Separación de concerns
- Fácil de testear
- Escalable
- Mantenible
- Flexible a cambios

## Benefits
- Tests unitarios sin Android framework
- Data layer desacoplado de UI
- Use cases reutilizables
- Fácil de cambiar implementación

## Consequences
- Más archivos
- Aprendizaje curva (MVVM, DI)
- Requiere disciplina

## Status: ACCEPTED

## References
- https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
- https://developer.android.com/jetpack/guide
```

**Tiempo:** 2 horas
**Responsable:** Tech Lead

---

## ✅ CHECKLIST: ANTES DE FASE 0

```
[ ] Hilt configurado en build.gradle
[ ] Package structure creada (5 paquetes)
[ ] Data models creados (5 clases)
[ ] Testing framework setup (Junit + Mockito + Espresso)
[ ] Hilt modules creados (@HiltViewModel, @Repository)
[ ] ADR dokumentado (Clean Architecture)
[ ] Team entiende la arquitectura (brief/meeting)
[ ] Coding guidelines documentados

Si TODO ✅ → Proceder a FASE 0
Si ALGUNO ❌ → Arreglarlo antes de continuar
```

---

## 🎯 PRIORIDAD 2: FASE 0 (Semana 0)

### 2.1 Setup LibPD (3-5 días)

**Seguir:** FASE_0_SETUP_CHECKLIST.md

**No hay cambios basados en análisis.**

---

## 🎯 PRIORIDAD 3: FASE 1 WEEK 1 (Semana 1)

### 3.1 Audio Engine - CRÍTICO (60 horas)

**¿Por qué primero?** Sin audio, app es inútil. Sin audio, no se valida nada.

**Qué hacer:**

```
Day 1-2: AAudio integration (24h)
  ├─ AAudioStreamBuilder setup
  ├─ Callback implementation
  ├─ Buffer management
  └─ Thread safety

Day 2-3: LibPD wrapper (18h)
  ├─ pd_init()
  ├─ pd_openpatch()
  ├─ pd_send_float() ×6 receivers
  └─ Error handling

Day 4: SPL Limiter (12h)
  ├─ SPL calculation
  ├─ Dynamic range compression
  └─ Notch filters

Day 5: Testing (6h)
  ├─ Callback latency tests
  ├─ Sample rate verification
  └─ No crashes test
```

**Archivos a crear:**
```
app/src/main/cpp/
├── audio/audio_engine.cpp (250 líneas)
├── audio/audio_engine.h (50 líneas)
├── dsp/libpd_wrapper.cpp (150 líneas)
├── dsp/libpd_wrapper.h (40 líneas)
├── audio/spl_limiter.cpp (100 líneas)
└── audio/spl_limiter.h (30 líneas)

app/src/test/java/
└── domain/usecase/GenerateAudioUseCaseTest.kt (200 líneas)
```

**Responsable:** Audio Engineer (1 FTE)
**Bloqueador:** SÍ (todo depende de esto)

---

### 3.2 Repositories - Setup (20 horas)

**¿Qué?** Crear interfaces + implementaciones básicas

```kotlin
// app/src/main/java/com/visualonda/sensory/domain/repository/

interface IAudioRepository {
    suspend fun initializeAudioEngine(): Result<Unit>
    suspend fun sendParameters(params: AudioParameters): Result<Unit>
    suspend fun stopAudio(): Result<Unit>
    suspend fun setVolume(percent: Int): Result<Unit>
    suspend fun getCurrentSPL(): Result<Float>
}

interface ICameraRepository {
    fun startCapture(): Flow<Image>
    fun stopCapture()
    suspend fun setGridSize(size: Int)
}

interface IMLRepository {
    suspend fun detectObjects(frame: ControlFrame): DetectionResult
    suspend fun estimateDepth(frame: ControlFrame): FloatArray
}

interface ISettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun updateSettings(settings: AppSettings)
}

// Implementaciones básicas:
@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val audioEngine: NativeAudioEngine
) : IAudioRepository {
    override suspend fun initializeAudioEngine(): Result<Unit> = withContext(Dispatchers.Default) {
        try {
            audioEngine.init()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AudioEngineException("Failed to initialize", e))
        }
    }
    
    // ... más métodos ...
}

@Singleton
class CameraRepositoryImpl @Inject constructor() : ICameraRepository {
    // Implementación basic, se completa en Semana 2
}

@Singleton
class MLRepositoryImpl @Inject constructor() : IMLRepository {
    // Implementación basic, se completa en Semana 3-4
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : ISettingsRepository {
    private val prefs = context.getSharedPreferences("visualonda", Context.MODE_PRIVATE)
    
    override suspend fun getSettings(): AppSettings {
        return AppSettings(
            volumePercent = prefs.getInt("volume", 75),
            splLimit = prefs.getFloat("spl_limit", 85f),
            gridSize = prefs.getInt("grid_size", 16),
            talkBackEnabled = prefs.getBoolean("talkback", true),
            hapticEnabled = prefs.getBoolean("haptic", true)
        )
    }
    
    override suspend fun updateSettings(settings: AppSettings) {
        prefs.edit().apply {
            putInt("volume", settings.volumePercent)
            putFloat("spl_limit", settings.splLimit)
            putInt("grid_size", settings.gridSize)
            putBoolean("talkback", settings.talkBackEnabled)
            putBoolean("haptic", settings.hapticEnabled)
            apply()
        }
    }
}
```

**Tiempo:** 20 horas (puede ser paralelo con audio)
**Responsable:** Android Lead

---

### 3.3 Use Cases - Setup (10 horas)

```kotlin
// app/src/main/java/com/visualonda/sensory/domain/usecase/

@HiltViewModel
class GenerateAudioUseCase @Inject constructor(
    private val audioRepository: IAudioRepository,
    private val mappingRepository: IMappingRepository
) : BaseUseCase<ControlFrame, AudioParameters>() {
    
    override suspend fun execute(frame: ControlFrame): AudioParameters {
        val params = mappingRepository.applyMappings(frame)
        audioRepository.sendParameters(params)
        return params
    }
}

class ProcessFrameUseCase @Inject constructor(
    private val cameraRepository: ICameraRepository,
    private val mappingRepository: IMappingRepository
) : BaseUseCase<Image, ControlFrame>() {
    
    override suspend fun execute(image: Image): ControlFrame {
        return cameraRepository.processFrame(image)
    }
}

// Más use cases...
```

**Tiempo:** 10 horas
**Responsable:** Android Lead

---

## 📋 CHECKLIST: ANTES DE SEMANA 2

```
[ ] Audio engine compilando sin errores
[ ] AAudio callbacks funcionando (básico)
[ ] LibPD inicializando correctamente
[ ] SPL limiter implementado
[ ] Unit tests para audio (básico)
[ ] Repositories interfaces definidas
[ ] Repositories básicos implementados
[ ] Use cases creados
[ ] DataModels finalizados
[ ] Hilt inyección funcionando

Si TODO ✅ → Proceder a Semana 2 (Camera)
Si <80% ✅ → Extender Semana 1 (audio es crítico)
```

---

## 🚀 PRIORIDAD 4: DESPUÉS DE FASE 1

Los siguientes items en orden:

```
SEMANA 2: Camera (CameraX + Frame Processing)
SEMANA 3-4: Testing + Integration
SEMANA 5-8: Accessibility (TalkBack, Gestures, Haptic)
SEMANA 9-12: ML Integration
SEMANA 13-18: Polish + Release
```

---

## ⚠️ NO HACER (Antipatterns)

```
❌ NO iniciar Fase 1 sin arquitectura definida
❌ NO esperar a Fase 4 para agregar tests
❌ NO hardcodear valores (siempre usar constants)
❌ NO escribir C++ sin JNI thread safety
❌ NO olvidar error handling (trycatch everywere)
❌ NO hacer audio en main thread
❌ NO hacer UI updates desde background thread
❌ NO esperar a cambios grandes → hacer refactor modular
❌ NO ignorar warnings del compilador
❌ NO comitear código sin tests
```

---

## ✅ RESUMEN EJECUTIVO

### Antes de Fase 0:
```
[ ] Architecture setup (Hilt, Clean Architecture)
[ ] Data models creados
[ ] Test infrastructure ready
[ ] ADR documented

Tiempo: ~8-10 horas
Responsable: Tech Lead + Android Lead
Bloqueador: SÍ
```

### Semana 1 (Fase 1 Week 1):
```
[ ] Audio Engine implementado
[ ] SPL Limiter funcionando
[ ] Repositories + Use Cases listos
[ ] Unit tests básicos
[ ] Todo compilando

Tiempo: ~100 horas
Responsable: Audio Engineer + Android Lead
Bloqueador: SÍ (todo depende)
```

---

## 📞 DECISIONES REQUERIDAS HOY

```
1. ¿Usamos Hilt para DI? 
   → YES (recomendado)

2. ¿Implementamos Clean Architecture completa?
   → YES (es el futuro)

3. ¿Escribimos tests desde el inicio?
   → YES (no es "después")

4. ¿Extendemos Semana 1 si audio no está listo?
   → YES (audio es bloqueador)

5. ¿Qué es más importante: velocidad o calidad?
   → AMBOS (arquitectura sólida es rápida a largo plazo)
```

---

## 🎯 FINAL WORD

**Si seguimos este plan:**
- ✅ Arquitectura sólida desde día 1
- ✅ Tests aseguran calidad
- ✅ Fácil de parallelizar trabajo
- ✅ 18 semanas es realista
- ✅ App en Google Play Store ✅

**Si NO seguimos este plan:**
- ❌ Refactoring en semana 12 (desastre)
- ❌ Bugs aparecen en Fase 4
- ❌ Imposible de mantener código
- ❌ Delays, delays, delays
- ❌ ❌ App never ships

**RECOMENDACIÓN: Hacer las cosas bien desde el inicio.**

---

**Documento:** QUE_HACER_PRIMERO.md
**Fecha:** Julio 2026
**Acción:** START TODAY with architecture setup

