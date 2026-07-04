# ⚠️ PROBLEMA DE MEMORIA - SOLUCIÓN

**Problema identificado:** 
```
"El archivo de paginación es demasiado pequeño para completar la operación"
```

**Causa:** Tu sistema no tiene suficiente memoria virtual (paginación) para compilar Gradle.

---

## 🔧 SOLUCIÓN - Aumentar archivo de paginación

### Windows 10/11:

1. **Abre Panel de Control**
   - Presiona: `Windows + R`
   - Escribe: `control panel`
   - Enter

2. **Sistemas > Configuración avanzada del sistema**
   - O busca: "Configuración del sistema avanzada"

3. **Pestaña "Opciones avanzadas"**
   - Click en "Rendimiento" → "Opciones..."

4. **Pestaña "Opciones avanzadas"** (la segunda vez)
   - Click en "Memoria virtual" → "Cambiar..."

5. **Aumentar tamaño**
   - Quita ✓ "Administrar automáticamente"
   - En "Tamaño personalizado":
     - **Tamaño inicial:** 4096 MB
     - **Tamaño máximo:** 8192 MB
   - Click "Establecer"
   - Click "Aceptar"

6. **Reinicia el PC**
   - Tu sistema necesita reiniciar para aplicar cambios

---

## ⚡ ALTERNATIVA RÁPIDA (Sin reiniciar)

Si no quieres reiniciar, comprime archivos para liberar RAM:

```powershell
# Ejecuta como administrador en PowerShell:

# Limpiar archivos temporales
Remove-Item -Path "$env:TEMP\*" -Force -Recurse -ErrorAction SilentlyContinue

# Limpiar papelera
$shell = New-Object -ComObject shell.application
$shell.Namespace(10).Self.InvokeVerb("empty")

# Svaciar buffer de caché
[System.GC]::Collect()
```

Luego intenta compilar de nuevo.

---

## 📊 VERIFICAR PAGINACIÓN ACTUAL

En PowerShell (como Admin):

```powershell
$pagefile = Get-WmiObject -Query "SELECT * FROM Win32_PageFile"
$pagefile | Select-Object Name, FileSize, AllocatedBaseSize
```

**Debería mostrar algo como:**
```
C:\pagefile.sys | 8388608 KB (8 GB)
```

---

## ✅ PASOS FINALES

1. **Aumenta la paginación a 8 GB** (como se describe arriba)
2. **Reinicia el PC**
3. **Vuelve aquí y ejecuta:**
   ```powershell
   cd "F:\Programas de  github\Visualonda\android_skeleton"
   gradle clean build --no-daemon
   ```

---

## 💡 SI NADA DE ESTO FUNCIONA

**Plan B: Usar Cloud Build**

Puedo configurar que se compile en la nube:
1. Creas cuenta en GitHub (gratis)
2. Subes el código
3. GitHub Actions compila automáticamente
4. Descargas el APK listo

**Ventaja:** Sin problemas de memoria local, sin firewall.

---

**Recomendación:** Aumenta la paginación, reinicia, e intenta compilar de nuevo.

Si falla, avísame y hacemos cloud build.

