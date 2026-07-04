# 🚀 INSTRUCCIONES - QUÉ HACER AHORA

**El código ya está aplicado. Solo necesitas 3 cosas.**

---

## ✅ LO QUE YA SE HIZO

Tu proyecto ya tiene:
- ✅ build.gradle actualizado (todas las dependencias)
- ✅ CMakeLists.txt actualizado
- ✅ MainActivity.kt refactorizado con Hilt
- ✅ Arquitectura MVVM completa
- ✅ 11 nuevos archivos Kotlin en su lugar

**Carpeta:** `f:\Programas de  github\Visualonda\android_skeleton\`

---

## 🔴 LO QUE FALTA (3 COSAS)

### **COSA 1: DESCARGAR LIBPD (30 min)**

**Por qué:** Motor de audio. Archivo binario que no se puede crear, solo descargar.

**Pasos:**

1. **Ir a GitHub:**
   ```
   https://github.com/libpd/libpd/releases
   ```

2. **Descargar:**
   - Busca: `libpd-0.12.x-android.zip`
   - Descarga la versión más reciente
   - Extrae a: `C:\temp\libpd-android` (o donde sea)

3. **Copiar libpd.so (1.2 MB):**
   ```
   Desde:  C:\temp\libpd-android\android\arm64-v8a\libpd.so
   Hacia:  f:\Programas de  github\Visualonda\android_skeleton\app\src\main\jniLibs\arm64-v8a\libpd.so
   ```
   
   Si la carpeta `jniLibs` no existe, créala.

4. **Copiar headers (3 archivos):**
   ```
   Desde:  C:\temp\libpd-android\pure-data\src\
   Hacia:  f:\Programas de  github\Visualonda\android_skeleton\app\src\main\cpp\libpd\include\
   
   Archivos a copiar:
     - libpd.h
     - pd.h
     - m_pd.h
   ```

5. **Copiar patch:**
   ```
   Desde:  f:\Programas de  github\Visualonda\sensory-language\light_material_patch.pd
   Hacia:  f:\Programas de  github\Visualonda\android_skeleton\app\src\main\assets\patches\light_material_patch.pd
   ```
   
   Si la carpeta `patches` no existe, créala.

**Verificar:**
```
Abre Explorer y comprueba que existan:
  ✓ android_skeleton/app/src/main/jniLibs/arm64-v8a/libpd.so (1.2 MB)
  ✓ android_skeleton/app/src/main/cpp/libpd/include/libpd.h
  ✓ android_skeleton/app/src/main/cpp/libpd/include/pd.h
  ✓ android_skeleton/app/src/main/cpp/libpd/include/m_pd.h
  ✓ android_skeleton/app/src/main/assets/patches/light_material_patch.pd
```

**Si todo OK:** Cosa 1 lista ✓

---

### **COSA 2: COMPILAR (30 min)**

**Por qué:** Verificar que todo el código está bien escrito.

**Pasos:**

1. **Abre CMD o PowerShell**

2. **Navega al proyecto:**
   ```
   cd f:\Programas de  github\Visualonda\android_skeleton
   ```

3. **Compilar:**
   ```bash
   gradlew clean build
   ```
   
   O si gradlew no funciona:
   ```bash
   ./gradlew.bat clean build
   ```

4. **Espera a que termine...**
   - Si todo OK: `BUILD SUCCESSFUL`
   - Si falla: Ve el error y búscalo en Google

**Errores comunes:**

- **"Cannot find libpd.so"**
  - Verifica que existe en: `app/src/main/jniLibs/arm64-v8a/libpd.so`

- **"Hilt not found"**
  - Verifica build.gradle tiene: `kapt 'com.google.dagger:hilt-compiler:2.48'`

- **"Kotlin error"**
  - Verifica tengas Kotlin installed en Android Studio

**Si BUILD SUCCESSFUL:** Cosa 2 lista ✓

---

### **COSA 3: PROBAR EN TELÉFONO (30 min)**

**Por qué:** Asegurar que la app funciona de verdad.

**Pasos:**

1. **Conecta teléfono Android a PC (USB)**

2. **Ejecuta:**
   ```bash
   ./gradlew installDebug
   ```

3. **Abre la app en tu teléfono**

4. **Verifica:**
   - ✓ App abre sin crash
   - ✓ Ves 3 botones
   - ✓ Presiona "Init Audio Engine" → muestra "Audio initialized"
   - ✓ Presiona "Init PD (Week 1)" → muestra "PD initialized"
   - ✓ Presiona "Start Camera (Week 3)" → muestra "Camera started"

5. **Si todo funciona:**
   - ✅ FASE 0 COMPLETA
   - ✅ Listo para Phase 1 Week 1

**Si crashea:**
- Ve logcat: `adb logcat | grep -i crash`
- Busca el error en Google
- Piensa: ¿Faltan binarios libpd?

**Si BUILD SUCCESSFUL + APP ABRE SIN CRASH:** Cosa 3 lista ✓

---

## ✅ LISTA DE VERIFICACIÓN

```
COSA 1 - Descargar libpd:
  [ ] Descargado libpd-0.12.x-android.zip
  [ ] Extraído a temp folder
  [ ] Copiado libpd.so a jniLibs/arm64-v8a/
  [ ] Copiados 3 headers a cpp/libpd/include/
  [ ] Copiado patch a assets/patches/

COSA 2 - Compilar:
  [ ] Ejecutado: ./gradlew clean build
  [ ] Resultado: BUILD SUCCESSFUL

COSA 3 - Probar:
  [ ] Ejecutado: ./gradlew installDebug
  [ ] App abre sin crash
  [ ] 3 botones visibles
  [ ] Botones no crashing
```

**Si TODOS los checkboxes están ✓:**
→ FASE 0 COMPLETA 🎉

---

## 🎯 DURACIÓN TOTAL

```
Cosa 1 (descargar + copiar):  30 min
Cosa 2 (compilar):           30 min
Cosa 3 (instalar + probar):  30 min

TOTAL:                       90 minutos (1.5 horas)
```

---

## 🚀 DESPUÉS DE FASE 0

**Si Phase 0 está completa (app compila y abre):**

```
JUEVES COMIENZA PHASE 1 WEEK 1:

Audio Engineer:
  - Crea: libpd_wrapper.cpp (C++ wrapper para libpd)
  - Resultado: Audio funcional

Android Developer:
  - Integra código C++ con JNI
  - Resultado: Botones llaman código C++

Estimado:
  - Semana 1: 40 horas
  - Resultado: Audio real (escuchas sonido en auriculares)
```

---

## 💡 NOTAS IMPORTANTES

1. **gradlew:** Si no funciona `./gradlew`, intenta `gradlew.bat`
2. **Android Studio:** Asegúrate que tienes SDK 34 instalado
3. **Teléfono:** Necesita Android 7.0+ (API 24+)
4. **USB:** Habilita "USB Debugging" en teléfono

---

## ❓ SI ALGO FALLA

**Responde esto:**
1. ¿En qué paso falla?
2. ¿Cuál es el error exacto?
3. ¿Dónde lo ves?

Luego:
1. Copia el error
2. Búscalo en Google
3. Lee las primeras 3 respuestas de Stack Overflow
4. Intenta la solución

Si nada funciona:
- Envía screenshot del error
- Y responde las 3 preguntas

---

## 📞 ESTOY AQUÍ SI LO NECESITAS

Cuando hayas:
1. Descargado libpd
2. Compilado exitosamente
3. Instalado en teléfono

**Escríbeme:** "FASE 0 COMPLETA"

Y comenzamos Phase 1 Week 1.

---

**DOCUMENTO:** INSTRUCCIONES_SIGUIENTES.md
**ACCIÓN:** Haz las 3 cosas arriba
**TIEMPO:** 1.5 horas
**RESULTADO:** App lista para Phase 1

