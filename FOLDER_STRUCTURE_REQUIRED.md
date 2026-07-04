# 📁 FOLDER STRUCTURE REQUIRED FOR FASE 0 & WEEK 1

**Create these directories in exact order**

---

## ANDROID BINARIES & HEADERS (DAY 1 - MONDAY)

```
android_skeleton/
├── app/
│   └── src/
│       └── main/
│           ├── jniLibs/  ← CREAR
│           │   └── arm64-v8a/  ← CREAR
│           │       └── libpd.so  ← COPIAR AQUÍ (from libpd release)
│           │
│           ├── cpp/  ← CREAR
│           │   ├── libpd/  ← CREAR
│           │   │   └── include/  ← CREAR
│           │   │       ├── libpd.h  ← COPIAR AQUÍ
│           │   │       ├── pd.h  ← COPIAR AQUÍ
│           │   │       └── m_pd.h  ← COPIAR AQUÍ
│           │   │
│           │   └── native-lib.cpp  (EDIT - exists)
│           │
│           └── assets/  (exists)
│               ├── patches/  ← CREAR
│               │   └── light_material_patch.pd  ← COPIAR AQUÍ
│               │
│               └── config/  (CREAR pero vacío por ahora)
```

**Commands to create (PowerShell):**

```powershell
$basePath = "android_skeleton\app\src\main"

@(
    "$basePath\jniLibs\arm64-v8a",
    "$basePath\cpp\libpd\include",
    "$basePath\assets\patches",
    "$basePath\assets\config"
) | ForEach-Object { 
    New-Item -ItemType Directory -Path $_ -Force | Out-Null
}

Write-Host "✓ All directories created"
```

---

## JAVA/KOTLIN SOURCE STRUCTURE (DAY 2 - TUESDAY)

```
android_skeleton/app/src/main/java/com/visualonda/sensory/
│
├── MainActivity.kt  ← REEMPLAZAR COMPLETAMENTE
│
├── domain/  ← CREAR
│   ├── model/  ← CREAR
│   │   ├── ControlFrame.kt  ← NUEVA
│   │   ├── ControlCell.kt  ← NUEVA
│   │   └── AudioParameters.kt  ← NUEVA
│   │
│   ├── repository/  ← CREAR
│   │   ├── ICameraRepository.kt  ← NUEVA
│   │   ├── IAudioRepository.kt  ← NUEVA
│   │   └── ISettingsRepository.kt  ← NUEVA
│   │
│   └── usecase/  ← CREAR
│       ├── ProcessFrameUseCase.kt  ← NUEVA
│       └── GenerateAudioUseCase.kt  ← NUEVA
│
├── data/  ← CREAR
│   ├── repository/  ← CREAR
│   │   ├── CameraRepositoryImpl.kt  ← NUEVA
│   │   ├── AudioRepositoryImpl.kt  ← NUEVA
│   │   └── SettingsRepositoryImpl.kt  ← NUEVA
│   │
│   ├── datasource/  ← CREAR
│   │   └── local/  ← CREAR
│   │       └── PreferenceDataSource.kt  ← NUEVA
│   │
│   └── db/  ← CREAR (vacío por ahora)
│
├── ui/  ← CREAR
│   ├── viewmodel/  ← CREAR
│   │   ├── CameraViewModel.kt  ← NUEVA
│   │   └── AudioViewModel.kt  ← NUEVA
│   │
│   ├── activity/  ← CREAR
│   │   └── MainActivity.kt  (same as root MainActivity)
│   │
│   ├── fragment/  ← CREAR (vacío por ahora)
│   │
│   └── view/  ← CREAR (vacío por ahora)
│
├── di/  ← CREAR (CRÍTICO)
│   ├── AppModule.kt  ← NUEVA
│   ├── RepositoryModule.kt  ← NUEVA
│   ├── UseCaseModule.kt  ← NUEVA
│   └── DataModule.kt  ← NUEVA
│
├── util/  ← CREAR
│   ├── Constants.kt  ← NUEVA
│   └── Extensions.kt  ← NUEVA
│
└── accessibility/  ← CREAR (vacío por ahora - Fase 2)
```

**Commands to create (PowerShell):**

```powershell
$basePath = "android_skeleton\app\src\main\java\com\visualonda\sensory"

@(
    "$basePath\domain\model",
    "$basePath\domain\repository",
    "$basePath\domain\usecase",
    "$basePath\data\repository",
    "$basePath\data\datasource\local",
    "$basePath\data\db",
    "$basePath\ui\viewmodel",
    "$basePath\ui\activity",
    "$basePath\ui\fragment",
    "$basePath\ui\view",
    "$basePath\di",
    "$basePath\util",
    "$basePath\accessibility"
) | ForEach-Object { 
    New-Item -ItemType Directory -Path $_ -Force | Out-Null
}

Write-Host "✓ All Kotlin directories created"
```

---

## C++ SOURCES (WEEK 1 ONWARDS)

**These directories are for Week 1-3, but create NOW to avoid confusion:**

```
android_skeleton/app/src/main/cpp/
│
├── libpd_wrapper.cpp  ← WEEK 1
├── libpd_wrapper.h    ← WEEK 1
│
├── audio_engine.cpp   ← WEEK 2
├── audio_engine.h     ← WEEK 2
│
├── frame_processor.cpp  ← WEEK 3
├── frame_processor.h    ← WEEK 3
│
├── mapping_engine.cpp   ← EXISTS (EDIT Week 1)
├── mapping_engine.h     ← EXISTS
│
├── json_parser.cpp      ← WEEK 3
├── json_parser.h        ← WEEK 3
│
├── jni_bridge.h         ← WEEK 1
│
└── native-lib.cpp       ← EDIT (exists)
```

**Optional to create structure now (or create files as needed in Week 1):**

```powershell
# Create directories (optional now, required by Week 1)
$cppPath = "android_skeleton\app\src\main\cpp"

@(
    "$cppPath\audio",
    "$cppPath\vision",
    "$cppPath\dsp",
    "$cppPath\jni"
) | ForEach-Object { 
    New-Item -ItemType Directory -Path $_ -Force | Out-Null
}

Write-Host "✓ C++ directories created"
```

---

## TEST DIRECTORIES (OPTIONAL - SETUP NOW)

```
android_skeleton/app/src/
├── test/  (unit tests)
│   └── java/com/visualonda/sensory/
│       ├── unit/
│       │   ├── domain/
│       │   │   └── usecase/
│       │   ├── data/
│       │   │   └── repository/
│       │   └── util/
│       │
│       └── integration/
│
└── androidTest/  (instrumented tests)
    └── java/com/visualonda/sensory/
        ├── ui/
        └── performance/
```

**Commands:**

```powershell
$testPath = "android_skeleton\app\src\test\java\com\visualonda\sensory"

@(
    "$testPath\unit\domain\usecase",
    "$testPath\unit\data\repository",
    "$testPath\unit\util",
    "$testPath\integration"
) | ForEach-Object { 
    New-Item -ItemType Directory -Path $_ -Force | Out-Null
}

$androidTestPath = "android_skeleton\app\src\androidTest\java\com\visualonda\sensory"

@(
    "$androidTestPath\ui",
    "$androidTestPath\performance"
) | ForEach-Object { 
    New-Item -ItemType Directory -Path $_ -Force | Out-Null
}

Write-Host "✓ Test directories created"
```

---

## RESOURCE DIRECTORIES (ANDROID RESOURCES)

```
android_skeleton/app/src/main/
├── res/  (exists)
│   ├── layout/
│   │   ├── activity_main.xml  (exists)
│   │   └── activity_camera.xml  ← CREAR Week 3
│   │
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── dimens.xml  ← CREAR
│   │   └── styles.xml
│   │
│   ├── values-night/
│   │   └── colors.xml
│   │
│   └── drawable/  (vacío por ahora)
│
├── assets/  (exists)
│   ├── patches/
│   │   └── light_material_patch.pd  ← COPIAR aquí (Monday)
│   │
│   └── config/
│       └── (vacío por ahora)
│
└── AndroidManifest.xml  (exists)
```

---

## BUILD & CONFIG FILES

```
android_skeleton/
├── build.gradle  ← ACTUALIZAR (proyecto)
├── settings.gradle  (exists)
├── gradle.properties  (puede existir)
├── gradlew  (exists)
├── gradlew.bat  (exists)
│
├── app/
│   ├── build.gradle  ← ACTUALIZAR (app)
│   ├── CMakeLists.txt  ← ACTUALIZAR (C++ build)
│   ├── proguard-rules.pro  (opcional)
│   │
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml  (exists)
│           ├── res/
│           ├── assets/
│           ├── java/
│           └── cpp/
│
└── .gradle/  (auto-generated - ignore)
```

---

## COMPLETE FOLDER CREATION SCRIPT

**Run this PowerShell script to create everything at once:**

```powershell
# ============================================================
# VISUALONDA FOLDER STRUCTURE CREATION
# ============================================================

Write-Host "Creating Visualonda folder structure..." -ForegroundColor Green

# Main paths
$basePath = "android_skeleton\app\src"
$javaPath = "$basePath\main\java\com\visualonda\sensory"
$cppPath = "$basePath\main\cpp"
$assetsPath = "$basePath\main\assets"

# All directories to create
$dirs = @(
    # Native libraries & headers
    "$basePath\main\jniLibs\arm64-v8a",
    "$cppPath\libpd\include",
    
    # Assets
    "$assetsPath\patches",
    "$assetsPath\config",
    
    # Java/Kotlin - Domain Layer
    "$javaPath\domain\model",
    "$javaPath\domain\repository",
    "$javaPath\domain\usecase",
    
    # Java/Kotlin - Data Layer
    "$javaPath\data\repository",
    "$javaPath\data\datasource\local",
    "$javaPath\data\db",
    
    # Java/Kotlin - Presentation Layer
    "$javaPath\ui\viewmodel",
    "$javaPath\ui\activity",
    "$javaPath\ui\fragment",
    "$javaPath\ui\view",
    
    # Java/Kotlin - DI & Utils
    "$javaPath\di",
    "$javaPath\util",
    "$javaPath\accessibility",
    
    # C++ sources (optional - for organization)
    "$cppPath\audio",
    "$cppPath\vision",
    "$cppPath\dsp",
    "$cppPath\jni",
    
    # Tests (unit)
    "$basePath\test\java\com\visualonda\sensory\unit\domain\usecase",
    "$basePath\test\java\com\visualonda\sensory\unit\data\repository",
    "$basePath\test\java\com\visualonda\sensory\unit\util",
    "$basePath\test\java\com\visualonda\sensory\integration",
    
    # Tests (instrumented)
    "$basePath\androidTest\java\com\visualonda\sensory\ui",
    "$basePath\androidTest\java\com\visualonda\sensory\performance"
)

# Create all directories
foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Path $dir -Force | Out-Null
    Write-Host "✓ $dir"
}

Write-Host ""
Write-Host "✓✓✓ ALL DIRECTORIES CREATED SUCCESSFULLY ✓✓✓" -ForegroundColor Green
Write-Host ""
Write-Host "Next step: Copy libpd.so + headers to:"
Write-Host "  - jniLibs/arm64-v8a/libpd.so"
Write-Host "  - cpp/libpd/include/{libpd.h, pd.h, m_pd.h}"
Write-Host ""
```

**Save as:** `create_structure.ps1`

**Run:**
```powershell
.\create_structure.ps1
```

---

## VERIFICATION

**After creating all folders, verify with:**

```powershell
# Check if all important directories exist
$checks = @(
    "android_skeleton\app\src\main\jniLibs\arm64-v8a",
    "android_skeleton\app\src\main\cpp\libpd\include",
    "android_skeleton\app\src\main\java\com\visualonda\sensory\domain\model",
    "android_skeleton\app\src\main\java\com\visualonda\sensory\data\repository",
    "android_skeleton\app\src\main\java\com\visualonda\sensory\ui\viewmodel",
    "android_skeleton\app\src\main\java\com\visualonda\sensory\di"
)

foreach ($check in $checks) {
    if (Test-Path $check) {
        Write-Host "✓ $check"
    } else {
        Write-Host "✗ MISSING: $check" -ForegroundColor Red
    }
}
```

---

## FILE COUNT SUMMARY

After Fase 0 complete, you should have:

```
New Kotlin files: ~20 files
New C++ files: 0 files (done in Week 1-2)
Modified files: 3 files (build.gradle, CMakeLists.txt, MainActivity.kt)
Copied files: 4 files (libpd.so + 3 headers)

Total: ~27 new items
Build configuration: 2 files updated
Code: Clean MVVM architecture ready for Week 1
```

---

## DOCUMENTO: FOLDER_STRUCTURE_REQUIRED.md

**Propósito:** Visual guide para estructura de carpetas
**Timeline:** Crear Monday + Tuesday
**Verificar:** Wednesday antes de build
**Si no se crea:** Build fallará debido a paths incorrectos

