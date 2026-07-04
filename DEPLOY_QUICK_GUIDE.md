# 📱 DEPLOY QUICK GUIDE - 5 MINUTOS

**Para instalar app en Android ahora mismo**

---

## ⚡ 5 MIN INSTALL

### Step 1: Conectar teléfono (1 min)

```bash
# Conectar USB cable al teléfono

# En teléfono:
# Configuración → Acerca de → Build Number (tap 7 veces)
# Configuración → Opciones desarrollo → Depuración USB → ON

# En PC:
adb devices

# Debe mostrar tu teléfono
```

### Step 2: Instalar app (2 min)

```bash
cd "f:\Programas de  github\Visualonda\android_skeleton"

./gradlew installDebug

# Espera "BUILD SUCCESSFUL"
```

### Step 3: Abrir app (1 min)

```bash
# En teléfono:
# - Encontrar app "Visualonda" o "sensory"
# - Tap para abrir
# - Deberías ver 3 botones

# En PC, ver logs:
adb logcat | grep native
```

### Step 4: Verificar (1 min)

```bash
# Botón 1: "Init PD (stub)" - tap
# Resultado: Logcat muestra "[native] pdInit() called - stub"

# Botón 3: "Send Sample Frame" - tap
# Resultado: Logcat muestra mapeos calculados

✅ App funciona!
```

---

## 🔧 TROUBLESHOOTING

| Problema | Solución |
|----------|----------|
| "adb: command not found" | Instalar Android SDK Platform Tools |
| "unauthorized" en adb | Permitir USB debugging en teléfono |
| App no instala | `adb uninstall com.visualonda.sensory` luego reintentar |
| App crashes al abrir | Revisar logcat: `adb logcat \| grep ERROR` |
| No se ve en adb devices | Cambiar USB mode a "Transfer Files" en teléfono |

---

## 📊 QUICK STATUS

```
✅ App compila: YES
✅ Instala en Android: YES
✅ Abre sin crash: YES
✅ Botones funcionan: YES

⏳ Audio: NO (próximo)
⏳ Cámara: NO (próximo)
⏳ ML: NO (próximo)
```

---

## 🎯 NEXT

Lee: **SIGUIENTE_PASOS_APP_LISTA.md**

