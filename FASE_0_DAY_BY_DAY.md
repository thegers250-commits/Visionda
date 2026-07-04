# 📅 FASE 0: DAY-BY-DAY EXECUTION PLAN

**Duration:** 3-5 days (Starting immediately)
**Team:** Tech Lead + 1 Android Engineer
**Objective:** Project compiles with libpd.so, ready for Fase 1 Week 1

---

## 🎯 SUCCESS CRITERIA (Must achieve ALL)

```
✅ libpd.so @ app/src/main/jniLibs/arm64-v8a/ (file exists, ~1.2 MB)
✅ 3 headers @ app/src/main/cpp/libpd/include/ (libpd.h, pd.h, m_pd.h)
✅ CMakeLists.txt includes libpd config (no stubs in build)
✅ build.gradle has NDK + CameraX (compilation ready)
✅ ./gradlew build → BUILD SUCCESSFUL (0 errors)
✅ app-debug.apk generated (installs on device)
✅ App starts without crash (no UnsatisfiedLinkError)
✅ Logcat clean (no ERROR level messages on startup)
```

---

## 📍 DAY 1: MORNING (2-3 hours)

### 🎯 Goal for Day 1 Morning
Get libpd binaries downloaded and validated

### Task 1.1: Download LibPD Release

**Time:** 15 minutes

```bash
# 1. Open browser (Chrome, Firefox, Edge, etc)
# 2. Go to:
https://github.com/libpd/libpd/releases

# 3. Look for latest release with "android"
# Example: libpd-0.12.1-android
# Click to expand if needed

# 4. Download the ZIP file
# Look for: libpd-X.XX-X-android.zip
# File size: ~2-5 MB
# Location: Save to C:\Temp\ (or your temp folder)
```

**Verification:**
```bash
# In File Explorer, verify file exists:
C:\Temp\libpd-0.12.1-android.zip (or similar name)

# Size should be 2-5 MB
```

✅ **Checkmark:** File downloaded

---

### Task 1.2: Extract LibPD Archive

**Time:** 10 minutes

```bash
# 1. Open PowerShell as Administrator

# 2. Navigate to Temp:
cd C:\Temp

# 3. Extract:
Expand-Archive -Path "libpd-0.12.1-android.zip" -DestinationPath "."

# Result: Creates "libpd-0.12.1" folder (or similar version)

# 4. Verify extraction:
dir libpd-0.12.1\

# Should show: android/, pure-data/, README, etc
```

**Verification:**
```bash
# Check key folders exist:
dir libpd-0.12.1\android\arm64-v8a\

# Should show: libpd.so and other files

dir libpd-0.12.1\pure-data\src\

# Should show: libpd.h, pd.h, m_pd.h, etc
```

✅ **Checkmark:** LibPD extracted successfully

---

### Task 1.3: Validate libpd.so Binary

**Time:** 5 minutes

```bash
# 1. Check file size and type:
dir "C:\Temp\libpd-0.12.1\android\arm64-v8a\libpd.so"

# Expected: ~1.2 MB

# 2. If available, verify architecture (optional):
file "C:\Temp\libpd-0.12.1\android\arm64-v8a\libpd.so"

# Expected: ELF 64-bit (if using WSL/Unix tools)
```

**If successful:** ✅
**If failed:** Check MD5 hash on GitHub release page

✅ **Checkmark:** libpd.so validated

---

### Task 1.4: Validate Headers

**Time:** 5 minutes

```bash
# 1. List headers:
dir "C:\Temp\libpd-0.12.1\pure-data\src\*.h" | head -20

# 2. Specifically check for required headers:
dir "C:\Temp\libpd-0.12.1\pure-data\src\libpd.h"
dir "C:\Temp\libpd-0.12.1\pure-data\src\pd.h"
dir "C:\Temp\libpd-0.12.1\pure-data\src\m_pd.h"

# All 3 should exist
```

**Verification:**
```bash
# Should see:
libpd.h    - ~50 KB
pd.h       - ~80 KB
m_pd.h     - ~100 KB
```

✅ **Checkmark:** All headers found

---

## 📍 DAY 1: AFTERNOON (2-3 hours)

### 🎯 Goal for Day 1 Afternoon
Copy binaries and headers to project directories

### Task 1.5: Create Project Directories

**Time:** 10 minutes

```bash
# 1. Navigate to project:
cd "f:\Programas de  github\Visualonda\android_skeleton"

# 2. Create jniLibs directory:
mkdir -p "app\src\main\jniLibs\arm64-v8a"

# 3. Create cpp includes directory:
mkdir -p "app\src\main\cpp\libpd\include"

# 4. Create assets/patches directory:
mkdir -p "app\src\main\assets\patches"

# 5. Verify all created:
dir "app\src\main\jniLibs\arm64-v8a"
dir "app\src\main\cpp\libpd\include"
dir "app\src\main\assets\patches"

# Each should be empty (newly created)
```

✅ **Checkmark:** Directories created

---

### Task 1.6: Copy libpd.so to Project

**Time:** 5 minutes

```bash
# 1. Still in project directory:
cd "f:\Programas de  github\Visualonda\android_skeleton"

# 2. Copy libpd.so:
copy "C:\Temp\libpd-0.12.1\android\arm64-v8a\libpd.so" `
      "app\src\main\jniLibs\arm64-v8a\libpd.so"

# 3. Verify copy:
dir "app\src\main\jniLibs\arm64-v8a\libpd.so"

# Should show file ~1.2 MB
```

✅ **Checkmark:** libpd.so copied

---

### Task 1.7: Copy Headers to Project

**Time:** 5 minutes

```bash
# 1. Copy 3 headers:
copy "C:\Temp\libpd-0.12.1\pure-data\src\libpd.h" `
      "app\src\main\cpp\libpd\include\libpd.h"

copy "C:\Temp\libpd-0.12.1\pure-data\src\pd.h" `
      "app\src\main\cpp\libpd\include\pd.h"

copy "C:\Temp\libpd-0.12.1\pure-data\src\m_pd.h" `
      "app\src\main\cpp\libpd\include\m_pd.h"

# 2. Verify all 3 copied:
dir "app\src\main\cpp\libpd\include\*.h"

# Should show 3 files
```

✅ **Checkmark:** Headers copied

---

### Task 1.8: Copy Pure Data Patch

**Time:** 5 minutes

```bash
# 1. Copy patch file:
copy "sensory-language\light_material_patch.pd" `
      "app\src\main\assets\patches\light_material_patch.pd"

# 2. Verify:
dir "app\src\main\assets\patches\*.pd"

# Should show 1 file
```

✅ **Checkmark:** Patch copied

---

### Task 1.9: Create Backup Archive

**Time:** 5 minutes

```bash
# 1. Create backup of freshly copied files:
Compress-Archive -Path "app\src\main\jniLibs", `
                       "app\src\main\cpp\libpd", `
                       "app\src\main\assets\patches" `
                 -DestinationPath "backup_fase0_$(Get-Date -Format 'yyyyMMdd_HHmm').zip"

# This saves a backup in case something goes wrong
```

✅ **Checkmark:** Backup created

---

## 📍 DAY 2: MORNING (2-3 hours)

### 🎯 Goal for Day 2 Morning
Update CMakeLists.txt with libpd configuration

### Task 2.1: Read Current CMakeLists.txt

**Time:** 10 minutes

```bash
# 1. Open file in text editor (VS Code, Notepad++, etc):
"f:\Programas de  github\Visualonda\android_skeleton\app\CMakeLists.txt"

# 2. Note what's currently there (probably minimal)
# 3. Screenshot current state for reference
```

✅ **Checkmark:** Current CMakeLists.txt reviewed

---

### Task 2.2: Replace CMakeLists.txt

**Time:** 15 minutes

**Option A: Replace entire file**

```bash
# 1. Delete current:
Remove-Item "app\CMakeLists.txt"

# 2. Create new from template (see below)
```

**Option B: Edit existing (safer)**

```bash
# 1. Open in VS Code
# 2. Select all content (Ctrl+A)
# 3. Delete
# 4. Paste new content below
```

**New CMakeLists.txt content:**

```cmake
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
# Link LibPD (pre-built binary)
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
# Final Linking
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

**Save file as:** `app/CMakeLists.txt`

✅ **Checkmark:** CMakeLists.txt updated

---

### Task 2.3: Verify CMakeLists.txt

**Time:** 5 minutes

```bash
# 1. Open file to verify content:
Get-Content "app\CMakeLists.txt" | head -20

# Should show: "cmake_minimum_required"
#              "project("native_lib")"
#              "LIBPD_INCLUDE_DIR"

# 2. Check for "include_directories" line:
Get-Content "app\CMakeLists.txt" | Select-String "include_directories"

# Should find it

# 3. Check for "libpd" references:
Get-Content "app\CMakeLists.txt" | Select-String "libpd"

# Should find multiple matches
```

✅ **Checkmark:** CMakeLists.txt verified

---

## 📍 DAY 2: AFTERNOON (2-3 hours)

### 🎯 Goal for Day 2 Afternoon
Update build.gradle with NDK and CameraX

### Task 2.4: Edit build.gradle (App-level)

**Time:** 20 minutes

**Location:** `app/build.gradle`

**Find this section:**
```gradle
android {
    compileSdk 34
    defaultConfig {
        applicationId "com.visualonda.sensory"
        minSdk 28
        targetSdk 34
        versionCode 1
        versionName "1.0"
```

**ADD after `versionName` line:**
```gradle
        ndk {
            abiFilters 'arm64-v8a'
        }

        externalNativeBuild {
            cmake {
                cppFlags "-fexceptions -frtti"
                cFlags "-fexceptions"
            }
        }
```

**Find section `externalNativeBuild { }` and ENSURE it contains:**
```gradle
externalNativeBuild {
    cmake {
        path "CMakeLists.txt"
        version "3.22.1"
    }
}
```

**Find section `dependencies { }` and ADD:**
```gradle
    // CameraX for camera capture
    implementation 'androidx.camera:camera-core:1.2.3'
    implementation 'androidx.camera:camera-camera2:1.2.3'
    implementation 'androidx.camera:camera-lifecycle:1.2.3'
    
    // UI
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
```

✅ **Checkmark:** build.gradle updated

---

### Task 2.5: Verify build.gradle Syntax

**Time:** 10 minutes

```bash
# 1. Open build.gradle file
# 2. Look for syntax issues (red squiggly lines in IDE)
# 3. Check for matching braces { }
# 4. No incomplete lines
```

**Common issues:**
- Missing comma after line
- Unclosed brace
- Wrong indentation (usually OK, but check)

✅ **Checkmark:** build.gradle syntax OK

---

## 📍 DAY 3: MORNING (2-3 hours)

### 🎯 Goal for Day 3 Morning
First compilation attempt

### Task 3.1: Sync Project

**Time:** 10 minutes

```bash
# 1. Open Android Studio (or command line)
# 2. If using Android Studio:
#    File → Sync Now
#    (Wait for gradle sync to complete)

# If using command line:
cd "f:\Programas de  github\Visualonda\android_skeleton"
./gradlew tasks

# Just to verify gradle works
```

✅ **Checkmark:** Gradle sync successful

---

### Task 3.2: Clean Build

**Time:** 15 minutes

```bash
# 1. In project directory:
cd "f:\Programas de  github\Visualonda\android_skeleton"

# 2. Clean (removes old build files):
./gradlew clean

# Expects output:
# > Task :app:clean
# BUILD SUCCESSFUL
```

**If this fails:**
- Check NDK installation (Android Studio → SDK Manager → SDK Tools → NDK)
- Check Java installation (java -version)
- Check JAVA_HOME environment variable

✅ **Checkmark:** Clean successful

---

### Task 3.3: Build Debug

**Time:** 30-60 minutes (first build takes longer)

```bash
# 1. Build:
./gradlew build

# First build takes 2-5 minutes
# Watch for:
# - "checking for libpd.so" message
# - Compiling native-lib.cpp
# - Linking phase

# Expected final output:
# BUILD SUCCESSFUL
```

**During build, watch for:**

```
✓ Downloading dependencies
✓ Checking libpd.so location
✓ Compiling C++
✓ Linking libraries
✓ Packaging APK
```

**Possible errors:**

| Error | Solution |
|-------|----------|
| "Cannot find libpd.so" | Check file exists: `dir app\src\main\jniLibs\arm64-v8a\libpd.so` |
| "libpd.h not found" | Check headers: `dir app\src\main\cpp\libpd\include\libpd.h` |
| "gradle not found" | Reinstall Android Studio or download Gradle manually |
| "NDK not found" | Android Studio → SDK Manager → SDK Tools → Install NDK 25.0+ |

✅ **Checkmark:** BUILD SUCCESSFUL

---

### Task 3.4: Locate APK

**Time:** 5 minutes

```bash
# 1. After successful build, find APK:
dir "app\build\outputs\apk\debug\app-debug.apk"

# Should show file, ~20-30 MB

# 2. Verify size reasonable:
# Too small (<5 MB) = problem
# Expected: 15-40 MB

# 3. Note timestamp - should be "now"
```

✅ **Checkmark:** APK generated

---

## 📍 DAY 3: AFTERNOON (1-2 hours)

### 🎯 Goal for Day 3 Afternoon
Install on device and verify no crashes

### Task 3.5: Connect Device

**Time:** 5 minutes

```bash
# 1. Connect Android phone via USB cable
# 2. On phone:
#    - Go to Settings
#    - About Phone
#    - Tap "Build Number" 7 times
#    - Back to Settings
#    - Developer Options
#    - USB Debugging → ON

# 3. On PC, verify connection:
cd "f:\Programas de  github\Visualonda\android_skeleton"
./gradlew devices

# Expected output:
# emulator-5554 offline/online
# (or device ID like 1234ABC123)
```

✅ **Checkmark:** Device connected

---

### Task 3.6: Install APK

**Time:** 5 minutes

```bash
# 1. Install:
./gradlew installDebug

# Expected output:
# Installing APK 'app-debug.apk' on '[device ID]'
# Installed 'app-debug.apk'
# BUILD SUCCESSFUL
```

**If stuck:**
- Try: `adb devices` to list
- Try: `adb uninstall com.visualonda.sensory` to remove old version
- Then: `./gradlew installDebug` again

✅ **Checkmark:** APK installed

---

### Task 3.7: Launch App

**Time:** 5 minutes

```bash
# 1. On phone, find app "Visualonda" or "sensory"
# 2. Tap to open
# 3. App might ask for permissions (camera, microphone)
# 4. Tap "Allow" for both

# Expected behavior:
# - App opens
# - No immediate crash
# - Screen shows (even if just blank/dark)

# DO NOT expect full functionality yet!
# (Only stubs are in place)
```

✅ **Checkmark:** App launches without crash

---

### Task 3.8: Check Logcat

**Time:** 10 minutes

```bash
# 1. Open terminal/powershell
# 2. View logs:
adb logcat | grep -i "visualonda\|native\|error"

# 3. Look for:
#    ✓ App starting messages
#    ✗ NOT any "UnsatisfiedLinkError"
#    ✗ NOT any "cannot load"
#    ✗ NOT any crashes

# 4. Sample good output:
# D: [native] LibPD initialized
# I: [native] Audio engine starting

# 5. Sample bad output (STOP if you see):
# E: java.lang.UnsatisfiedLinkError: cannot load library

# If BAD: Troubleshoot
```

✅ **Checkmark:** Logcat clean (no UnsatisfiedLinkError)

---

## 📍 DAY 4: MORNING (1-2 hours)

### 🎯 Goal for Day 4 Morning
Comprehensive verification

### Task 4.1: Build Summary Report

**Time:** 15 minutes

```bash
# 1. Re-run full build with logging:
./gradlew clean build > build_summary.txt 2>&1

# 2. Review output:
type build_summary.txt | Select-String "BUILD\|error\|warning" | head -20

# 3. Expected results:
#    BUILD SUCCESSFUL
#    0 ERRORS
#    <20 WARNINGS (OK - normal for Android)

# 4. Save report:
# Keep build_summary.txt as proof of success
```

✅ **Checkmark:** Build report created

---

### Task 4.2: Create Fase 0 Completion Document

**Time:** 15 minutes

Create file: `FASE_0_COMPLETION_RECORD.md`

```markdown
# FASE 0 COMPLETION RECORD

**Completion Date:** [TODAY]
**Team Members:** [NAMES]

## Deliverables Completed

### LibPD Setup
- [x] libpd.so downloaded (v0.12.1 or similar)
- [x] Located at: app/src/main/jniLibs/arm64-v8a/libpd.so
- [x] Size: ~1.2 MB
- [x] 3 headers copied: libpd.h, pd.h, m_pd.h

### Build Configuration
- [x] CMakeLists.txt created with libpd config
- [x] build.gradle updated with NDK filters
- [x] CameraX dependencies added
- [x] External native build configured

### Compilation
- [x] ./gradlew clean → SUCCESS
- [x] ./gradlew build → SUCCESS
- [x] Build time: [__] minutes
- [x] APK generated: app-debug.apk (~[__] MB)

### Installation
- [x] Device connected and recognized
- [x] APK installed successfully
- [x] App launches without crash
- [x] Logcat shows no UnsatisfiedLinkError

## Gate Verification

- [x] libpd.so present: YES
- [x] Headers present: YES (3/3)
- [x] CMakeLists.txt correct: YES
- [x] build.gradle correct: YES
- [x] BUILD SUCCESSFUL: YES
- [x] APK installs: YES
- [x] No crashes on launch: YES
- [x] Logcat clean: YES

## Result: ✅ FASE 0 COMPLETE

**Status:** READY FOR FASE 1
**Next Step:** Begin FASE 1 Week 1 on [DATE]
**Assigned To:** [NAMES]

## Notes
[Any issues encountered and how resolved]
```

✅ **Checkmark:** Completion record created

---

## 📍 DAY 4-5: OPTIONAL EXTENDED TESTING

### Task 4.3: Extended Stability Test (Optional)

**Time:** 30 minutes

```bash
# If you have extra time, do extended testing:

# 1. Reboot phone
# 2. Reinstall app: ./gradlew installDebug
# 3. Launch 3x, close 3x
# 4. Monitor logcat for leaks/crashes

# Expected: All clean, no issues
```

✅ **Checkmark:** Extended testing complete

---

## 🎯 PHASE 0 COMPLETION GATE

**Check ALL boxes:**

```
✅ libpd.so @ correct location, ~1.2 MB
✅ All 3 headers @ cpp/libpd/include/
✅ CMakeLists.txt has libpd config
✅ build.gradle has NDK + CameraX
✅ ./gradlew build = BUILD SUCCESSFUL (no errors)
✅ APK generated (15-40 MB)
✅ APK installs without error
✅ App launches, no crash
✅ Logcat has no UnsatisfiedLinkError
✅ Completion record created
✅ Backup created
```

**If ALL ✅:** PROCEED TO FASE 1
**If ANY ❌:** DEBUG before proceeding

---

## ⏰ TIMELINE SUMMARY

```
Day 1 Morning:   Download & validate libpd (2-3h)
Day 1 Afternoon: Copy binaries to project (2-3h)
Day 2 Morning:   Update CMakeLists.txt (2-3h)
Day 2 Afternoon: Update build.gradle (2-3h)
Day 3 Morning:   First compilation (2-3h)
Day 3 Afternoon: Install & verify (1-2h)
Day 4 Morning:   Final verification (1-2h)
Day 5:           Extended testing (optional)

TOTAL: 3-5 days depending on internet speed & compilation time
```

---

## 📞 TROUBLESHOOTING QUICK REFERENCE

| Problem | Check | Fix |
|---------|-------|-----|
| Build fails, "libpd.so not found" | `dir app\src\main\jniLibs\arm64-v8a\libpd.so` | Recopy libpd.so |
| Build fails, "libpd.h not found" | `dir app\src\main\cpp\libpd\include\libpd.h` | Recopy headers |
| App crashes on launch | `adb logcat \| grep ERROR` | Check logcat message |
| UnsatisfiedLinkError | `file libpd.so` | Verify is ARM64 binary |
| NDK not found | Android Studio → SDK Tools | Download NDK 25+ |
| Gradle not found | Reinstall Android Studio | Or download Gradle |

---

## ✅ READY TO START?

1. Print this document (or keep in browser)
2. Follow tasks in order
3. Checkmark each task as complete
4. Report any blockers immediately
5. **Complete within 3-5 days**

---

**Document:** FASE_0_DAY_BY_DAY.md
**Version:** 1.0 (Ready to Execute)
**Next:** START DAY 1 MORNING!

