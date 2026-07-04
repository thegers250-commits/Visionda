# ✅ DESPUÉS DE LA COMPILACIÓN

**Qué hacer cuando la compilación termine**

---

## ✅ SI VES: BUILD SUCCESSFUL

Eso significa:
```
✅ Compilación exitosa
✅ APK generado
✅ Listo para instalar en teléfono
```

---

## 📱 PASO 1: PREPARAR TELÉFONO

Asegúrate que tu teléfono Android tenga:

1. **USB Debugging activado:**
   - Ir a Settings → Developer options → USB Debugging
   - Si no ves Developer options, ir a About → Presionar Build Number 7 veces

2. **Conectar por USB:**
   - Conecta teléfono a PC por cable USB

3. **Permitir acceso:**
   - Debería aparecer un mensaje "Allow USB debugging?"
   - Presiona OK / Allow

---

## 🚀 PASO 2: INSTALAR EN TELÉFONO

Abre terminal en la carpeta del proyecto:

```bash
cd f:\Programas de  github\Visualonda\android_skeleton

# Instalar
./gradlew installDebug
```

O en PowerShell:

```powershell
cd 'f:\Programas de  github\Visualonda\android_skeleton'
cmd /c "gradlew.bat installDebug"
```

**Debería mostrar:**
```
Installing APK 'app-debug.apk'...
Installation successful
```

---

## 📲 PASO 3: ABRIR APP Y PROBAR

1. **Abre la app en tu teléfono**
   - Debería llamarse "Visualonda" o "Sensory"

2. **Deberías ver 3 botones:**
   - "Init Audio Engine"
   - "Init PD (Week 1)"
   - "Start Camera (Week 3)"

3. **Presiona cada botón:**
   - Cada uno debería mostrar un mensaje
   - NO debe crashear

4. **Si todo funciona:**
   - ✅ FASE 0 COMPLETA

---

## ❌ SI VES: BUILD FAILED

Significa que la compilación falló. Revisa el error:

### Errores comunes:

**Error: Cannot find libpd.so**
```
Solución: Está ok, es placeholder por ahora
Sigue adelante con instalación
```

**Error: SDK not found**
```
Solución: Instala Android SDK 34 en Android Studio
```

**Error: JAVA_HOME not set**
```
Solución: Instala Java JDK y configura JAVA_HOME
```

**Error: Gradle download failed**
```
Solución: Intenta de nuevo (problema de conexión)
./gradlew clean build
```

---

## 📂 DÓNDE ESTÁ EL APK

Después de compilar exitosamente:

```
f:\Programas de  github\Visualonda\android_skeleton\app\build\outputs\apk\debug\app-debug.apk
```

También puedes instalarlo manualmente:

```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

---

## 🎯 RESUMEN RÁPIDO

1. **Compilación termina**
2. **Ver BUILD SUCCESSFUL**
3. **Conectar teléfono**
4. **./gradlew installDebug**
5. **Abrir app en teléfono**
6. **Verificar 3 botones**
7. **Si todo ok → FASE 0 COMPLETA**

---

## 📞 SI ALGO FALLA EN INSTALACIÓN

### Problema: "Device not found"
```
Solución:
1. Revisa que esté conectado por USB
2. Activa USB Debugging en teléfono
3. Presiona Allow cuando aparezca
```

### Problema: "Timeout"
```
Solución: El teléfono se desconectó
1. Reconecta
2. Intenta de nuevo
```

### Problema: "App already installed"
```
Solución: La versión anterior existe
Ejecuta: ./gradlew installDebug -r
```

---

## ✨ CUANDO TERMINES CON ÉXITO

Escribe:
```
FASE 0 COMPLETA
```

Entonces:
```
🎉 Comienza Fase 1 Week 1
- Audio real (semana 1)
- Cámara real (semana 2-3)
- Accesibilidad (semana 5-8)
- ML (semana 9-12)
- Release (semana 13-16)
```

---

**DOCUMENTO:** DESPUÉS_COMPILACIÓN.md
**ACCIÓN:** Sigue los pasos 1-3
**RESULTADO:** App en tu teléfono

