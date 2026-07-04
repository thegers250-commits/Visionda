# 🚀 TAREA #1 - COMIENZA AHORA

**Status:** Listo para ejecutar  
**Duración:** 30-60 minutos  
**Objetivo:** Verificar que Fase 0 funciona en tu teléfono

---

## ⏱️ CHECKLIST 30 MINUTOS

### PASO 1: Ver GitHub Actions (2 minutos)

```
1. Abre navegador
2. Ve a: https://github.com/thegers250-commits/Visionda/actions
3. Busca workflow "Build Android APK" más reciente
4. ¿Ves 🟢 VERDE?
   └─ SÍ → Ir a PASO 2
   └─ NO (🟡 amarillo):
       ├─ Espera 5 minutos más
       └─ Si sigue 🟡: Refresh página (F5)
   └─ NO (🔴 rojo):
       ├─ Raro pero posible
       ├─ Click para ver logs
       └─ Probablemente error trivial
```

### PASO 2: Descargar APK (5 minutos)

```
1. Click en workflow 🟢 más reciente
2. Scroll down → Busca "Artifacts"
3. Verás: "app-debug" (es un zip)
4. Click para descargar
5. Espera a que termine (5-10 MB)
6. Archivo: C:\Users\[TuUser]\Downloads\app-debug.zip
```

### PASO 3: Extraer APK (2 minutos)

```powershell
# Abrir PowerShell

# Ir a Descargas
cd $env:USERPROFILE\Downloads

# Extraer zip
Expand-Archive -Path app-debug.zip -DestinationPath app-debug

# Verificar
ls app-debug
# Deberías ver: app-debug.apk (~5 MB)
```

### PASO 4: Instalar en teléfono (5 minutos)

```powershell
# Conectar teléfono por USB
# (En teléfono: Settings → Developer Options → USB Debugging = ON)

# Verificar que teléfono se ve
adb devices
# Debería mostrar tu teléfono listado

# Instalar app
adb install "$env:USERPROFILE\Downloads\app-debug\app-debug.apk"

# Espera mensaje: "Success"
```

**Si falla:**
```powershell
# Desinstalar versión anterior (si existe)
adb uninstall com.visualonda.sensory

# Intentar instalar nuevamente
adb install "$env:USERPROFILE\Downloads\app-debug\app-debug.apk"
```

### PASO 5: Abrir app (3 minutos)

```
OPCIÓN A (Desde teléfono - más fácil):
1. Home
2. Buscar "Visualonda"
3. Tap

OPCIÓN B (Desde PowerShell):
adb shell am start -n com.visualonda.sensory/.MainActivity
```

### PASO 6: Probar (5 minutos)

```
Deberías ver pantalla con 3 botones:
├─ Init PD
├─ Load Patch  
├─ Send Float (1000 Hz)
└─ (Plus status log area)

PRUEBAS:
1. Tap "Init PD"
   └─ ¿App crashea? NO → ✅
   └─ ¿Ves log "pdInit completed"? SÍ → ✅

2. Tap "Load Patch"
   └─ ¿App crashea? NO → ✅
   └─ ¿Ves log "Patch loaded"? SÍ → ✅

3. Tap "Send Float"
   └─ ¿App crashea? NO → ✅
   └─ ¿Ves log "Float sent"? SÍ → ✅

RESULTADO:
Si ✅ TODO OK:
   → FASE 0 VERIFICADA ✅
   → Listo para Fase 1
   
Si ❌ ALGO FALLÓ:
   → Ver troubleshooting abajo
```

---

## 🔧 TROUBLESHOOTING RÁPIDO

### GitHub Actions Rojo 🔴
```
Problema: Workflow muestra error
Causa: Probablemente error de compilación
Solución:
1. Click en workflow
2. Click "Build APK Debug"
3. Scroll para ver error específico
4. Contactar si no puedes resolverlo
```

### APK no descarga
```
Problema: Artifacts no aparecer
Causa: GitHub Actions aún compilando
Solución:
1. Espera 2-3 minutos más
2. Refresh página (F5)
3. Si sigue: Workflow puede tener error
```

### APK no instala
```
Problema: "INSTALL_FAILED_..."
Causa: Versión anterior + conflicto
Solución:
adb uninstall com.visualonda.sensory
adb install app-debug.apk
```

### App crashea al abrir
```
Problema: App se cierra inmediatamente
Causa: Permiso o biblioteca nativa
Solución:
adb logcat | grep -i visualonda
# Ver error específico en logcat
```

### App abre pero botones no funcionan
```
Problema: Tap botón → nada pasa
Causa: JNI library no cargó
Solución:
adb logcat | grep -i "native"
# Buscar "UnsatisfiedLinkError"
```

---

## 📊 MÉTRICA ÉXITO

```
FASE 0 VERIFICADA = ✅ Si:

☐ APK descargó sin problemas
☐ APK instaló exitosamente  
☐ App abre sin crash
☐ 3 botones visibles
☐ "Init PD" tapa → no crash
☐ "Load Patch" tapa → no crash
☐ "Send Float" tapa → no crash
☐ Logs aparecen en pantalla

RESULTADO: 0/8 fallos → ✅ ÉXITO
```

---

## ✅ CUANDO TERMINES

Si TODO funciona ✅:

```
1. Toma screenshot
2. Documenta resultado:
   - Fecha/Hora
   - Teléfono modelo
   - 0 crashes total
   - 3 botones funcionales

3. Commits a GitHub:
   git add .
   git commit -m "Fase 0 verification: Passed on [device]"
   git push

4. SIGUIENTE: Semana 1 comienza LUNES
   ├─ Descargar LibPD
   ├─ Crear libpd_wrapper.cpp
   ├─ Y así... (ver SEMANA_1_PLAN_DETALLADO.md)
```

---

## 🎓 RESUMEN

```
TAREA #1 = Verificar Fase 0 en tu teléfono

Duración: 30 minutos
Pasos:
1. GitHub Actions: Ver 🟢 verde
2. Descargar APK
3. Instalar: adb install
4. Abrir app
5. Probar 3 botones
6. ✅ ÉXITO

Resultado Esperado:
├─ App abre
├─ 3 botones responden
├─ 0 crashes
└─ Logcat limpio

Siguiente: Semana 1 (Monday)
```

---

## 🚀 ¡COMIENZA!

**Haz ESTO AHORA:**

1. Abre navegador → GitHub Actions
2. Descarga APK cuando esté 🟢 
3. Instala en teléfono
4. Prueba
5. Reporta resultado

**¡Vamos!** 🎉

---

**Creado:** Julio 4, 2026  
**Acción:** AHORA (30 minutos)  
**Siguiente:** Semana 1 (Monday)

