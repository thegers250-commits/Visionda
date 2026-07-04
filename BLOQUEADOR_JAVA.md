# ❌ BLOQUEADOR: JAVA NO ESTÁ INSTALADO

**La compilación no puede ejecutarse sin Java**

---

## 🔴 EL PROBLEMA

```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH
```

Esto significa que **Java (JDK) no está instalado** en tu sistema.

---

## ✅ SOLUCIONES

### **OPCIÓN 1: Instalar Android Studio (RECOMENDADO)**

Android Studio incluye Java automáticamente.

**Pasos:**
1. Descargar Android Studio: https://developer.android.com/studio
2. Instalar (sigue las instrucciones)
3. Durante instalación, selecciona: "Install Android SDK" ✓
4. Android Studio instalará Java automáticamente

**Tiempo:** 30-60 minutos

---

### **OPCIÓN 2: Instalar Java JDK Solo**

Si prefieres solo Java sin Android Studio.

**Pasos:**
1. Ir a: https://www.oracle.com/java/technologies/downloads/
2. Descargar: **Java 17 LTS** (versión estable)
3. Instalar (.exe)
4. Siguiente, siguiente, siguiente, Finish

**Tiempo:** 10-15 minutos

**Luego reinicia PowerShell para que detecte Java**

---

### **OPCIÓN 3: Usar Java de Android Studio**

Si ya tienes Android Studio instalado pero no lo detectó.

**Pasos:**
1. Abre PowerShell
2. Ejecuta esto:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Android\jdk\jdk-17"
   ```
   (Ajusta la versión según tengas)
3. Verifica:
   ```powershell
   java -version
   ```
4. Si funciona, puedes continuar

---

## 📋 DESPUÉS DE INSTALAR JAVA

Una vez que instales Java:

1. **Reinicia PowerShell**
2. **Verifica que funciona:**
   ```powershell
   java -version
   ```
   Debería mostrar la versión de Java

3. **Vuelve a compilar:**
   ```bash
   cd f:\Programas de  github\Visualonda\android_skeleton
   ./gradlew clean build
   ```

---

## 🕐 ESTIMADO DE TIEMPO

```
Opción 1 (Android Studio): 30-60 min
Opción 2 (Java solo):      10-15 min
Opción 3 (Config):         2-5 min
```

---

## 🚀 PRÓXIMOS PASOS

1. **Elige opción 1 o 2** (Opción 1 es más fácil)
2. **Instala Java**
3. **Reinicia PowerShell**
4. **Vuelve a compilar:** `./gradlew clean build`

---

## 💡 RECOMENDACIÓN

**Usa OPCIÓN 1: Android Studio**

Razones:
- ✅ Todo incluido (Java + Android SDK + Tools)
- ✅ Fácil de instalar
- ✅ Lo necesitarás para testing y debugging después
- ✅ Interfaz gráfica para ver el proyecto

---

**DOCUMENTO:** BLOQUEADOR_JAVA.md
**PROBLEMA:** Java no instalado
**SOLUCIÓN:** Instalar Java (10-60 min)
**PRÓXIMO:** Vuelve aquí después de instalar

