# 🏗️ PROPUESTAS DE MEJORA - ARQUITECTURA & DESIGN

**Análisis de Arquitectura Actual vs Propuesta Mejorada**

---

## 1️⃣ ARQUITECTURA ACTUAL (30% implementada)

```
MainActivity (UI cruda)
      ↓
   [3 Botones]
      ↓
Native JNI Bridge (minimal)
      ↓
native-lib.cpp (solo mapeos)
      ↓
Logcat (output)

❌ PROBLEMAS:
   - No es escalable
   - Acoplamiento UI-Logic
   - Sin capas definidas
   - Difícil de testear
```

---

## 2️⃣ ARQUITECTURA PROPUESTA (Clean Architecture + MVVM)

```
┌─────────────────────────────────────────────────┐
│            ANDROID FRAMEWORK LAYER              │
│  ┌───────────────────────────────────────────┐  │
│  │  Activities / Fragments (UI Controllers)  │  │
│  │  - CameraActivity                         │  │
│  │  - SettingsActivity                       │  │
│  │  - ScreenReaderActivity                   │  │
│  └───────────────────────────────────────────┘  │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│            PRESENTATION LAYER (MVVM)           │
│  ┌───────────────────────────────────────────┐  │
│  │  ViewModels (State Management)            │  │
│  │  - CameraViewModel                        │  │
│  │  - AudioViewModel                         │  │
│  │  - SettingsViewModel                      │  │
│  └───────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────┐  │
│  │  LiveData/StateFlow (Observable Data)     │  │
│  └───────────────────────────────────────────┘  │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│              DOMAIN LAYER                      │
│  ┌───────────────────────────────────────────┐  │
│  │  Use Cases (Business Logic)               │  │
│  │  - ProcessFrameUseCase                    │  │
│  │  - GenerateAudioUseCase                   │  │
│  │  - DetectObjectsUseCase                   │  │
│  │  - RecognizeTextUseCase                   │  │
│  └───────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────┐  │
│  │  Repositories (Interfaces)                │  │
│  │  - ICameraRepository                      │  │
│  │  - IAudioRepository                       │  │
│  │  - IMLRepository                          │  │
│  └───────────────────────────────────────────┘  │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│            DATA LAYER (Repositories)           │
│  ┌───────────────────────────────────────────┐  │
│  │  CameraRepositoryImpl                      │  │
│  │  ├─ CameraX integration                   │  │
│  │  └─ Frame processor bridge                │  │
│  ├─ AudioRepositoryImpl                       │  │
│  │  ├─ AAudio engine                         │  │
│  │  ├─ LibPD wrapper                         │  │
│  │  └─ SPL limiter                           │  │
│  ├─ MLRepositoryImpl                          │  │
│  │  ├─ TensorFlow Lite models                │  │
│  │  ├─ MediaPipe runners                     │  │
│  │  └─ ML Kit wrappers                       │  │
│  └─────────────────────────────────────────┘   │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│           INFRASTRUCTURE LAYER (C++)           │
│  ┌───────────────────────────────────────────┐  │
│  │  Audio Engine (audio_engine.cpp)          │  │
│  │  ├─ AAudio callbacks                      │  │
│  │  ├─ Thread management                     │  │
│  │  └─ Buffer management                     │  │
│  ├─ Vision Engine (frame_processor.cpp)      │  │
│  │  ├─ YUV parsing                           │  │
│  │  ├─ Grid processing                       │  │
│  │  └─ Feature extraction                    │  │
│  ├─ Mapping Engine (mapping_engine.cpp)      │  │
│  │  ├─ 6 mathematical mappings               │  │
│  │  ├─ Parameter scaling                     │  │
│  │  └─ Caching layer                         │  │
│  ├─ LibPD Wrapper (libpd_wrapper.cpp)        │  │
│  │  ├─ Thread-safe Pd communication          │  │
│  │  ├─ Patch management                      │  │
│  │  └─ Parameter sending                     │  │
│  └─────────────────────────────────────────┘   │
└────────────┬────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────┐
│           SYSTEM LAYER (Android OS)            │
│  ├─ AAudio Library (libaaudio.so)              │  
│  ├─ LibPD Library (libpd.so)                   │  
│  ├─ TensorFlow Lite Runtime                    │  
│  ├─ Camera Framework                           │  
│  └─ MediaPipe Native                           │  
└─────────────────────────────────────────────────┘
```

**Ventajas de esta arquitectura:**
```
✅ Separación clara de concerns
✅ Fácil de testear (cada layer independiente)
✅ Reutilizable (interfaces bien definidas)
✅ Escalable (agregar features sin quebrar)
✅ Mantenible (cambios localizados)
✅ Testeable (inyección de dependencias)
```

---

## 3️⃣ ESTRUCTURA DE PAQUETES RECOMENDADA

```
app/src/main/java/com/visualonda/sensory/
│
├── ui/                              # Presentation layer
│   ├── activity/
│   │   ├── MainActivity.kt (mejorada)
│   │   ├── CameraActivity.kt (NUEVA)
│   │   ├── GalleryActivity.kt (NUEVA)
│   │   ├── SettingsActivity.kt (NUEVA)
│   │   └── ScreenReaderActivity.kt (NUEVA)
│   ├── fragment/
│   │   ├── CameraFragment.kt (NUEVA)
│   │   ├── GalleryFragment.kt (NUEVA)
│   │   └── SettingsFragment.kt (NUEVA)
│   ├── viewmodel/
│   │   ├── CameraViewModel.kt (NUEVA)
│   │   ├── AudioViewModel.kt (NUEVA)
│   │   ├── SettingsViewModel.kt (NUEVA)
│   │   └── MLViewModel.kt (NUEVA)
│   ├── view/                        # Custom views
│   │   ├── AudioVisualizerView.kt (NUEVA)
│   │   ├── GridOverlayView.kt (NUEVA)
│   │   └── HapticFeedbackView.kt (NUEVA)
│   ├── dialog/
│   │   ├── OnboardingDialog.kt (NUEVA)
│   │   ├── ErrorDialog.kt (NUEVA)
│   │   └── PermissionDialog.kt (NUEVA)
│   └── adapter/                     # RecyclerView adapters
│       ├── DetectionAdapter.kt (NUEVA)
│       └── HistoryAdapter.kt (NUEVA)
│
├── domain/                          # Domain layer (business logic)
│   ├── repository/
│   │   ├── ICameraRepository.kt (NUEVA)
│   │   ├── IAudioRepository.kt (NUEVA)
│   │   ├── IMLRepository.kt (NUEVA)
│   │   └── ISettingsRepository.kt (NUEVA)
│   ├── usecase/
│   │   ├── ProcessFrameUseCase.kt (NUEVA)
│   │   ├── GenerateAudioUseCase.kt (NUEVA)
│   │   ├── DetectObjectsUseCase.kt (NUEVA)
│   │   ├── RecognizeTextUseCase.kt (NUEVA)
│   │   ├── EstimateDepthUseCase.kt (NUEVA)
│   │   └── DetectFacesUseCase.kt (NUEVA)
│   └── model/                       # Domain models
│       ├── ControlFrame.kt (NUEVA)
│       ├── ControlCell.kt (NUEVA)
│       ├── AudioParameters.kt (NUEVA)
│       ├── DetectionResult.kt (NUEVA)
│       └── AppSettings.kt (NUEVA)
│
├── data/                            # Data layer (repositories)
│   ├── repository/
│   │   ├── CameraRepositoryImpl.kt (NUEVA)
│   │   ├── AudioRepositoryImpl.kt (NUEVA)
│   │   ├── MLRepositoryImpl.kt (NUEVA)
│   │   └── SettingsRepositoryImpl.kt (NUEVA)
│   ├── datasource/                  # Local/remote data sources
│   │   ├── local/
│   │   │   ├── PreferenceDataSource.kt (NUEVA)
│   │   │   └── CacheDataSource.kt (NUEVA)
│   │   └── remote/
│   │       └── (no aplica aún)
│   └── db/                          # Database (Room)
│       ├── AppDatabase.kt (NUEVA)
│       ├── dao/
│       │   ├── SettingsDao.kt (NUEVA)
│       │   └── CacheDao.kt (NUEVA)
│       └── entity/
│           ├── SettingsEntity.kt (NUEVA)
│           └── CacheEntity.kt (NUEVA)
│
├── util/                            # Utilities
│   ├── Constants.kt (NUEVA)
│   ├── Extensions.kt (NUEVA)
│   ├── Logger.kt (NUEVA)
│   ├── PermissionHelper.kt (NUEVA)
│   ├── AudioHelper.kt (NUEVA)
│   ├── TimeHelper.kt (NUEVA)
│   └── AccessibilityHelper.kt (NUEVA)
│
├── di/                              # Dependency Injection (Hilt)
│   ├── AppModule.kt (NUEVA)
│   ├── RepositoryModule.kt (NUEVA)
│   ├── UseCaseModule.kt (NUEVA)
│   └── DataModule.kt (NUEVA)
│
└── accessibility/                   # Accessibility features
    ├── AccessibilityDelegate.kt (NUEVA)
    ├── GestureHandler.kt (NUEVA)
    ├── HapticFeedback.kt (NUEVA)
    ├── TalkBackIntegration.kt (NUEVA)
    └── AudioAnnouncerUseCase.kt (NUEVA)

app/src/main/cpp/
│
├── CMakeLists.txt (ACTUALIZADO)
│
├── audio/
│   ├── audio_engine.cpp (NUEVA)
│   ├── audio_engine.h (NUEVA)
│   ├── spl_limiter.cpp (NUEVA)
│   └── spl_limiter.h (NUEVA)
│
├── vision/
│   ├── frame_processor.cpp (NUEVA)
│   ├── frame_processor.h (NUEVA)
│   ├── grid_processor.cpp (NUEVA)
│   └── grid_processor.h (NUEVA)
│
├── dsp/
│   ├── mapping_engine.cpp (EXISTENTE, mejorar)
│   ├── mapping_engine.h (EXISTENTE, mejorar)
│   ├── libpd_wrapper.cpp (NUEVA)
│   ├── libpd_wrapper.h (NUEVA)
│   └── pd_patch_loader.cpp (NUEVA)
│
└── jni/
    ├── native-lib.cpp (ACTUALIZAR)
    ├── jni_bridge.h (NUEVA)
    └── jni_callbacks.cpp (NUEVA)

app/src/test/java/com/visualonda/sensory/
│
├── unit/                            # Unit tests
│   ├── domain/
│   │   ├── usecase/
│   │   │   ├── ProcessFrameUseCaseTest.kt (NUEVA)
│   │   │   ├── GenerateAudioUseCaseTest.kt (NUEVA)
│   │   │   └── DetectObjectsUseCaseTest.kt (NUEVA)
│   │   └── model/
│   │       └── ControlFrameTest.kt (NUEVA)
│   ├── data/
│   │   └── repository/
│   │       └── SettingsRepositoryTest.kt (NUEVA)
│   └── util/
│       ├── AudioHelperTest.kt (NUEVA)
│       └── TimeHelperTest.kt (NUEVA)
│
└── integration/                     # Integration tests
    ├── CameraToAudioTest.kt (NUEVA)
    ├── AudioProcessingTest.kt (NUEVA)
    └── AccessibilityTest.kt (NUEVA)

app/src/androidTest/java/com/visualonda/sensory/
│
├── ui/                              # UI tests (Espresso)
│   ├── CameraActivityTest.kt (NUEVA)
│   ├── SettingsActivityTest.kt (NUEVA)
│   └── AccessibilityUITest.kt (NUEVA)
│
└── performance/                     # Performance tests
    ├── FrameProcessingBenchmark.kt (NUEVA)
    ├── AudioLatencyBenchmark.kt (NUEVA)
    └── MLInferenceBenchmark.kt (NUEVA)
```

**Total de archivos nuevos: ~120 Kotlin + 20 C++ + 15 XML**

---

## 4️⃣ PATRONES DE DISEÑO RECOMENDADOS

### A. Dependency Injection (Hilt)

```kotlin
// ❌ ACTUAL (coupling fuerte):
class CameraActivity : AppCompatActivity() {
    private val repository = CameraRepositoryImpl()
    private val usecase = ProcessFrameUseCase(repository)
}

// ✅ PROPUESTO (con Hilt):
@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private val viewModel: CameraViewModel by viewModels()
    
    // ViewModel inyectado automáticamente
    // Con sus dependencias resueltas por Hilt
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val processFrameUseCase: ProcessFrameUseCase,
    private val audioUseCase: GenerateAudioUseCase
) : ViewModel()
```

**Beneficio:** Fácil de testear, componentes desacoplados

---

### B. Repository Pattern

```kotlin
// ❌ ACTUAL (lógica mezclada):
class MainActivity : AppCompatActivity() {
    fun processFrame(image: Image) {
        // Aquí tengo lógica de visión, audio, ML todo junto
        val yData = image.planes[0].buffer.array()
        // ... procesamiento ...
        // Luego envío a audio
        // Luego a ML
        // Todo en el UI thread ❌
    }
}

// ✅ PROPUESTO (separación clara):
interface ICameraRepository {
    suspend fun processFrame(image: Image): ControlFrame
}

class CameraRepositoryImpl @Inject constructor() : ICameraRepository {
    override suspend fun processFrame(image: Image): ControlFrame {
        // Aquí SOLO lógica de cámara/visión
        return frameProcessor.process(image)
    }
}

// En ViewModel:
viewModelScope.launch {
    val frame = cameraRepository.processFrame(image)
    val audioParams = audioRepository.generateAudio(frame)
    val detections = mlRepository.detectObjects(frame)
}
```

**Beneficio:** Cada repository tiene responsabilidad única

---

### C. State Management (StateFlow)

```kotlin
// ❌ ACTUAL (variables mutables):
class CameraActivity : AppCompatActivity() {
    private var currentFrame: Image? = null
    private var isProcessing = false
    private var lastError: String? = null
}

// ✅ PROPUESTO (reactive state):
class CameraViewModel @Inject constructor(
    private val processFrameUseCase: ProcessFrameUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()
    
    private val _events = Channel<CameraEvent>()
    val events: Flow<CameraEvent> = _events.receiveAsFlow()
    
    fun processFrame(image: Image) {
        viewModelScope.launch {
            _uiState.value = CameraUiState.Processing
            try {
                val frame = processFrameUseCase(image)
                _uiState.value = CameraUiState.Success(frame)
                _events.send(CameraEvent.FrameProcessed(frame))
            } catch (e: Exception) {
                _uiState.value = CameraUiState.Error(e.message ?: "Unknown error")
                _events.send(CameraEvent.Error(e))
            }
        }
    }
}

// En Activity:
lifecycleScope.launch {
    viewModel.uiState.collectLatest { state ->
        when (state) {
            is CameraUiState.Processing -> showLoading()
            is CameraUiState.Success -> renderFrame(state.frame)
            is CameraUiState.Error -> showError(state.message)
        }
    }
}
```

**Beneficio:** UI siempre refleja estado actual, fácil de debuggear

---

### D. Use Cases Pattern

```kotlin
// UseCase base
abstract class BaseUseCase<in Params, out Result> {
    abstract suspend fun execute(params: Params): Result
    
    suspend operator fun invoke(params: Params): Result = execute(params)
}

// Uso:
class ProcessFrameUseCase @Inject constructor(
    private val cameraRepository: ICameraRepository,
    private val mappingRepository: IMappingRepository
) : BaseUseCase<Image, ControlFrame>() {
    
    override suspend fun execute(params: Image): ControlFrame {
        val rawFrame = cameraRepository.processFrame(params)
        return mappingRepository.applyMappings(rawFrame)
    }
}

// En ViewModel:
private fun onFrameReceived(image: Image) {
    viewModelScope.launch {
        try {
            val frame = processFrameUseCase(image) // Invoca operador invoke
            _uiState.value = UiState.Success(frame)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e)
        }
    }
}
```

**Beneficio:** Casos de uso reutilizables, testeable, claro

---

## 5️⃣ DATA FLOW - PROPUESTO

### De Cámara a Audio (End-to-End):

```
1. Camera.onFrameAvailable(Image)
   ↓
2. CameraAnalyzer.analyze(Image) [CameraX listener]
   ↓
3. ViewModel.processFrame(Image) [UI thread]
   ↓
4. UseCase.execute(Image) [IO thread/coroutine]
   ↓
5. CameraRepository.processFrame(Image) [IO thread]
   ├─ Extract YUV data
   ├─ Generate 16x16 grid
   ├─ Calculate cell parameters (az, el, dist)
   └─ Return ControlFrame
   ↓
6. MappingRepository.applyMappings(ControlFrame) [IO thread]
   ├─ Call native C++ mapping_engine
   ├─ elevation_to_freq(), distance_gain(), etc
   └─ Return AudioParameters
   ↓
7. AudioRepository.sendToAudio(AudioParameters) [Audio thread]
   ├─ Thread-safe queue to audio callback
   ├─ Audio callback receives parameters
   ├─ Update LibPD receivers
   └─ Synthesize audio (AAudio callback)
   ↓
8. AAudio callback generates samples [Real-time thread]
   ├─ Fetch current parameters
   ├─ Sine wave synthesis
   ├─ Apply effects (compression, notch)
   └─ Output to speaker
   ↓
9. ViewModel.updateUI(DetectionResults)
   ├─ Update LiveData
   └─ UI renders results
```

**Beneficio:** Claridad, sin deadlocks, responsive

---

## 6️⃣ ERROR HANDLING MEJORADO

```kotlin
// Sealed class para resultados
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
    data class Loading<T> : Result<T>()
}

// En repository:
class AudioRepositoryImpl @Inject constructor() : IAudioRepository {
    override suspend fun sendToAudio(params: AudioParameters): Result<Unit> {
        return try {
            if (!audioEngine.isInitialized()) {
                audioEngine.initialize()
            }
            audioEngine.updateParameters(params)
            Result.Success(Unit)
        } catch (e: AudioEngineException) {
            Result.Error(e) // Catched and handled
        } catch (e: Exception) {
            Result.Error(AudioEngineException("Unexpected error", e))
        }
    }
}

// En ViewModel:
private fun processAudio(params: AudioParameters) {
    viewModelScope.launch {
        val result = audioRepository.sendToAudio(params)
        when (result) {
            is Result.Success -> _uiState.value = UiState.AudioPlaying
            is Result.Error -> {
                Logger.error("Audio error", result.exception)
                _uiState.value = UiState.Error(result.exception.message ?: "Unknown")
            }
            is Result.Loading -> _uiState.value = UiState.Loading
        }
    }
}

// Custom exceptions
class AudioEngineException(message: String, cause: Throwable? = null) 
    : Exception(message, cause)

class CameraNotAvailableException(message: String = "Camera not available") 
    : Exception(message)

class PermissionDeniedException(permission: String) 
    : Exception("Permission denied: $permission")

class MLModelException(message: String, cause: Throwable? = null) 
    : Exception(message, cause)
```

---

## 7️⃣ TESTING STRATEGY

### Unit Tests

```kotlin
@RunWith(MockitoRunner::class)
class ProcessFrameUseCaseTest {
    
    @Mock
    private lateinit var cameraRepository: ICameraRepository
    
    @Mock
    private lateinit var mappingRepository: IMappingRepository
    
    private lateinit var useCase: ProcessFrameUseCase
    
    @Before
    fun setup() {
        useCase = ProcessFrameUseCase(cameraRepository, mappingRepository)
    }
    
    @Test
    fun `when image processed, returns valid ControlFrame`() = runTest {
        // Given
        val mockImage = mockk<Image>()
        val expectedFrame = ControlFrame(/* ... */)
        
        coEvery { cameraRepository.processFrame(mockImage) } returns rawFrame
        coEvery { mappingRepository.applyMappings(rawFrame) } returns expectedFrame
        
        // When
        val result = useCase(mockImage)
        
        // Then
        assertEquals(expectedFrame, result)
        verify { cameraRepository.processFrame(mockImage) }
    }
    
    @Test
    fun `when camera fails, throws exception`() = runTest {
        // Given
        coEvery { cameraRepository.processFrame(any()) } throws CameraException()
        
        // When & Then
        assertFailsWith<CameraException> {
            useCase(mockk())
        }
    }
}
```

### Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class CameraToAudioIntegrationTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var cameraRepository: CameraRepositoryImpl
    private lateinit var audioRepository: AudioRepositoryImpl
    
    @Before
    fun setup() {
        cameraRepository = CameraRepositoryImpl()
        audioRepository = AudioRepositoryImpl()
    }
    
    @Test
    fun `end-to-end: camera frame to audio output`() = runTest {
        // Given
        val testFrame = createTestCameraFrame()
        
        // When
        val controlFrame = cameraRepository.processFrame(testFrame)
        val audioParams = audioRepository.generateAudio(controlFrame)
        val latency = measureTimeMillis {
            audioRepository.sendToAudio(audioParams)
        }
        
        // Then
        assertTrue(latency < 100L) // <100ms latency
        assertTrue(audioParams.frequency in 20f..20000f)
        assertTrue(audioParams.amplitude in 0f..1f)
    }
}
```

---

## 8️⃣ PERFORMANCE CONSIDERATIONS

```kotlin
// ❌ BAD: Crear objetos en cada frame
fun processFrame(image: Image) {
    val parameters = AudioParameters(...) // ALLOCATES EVERY FRAME!
    val detections = mutableListOf<Detection>() // ALLOCATES EVERY FRAME!
    // ... más allocations ...
}

// ✅ GOOD: Reutilizar buffers
private val parameterPool = ObjectPool(::AudioParameters)
private val detectionBuffer = mutableListOf<Detection>()

fun processFrame(image: Image) {
    val parameters = parameterPool.acquire()
    try {
        // Use reused object
        parameters.frequency = calculateFrequency()
        detectionBuffer.clear()
        // Reutilizar buffer
        detectObjects(detectionBuffer)
    } finally {
        parameterPool.release(parameters)
    }
}

// ❌ BAD: Coroutine por cada frame
override fun onFrameAvailable() {
    viewModelScope.launch {  // 30 FPS = 30 launches/sec = GC pressure!
        processFrame()
    }
}

// ✅ GOOD: Flow-based processing
private fun startFrameProcessing() {
    frameFlow
        .flowOn(Dispatchers.Default)
        .collect { frame ->
            processFrame(frame)
        }
}

// ❌ BAD: Logging en audio callback (real-time thread!)
override fun onAudioCallback(buffer: FloatArray) {
    Log.d(TAG, "Processing audio") // NO! Blocks real-time thread
    synthesizeAudio(buffer)
}

// ✅ GOOD: Ring buffer for metrics
class AudioCallbackMetrics {
    private val ringBuffer = RingBuffer<AudioEvent>(capacity = 1000)
    
    override fun onAudioCallback(buffer: FloatArray) {
        // Only add to non-blocking ring buffer
        ringBuffer.offer(AudioEvent.SampleGenerated)
        synthesizeAudio(buffer)
    }
    
    // Fuera del callback, leer metrics cuando sea necesario
    fun printMetrics() {
        Log.d(TAG, "Audio callbacks: ${ringBuffer.size()}")
    }
}
```

---

## 9️⃣ CONCLUSIÓN

Esta arquitectura propuesta:

```
✅ Escalable: Agregar features sin quebrar existente
✅ Testeable: Cada capa se testa independientemente
✅ Mantenible: Código claro, separación de concerns
✅ Performante: Considerar GC, threading, memory management
✅ Robusta: Error handling, edge cases
✅ Accesible: Accessible-first design para ciegos
✅ Documentada: Cada componente tiene propósito claro

Con esta estructura + 12,000 líneas de código
+ testing riguroso → APP RELEASE-READY en 18 semanas ✅
```

