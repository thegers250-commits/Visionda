# 🎛️ FASE 2: ACCESIBILIDAD (Semanas 5-8)

**Duración:** 4 semanas
**Equipo:** Android (1) + Accesibilidad spec
**Objetivo:** Navegación completa para usuarios ciegos

---

## 📋 Visión General Fase 2

```
ENTRADA (Fase 1):
  ✅ Audio engine funcional
  ✅ Cámara capturando
  ✅ Mapeos calculados

OBJETIVO (Fase 2):
  → Usuario ciego puede navegar TODO sin documentación
  → Interfaz completamente accesible (TalkBack compatible)
  → Múltiples modos (Cámara, Pantalla, Galería, etc.)
  → Gestos intuitivos (swipe, pinch, long-press)
  → Feedback háptico (vibración táctil)

SALIDA:
  ✅ App 100% accesible
  ✅ Beta testing con usuarios ciegos (10-20 personas)
  ✅ Feedback consolidado → iteraciones
```

---

## Semana 5: TalkBack Integration + Gesture Recognition

### 5.1: AccessibilityService Setup (6 horas)

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/accessibility/AccessibilityServiceImpl.kt

package com.visualonda.sensory.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.content.Intent

class VisualondaAccessibilityService : AccessibilityService() {
    
    private val TAG = "AccessibilityService"
    
    override fun onServiceConnected() {
        Log.d(TAG, "Accessibility service connected")
        
        // Configure service
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.flags = AccessibilityServiceInfo.DEFAULT
        serviceInfo = info
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                Log.d(TAG, "View focused: ${event.source?.contentDescription}")
                announceForAccessibility("View focused")
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                Log.d(TAG, "View clicked: ${event.source?.contentDescription}")
                announceForAccessibility("View activated")
            }
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
    
    private fun announceForAccessibility(text: String) {
        // Send announcement to TalkBack
        val event = AccessibilityEvent.obtain()
        event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
        event.text.add(text)
        sendAccessibilityEvent(event)
    }
}
```

### 5.2: Gesture Detector (8 horas)

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/accessibility/GestureDetector.kt

package com.visualonda.sensory.accessibility

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.util.Log

interface GestureListener {
    fun onSwipeUp()
    fun onSwipeDown()
    fun onSwipeLeft()
    fun onSwipeRight()
    fun onLongPress()
    fun onDoubleTap()
    fun onPinchZoom(scale: Float)
}

class VisualondaGestureDetector(
    private val context: Context,
    private val listener: GestureListener
) : GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
    
    private val TAG = "GestureDetector"
    private val gestureDetector: GestureDetector
    private val scaleDetector: ScaleGestureDetector
    
    private var downX = 0f
    private var downY = 0f
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
    
    init {
        gestureDetector = GestureDetector(context, this)
        scaleDetector = ScaleGestureDetector(context, this)
    }
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
        }
        return true
    }
    
    // GestureDetector callbacks
    override fun onDown(e: MotionEvent?): Boolean = true
    
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || e2 == null) return false
        
        val deltaX = e2.x - e1.x
        val deltaY = e2.y - e1.y
        
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Horizontal swipe
            if (deltaX > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                Log.d(TAG, "Swipe RIGHT")
                listener.onSwipeRight()
                return true
            } else if (deltaX < -SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                Log.d(TAG, "Swipe LEFT")
                listener.onSwipeLeft()
                return true
            }
        } else {
            // Vertical swipe
            if (deltaY > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                Log.d(TAG, "Swipe DOWN")
                listener.onSwipeDown()
                return true
            } else if (deltaY < -SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                Log.d(TAG, "Swipe UP")
                listener.onSwipeUp()
                return true
            }
        }
        return false
    }
    
    override fun onLongPress(e: MotionEvent?) {
        Log.d(TAG, "Long press detected")
        listener.onLongPress()
    }
    
    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.d(TAG, "Double tap detected")
        listener.onDoubleTap()
        return true
    }
    
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean = true
    override fun onShowPress(e: MotionEvent?) {}
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = true
    
    // ScaleGestureDetector callbacks
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        Log.d(TAG, "Pinch zoom: ${detector.scaleFactor}")
        listener.onPinchZoom(detector.scaleFactor)
        return true
    }
    
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}
}
```

### 5.3: Actualizar MainActivity - Gesture Integration (6 horas)

```kotlin
// Agregar a MainActivity.kt:

class MainActivity : AppCompatActivity(), GestureListener {
    
    private lateinit var gestureDetector: VisualondaGestureDetector
    private var currentMode = "camera"  // camera, screen, gallery, maps
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ... existing code ...
        
        gestureDetector = VisualondaGestureDetector(this, this)
    }
    
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }
    
    // GestureListener implementations
    override fun onSwipeUp() {
        Log.d(TAG, "Swipe UP")
        currentMode = when (currentMode) {
            "camera" → "screen"
            "screen" → "gallery"
            "gallery" → "maps"
            else → "camera"
        }
        announceMode()
    }
    
    override fun onSwipeDown() {
        Log.d(TAG, "Swipe DOWN")
        currentMode = when (currentMode) {
            "camera" → "maps"
            "screen" → "camera"
            "gallery" → "screen"
            else → "gallery"
        }
        announceMode()
    }
    
    override fun onSwipeLeft() {
        Log.d(TAG, "Swipe LEFT - Explore left")
        announceForAccessibility("Exploring to the left")
    }
    
    override fun onSwipeRight() {
        Log.d(TAG, "Swipe RIGHT - Explore right")
        announceForAccessibility("Exploring to the right")
    }
    
    override fun onLongPress() {
        Log.d(TAG, "Long press - Detailed exploration mode")
        announceForAccessibility("Detailed exploration mode activated")
        // Enable zoom mode, increase resolution temporarily
    }
    
    override fun onDoubleTap() {
        Log.d(TAG, "Double tap - Select/Activate")
        announceForAccessibility("Element selected")
    }
    
    override fun onPinchZoom(scale: Float) {
        Log.d(TAG, "Pinch zoom: $scale")
        if (scale > 1.1f) {
            announceForAccessibility("Zoomed in")
        } else if (scale < 0.9f) {
            announceForAccessibility("Zoomed out")
        }
    }
    
    private fun announceMode() {
        val modeText = when (currentMode) {
            "camera" → "Camera mode"
            "screen" → "Screen navigation mode"
            "gallery" → "Gallery browsing mode"
            "maps" → "Maps navigation mode"
            else → "Unknown mode"
        }
        announceForAccessibility(modeText)
    }
    
    private fun announceForAccessibility(text: String) {
        val event = AccessibilityEvent.obtain()
        event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
        event.text.add(text)
        sendAccessibilityEvent(event)
    }
}
```

### 5.4: Testing Semana 5

```
☐ Compilación sin errores
☐ App abre
☐ TalkBack conectado: lee elementos
☐ Swipe UP/DOWN: cambia de modo (anunciado)
☐ Swipe LEFT/RIGHT: explora
☐ Long press: entrada a modo detallado
☐ Double tap: selecciona
☐ Pinch zoom: zoom in/out (anunciado)
☐ Logcat sin errores de accessibility
✅ Semana 5 parcial: TalkBack working
```

---

## Semana 6-7: Haptic Feedback + Multi-Mode + Settings

### 6.1: Haptic Feedback (6 horas)

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/accessibility/HapticFeedback.kt

package com.visualonda.sensory.accessibility

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class HapticFeedback(private val context: Context) {
    
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val TAG = "HapticFeedback"
    
    fun pulse() {
        // Short pulse on element detection
        vibrate(50)  // 50ms
        Log.d(TAG, "Pulse")
    }
    
    fun doubleClick() {
        // Double pulse on selection
        vibrate(intArrayOf(0, 100, 100, 100), -1)  // pattern: wait, vibrate, wait, vibrate
        Log.d(TAG, "Double click")
    }
    
    fun modeChange() {
        // Triple pulse on mode change
        vibrate(intArrayOf(0, 50, 150, 50, 150, 50), -1)
        Log.d(TAG, "Mode change")
    }
    
    fun zoomIn() {
        // Ascending vibration on zoom in
        vibrate(intArrayOf(0, 30, 50, 30, 80, 30), -1)
        Log.d(TAG, "Zoom in")
    }
    
    fun zoomOut() {
        // Descending vibration on zoom out
        vibrate(intArrayOf(0, 80, 50, 30, 50, 50), -1)
        Log.d(TAG, "Zoom out")
    }
    
    fun error() {
        // Error pattern: long buzzzz
        vibrate(intArrayOf(0, 200), -1)
        Log.d(TAG, "Error")
    }
    
    private fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
    
    private fun vibrate(pattern: IntArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, repeat)
        }
    }
}
```

### 6.2: Settings Activity (8 horas)

```kotlin
// FILE: app/src/main/java/com/visualonda/sensory/SettingsActivity.kt

package com.visualonda.sensory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.content.SharedPreferences
import android.util.Log

class SettingsActivity : AppCompatActivity() {
    
    private val TAG = "Settings"
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences("visualonda_prefs", MODE_PRIVATE)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        // Volume slider
        val lblVolume = TextView(this)
        lblVolume.text = "Master Volume"
        
        val volumeSlider = SeekBar(this)
        volumeSlider.max = 100
        volumeSlider.progress = prefs.getInt("volume", 50)
        volumeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    prefs.edit().putInt("volume", progress).apply()
                    Log.d(TAG, "Volume: $progress")
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // SPL Limit
        val lblSPL = TextView(this)
        lblSPL.text = "SPL Limit (dB)"
        
        val splSpinner = Spinner(this)
        val splOptions = arrayOf("75 dB (Safe)", "80 dB (Caution)", "85 dB (OSHA)")
        splSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, splOptions)
        splSpinner.setSelection(prefs.getInt("spl_level", 0))
        
        // TalkBack Enabled
        val talkbackToggle = CheckBox(this)
        talkbackToggle.text = "TalkBack Integration"
        talkbackToggle.isChecked = prefs.getBoolean("talkback_enabled", true)
        talkbackToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("talkback_enabled", isChecked).apply()
            Log.d(TAG, "TalkBack: $isChecked")
        }
        
        // Haptic Feedback
        val hapticToggle = CheckBox(this)
        hapticToggle.text = "Haptic Feedback"
        hapticToggle.isChecked = prefs.getBoolean("haptic_enabled", true)
        hapticToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("haptic_enabled", isChecked).apply()
            Log.d(TAG, "Haptic: $isChecked")
        }
        
        // Material preset selector
        val lblMaterial = TextView(this)
        lblMaterial.text = "Material Detection Preset"
        
        val materialSpinner = Spinner(this)
        val materialOptions = arrayOf("Balanced", "Enhanced Metal", "Enhanced Wood", "Minimal Processing")
        materialSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materialOptions)
        
        // Save button
        val btnSave = Button(this)
        btnSave.text = "Save Settings"
        btnSave.setOnClickListener {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        layout.addView(lblVolume)
        layout.addView(volumeSlider)
        layout.addView(lblSPL)
        layout.addView(splSpinner)
        layout.addView(talkbackToggle)
        layout.addView(hapticToggle)
        layout.addView(lblMaterial)
        layout.addView(materialSpinner)
        layout.addView(btnSave)
        
        setContentView(layout)
    }
}
```

### 6.3: AndroidManifest.xml - Update Permissions & Services

```xml
<!-- Agregar a AndroidManifest.xml -->

<service
    android:name=".accessibility.VisualondaAccessibilityService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>

<activity
    android:name=".SettingsActivity"
    android:exported="false" />

<!-- Agregar si no existe -->
<uses-permission android:name="android.permission.VIBRATE" />
```

### 6.4: Testing Semana 6-7

```
☐ Haptic feedback en eventos:
   ☐ Pulse cuando elemento detectado
   ☐ Double click cuando seleccionado
   ☐ Mode change cuando cambia modo
   ☐ Zoom in/out vibration patterns

☐ Settings Activity:
   ☐ Abre sin crash
   ☐ Volume slider funciona
   ☐ Checkboxes guardan estado
   ☐ Spinner para SPL limit
   ☐ Datos persistidos en SharedPreferences

☐ Multi-modo:
   ☐ Swipe UP/DOWN cambia modo
   ☐ Cada modo anunciado
   ☐ Audio cambia según modo

✅ Semana 7: Accesibilidad completa
```

---

## Semana 8: Beta Testing

### 8.1: Distribution

```
1. Compilar APK (release o beta)
2. Distribuir a 10-20 usuarios ciegos (Google Play internal testing track)
3. Recolectar feedback
```

### 8.2: Feedback Collection

```
Contacto diario vía:
  ☐ Formulario Google Forms
  ☐ Email
  ☐ Llamadas telefónicas

Preguntas claves:
  1. ¿Navega la app sin documentación?
  2. ¿Los gestos son intuitivos?
  3. ¿El feedback de audio es claro?
  4. ¿La vibración ayuda?
  5. ¿Qué falta?
  6. Bugs encontrados?
```

### 8.3: Iterate

```
Semana 8:
  ☐ Triage bugs (críticos vs. nice-to-have)
  ☐ Crear tickets en Jira
  ☐ Iniciar fixes
  ☐ Re-release beta si hay cambios mayores
```

### 8.4: Gate Fase 2

```
CRITERIOS:
  ☐ 10+ usuarios ciegos testean 1+ hora sin docum
  ☐ 80%+ pueden navegar intuitivamente
  ☐ <5 bugs críticos encontrados
  ☐ Feedback positivo en accesibilidad
  ☐ TalkBack + Gestos funcionan
  ☐ Haptic feedback apreciado
  
Si ✅: PROCEDE A FASE 3 (Inteligencia)
```

---

## 📊 Entregables Fase 2

```
CÓDIGO:
  ✅ AccessibilityService.kt (150 líneas)
  ✅ GestureDetector.kt (250 líneas)
  ✅ HapticFeedback.kt (120 líneas)
  ✅ SettingsActivity.kt (200 líneas)
  ✅ MainActivity actualizado (150 líneas)
  ✅ AndroidManifest.xml actualizado

TOTAL: ~870 líneas

FUNCIONALIDAD:
  ✅ TalkBack integrado 100%
  ✅ 5+ gestos intuitivos
  ✅ Haptic feedback patrones
  ✅ Settings accesibles
  ✅ Multi-modo funcional
  ✅ 100% accessible (WCAG A compliance)

TESTING:
  ✅ 10+ usuarios ciegos beta
  ✅ Iteración basada en feedback
  ✅ <5 bugs críticos
  ✅ 80%+ usabilidad intuitiva
```

---

**Próximo:** FASE_3_INTELIGENCIA.md (Semanas 9-12)

