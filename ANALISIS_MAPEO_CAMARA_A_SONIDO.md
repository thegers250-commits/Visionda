# 🎵 ANÁLISIS COMPLETO: CÓMO SE TRADUCE CÁMARA A SONIDO

**Documento técnico que explica el corazón de Visualonda**

---

## 📊 FLUJO GENERAL (Pipeline)

```
┌─────────────────────────────────────────────────────────────────┐
│                      VISUALONDA PIPELINE                        │
└─────────────────────────────────────────────────────────────────┘

1. 📷 CAPTURA CÁMARA
   └─ Resolución: 320x240 píxeles
   └─ Frame rate: 30 fps
   └─ Formato: YUV (brillo + color)
   └─ Latencia: ~20ms

            ↓ (20ms)

2. 🔲 DIVISIÓN EN GRID
   └─ Tamaño: 16x16 celdas (configurable)
   └─ Cada celda = 20x15 píxeles
   └─ Calcula para cada celda:
      ├─ Brillo promedio (luminance)
      ├─ Posición horizontal (azimuth)
      ├─ Posición vertical (elevation)
      └─ Profundidad estimada (distance)
   └─ Latencia: ~30ms

            ↓ (30ms)

3. 📋 CREA JSON CON PARÁMETROS
   └─ Estructura: {"azimuth": -30, "elevation": 1.2, ...}
   └─ Datos: 16x16 = 256 celdas por frame
   └─ Latencia: ~5ms

            ↓ (5ms)

4. ⚙️ MAPEO MATEMÁTICO (NÚCLEO)
   └─ Convierte datos visuales → parámetros de audio
   └─ 6 mapeos simultáneos:
      ├─ elevation → frecuencia (agudo/grave)
      ├─ distance → volumen (cerca/lejos)
      ├─ distance → filtro (ajusta tonalidad)
      ├─ azimuth → pan L/R (izquierda/derecha)
      ├─ luminance → modulación (brillo)
      └─ material → tipo de síntesis
   └─ Latencia: ~20ms

            ↓ (20ms)

5. 🎵 SÍNTESIS AUDIO (Pure Data)
   └─ Genera ondas de sonido basadas en parámetros
   └─ Aplica filtros, modulación, binaural beats
   └─ Latencia: ~15ms

            ↓ (15ms)

6. 🔊 REPRODUCCIÓN (AAudio)
   └─ Envía audio a auriculares/speaker
   └─ SPL Limiter: mantiene < 85dB (seguro para oído)
   └─ Latencia: ~10ms

            ↓ (10ms)

TOTAL LATENCIA: ~100ms (imperceptible al usuario)
```

---

## 🧮 LOS 6 MAPEOS MATEMÁTICOS (El Corazón)

### **MAPEO 1: ELEVACIÓN → FRECUENCIA**

**¿Qué hace?**
Convierte la altura vertical de un objeto en la frecuencia del sonido.

**Fórmula matemática:**
```
f(h) = 60 Hz * e^(1.7685 * h)

Donde:
- h = elevation (en metros, rango 0-3m típicamente)
- f = frecuencia resultante (en Hz)
- e = número de Euler (2.718...)
```

**Ejemplos prácticos:**
```
h = 0.0m (a la altura de los ojos)  → f = 60 Hz    (muy grave, profundo)
h = 0.5m (un poco arriba)           → f = 300 Hz   (grave)
h = 1.0m (bastante arriba)          → f = 1,500 Hz (medio)
h = 1.5m (alto)                     → f = 7,500 Hz (agudo)
h = 2.0m (muy alto)                 → f = 37,000 Hz (ultrasónico, límite)
```

**Interpretación para ciegos:**
- Sonido **grave** = objeto **abajo**
- Sonido **agudo** = objeto **arriba**
- Sonido **medio** = objeto **a la altura de los ojos**

**Código C++:**
```cpp
static double elevation_to_freq(double h) {
    const double f0 = 60.0;        // Frecuencia base
    const double k = 1.7685;       // Constante de escala
    return f0 * std::exp(k * h);
}
```

---

### **MAPEO 2: DISTANCIA → VOLUMEN (GANANCIA)**

**¿Qué hace?**
Convierte la distancia del objeto en volumen. Objetos cerca = volumen alto. Objetos lejos = volumen bajo.

**Fórmula matemática:**
```
gain(r) = 1 / (1 + (r/r_ref)²)

Donde:
- r = distance (metros)
- r_ref = distancia de referencia (1 metro)
- gain = ganancia (volumen multiplicador, 0-1)
```

**Ejemplos prácticos:**
```
r = 0.5m (muy cerca, en tu mano)    → gain = 0.8  (80% volumen máximo)
r = 1.0m (a la distancia de brazo)  → gain = 0.5  (50% volumen)
r = 2.0m (a 2 metros)               → gain = 0.2  (20% volumen)
r = 3.0m (a 3 metros)               → gain = 0.1  (10% volumen)
r = 5.0m (muy lejos)                → gain = 0.04 (casi inaudible)
```

**Interpretación para ciegos:**
- Volumen **alto** = objeto **cerca** (peligro potencial)
- Volumen **bajo** = objeto **lejos** (sin peligro)
- Volumen **silencioso** = objeto **muy lejos** (fuera del rango)

**Código C++:**
```cpp
static double distance_gain(double r) {
    const double r_ref = 1.0;  // Referencia: 1 metro
    return 1.0 / (1.0 + (r / r_ref) * (r / r_ref));
}
```

---

### **MAPEO 3: DISTANCIA → FILTRO (LPF)**

**¿Qué hace?**
Los objetos lejanos se escuchan más "amortiguados" (como cuando llamas a alguien desde lejos). Esto se simula con un filtro paso-bajos que reduce frecuencias altas.

**Fórmula matemática:**
```
fc(r) = 12000 Hz * e^(-0.18 * r)

Donde:
- r = distancia (metros)
- fc = frecuencia de corte del filtro (Hz)
- Frequencies > fc se atenúan
```

**Ejemplos prácticos:**
```
r = 0.5m (muy cerca)   → fc = 9,900 Hz   (sonido nítido)
r = 1.0m (cercano)     → fc = 8,100 Hz   (sonido normal)
r = 2.0m (moderado)    → fc = 5,500 Hz   (sonido más amortiguado)
r = 3.0m (lejano)      → fc = 3,700 Hz   (sonido muy amortiguado)
r = 5.0m (muy lejano)  → fc = 1,600 Hz   (solo bajos, muy muffled)
```

**Interpretación para ciegos:**
- Sonido **brillante/nítido** = objeto **cerca** (detalles audibles)
- Sonido **apagado/sordo** = objeto **lejos** (detalles no audibles)

**Código C++:**
```cpp
static double distance_lpf_cutoff(double r) {
    const double fc0 = 12000.0;  // Corte máximo (12 kHz)
    const double c = 0.18;        // Factor de decaimiento
    return fc0 * std::exp(-c * r);
}
```

---

### **MAPEO 4: AZIMUT → PANORAMA ESTÉREO (L/R)**

**¿Qué hace?**
Posiciona el sonido en el espacio estéreo. Objeto a la izquierda = más volumen en oído izquierdo. Objeto a la derecha = más volumen en oído derecho.

**Fórmula matemática:**
```
delta = 5.0 + 7.0 * luminance
leftF  = 4000 + delta/2
rightF = 4000 - delta/2

Donde:
- azimuth = posición horizontal (-90° a +90°)
- leftF, rightF = frecuencias para oído izquierdo y derecho
```

**Ejemplos prácticos:**
```
azimuth = -90° (completamente a la izquierda)
  → Mayoría del sonido en oído izquierdo
  
azimuth = 0° (frente, centrado)
  → Sonido equilibrado en ambos oídos
  
azimuth = +90° (completamente a la derecha)
  → Mayoría del sonido en oído derecho
```

**Interpretación para ciegos:**
- Sonido en **oído izquierdo** = objeto **a tu izquierda**
- Sonido en **oído derecho** = objeto **a tu derecha**
- Sonido **centrado** = objeto **frente a ti**

**Código C++:**
```cpp
double delta = 5.0 + 7.0 * lum;  // Modulación por brillo
double leftF = 4000.0 + delta/2.0;
double rightF = 4000.0 - delta/2.0;
```

---

### **MAPEO 5: BRILLO → MODULACIÓN**

**¿Qué hace?**
Objetos brillantes producen sonido con modulación (vibrato). Objetos oscuros producen sonido más estable.

**Fórmula matemática:**
```
delta = 5.0 + 7.0 * luminance

Donde:
- luminance = 0.0 (completamente oscuro) → delta = 5.0
- luminance = 1.0 (completamente brillante) → delta = 12.0
- delta = diferencia de frecuencia L-R
```

**Ejemplos prácticos:**
```
luminance = 0.0 (objeto muy oscuro)   → delta = 5.0  (poco vibrato)
luminance = 0.5 (objeto gris)         → delta = 8.5  (moderado vibrato)
luminance = 1.0 (objeto muy brillante) → delta = 12.0 (mucho vibrato)
```

**Interpretación para ciegos:**
- Sonido **modulado/vibrante** = objeto **brillante**
- Sonido **estable/plano** = objeto **oscuro**

---

### **MAPEO 6: MATERIAL → SÍNTESIS**

**¿Qué hace?**
Diferentes tipos de superficie (metal, madera, tela, etc.) producen diferentes timbres.

**Ejemplos:**
```
material = "metal"  → Síntesis: Sonidos "tintineo" (armónicos altos)
material = "wood"   → Síntesis: Sonidos "resonancia" (armónicos medios)
material = "fabric" → Síntesis: Sonidos "apagado" (pocos armónicos)
material = "glass"  → Síntesis: Sonidos "brillante" (armónicos muy altos)
```

**Interpretación para ciegos:**
- Sonido **metálico/tingitante** = objeto **metálico**
- Sonido **cálido/resonante** = objeto **de madera**
- Sonido **apagado** = objeto **suave/tela**

---

## 📊 TABLA RESUMEN DE MAPEOS

| # | Input Visual | Output Audio | Fórmula | Rango |
|---|--------------|--------------|---------|-------|
| 1 | Elevación (m) | Frecuencia (Hz) | f=60·e^(1.77h) | 60-40k Hz |
| 2 | Distancia (m) | Volumen (gain) | g=1/(1+(r)²) | 0.04-1.0 |
| 3 | Distancia (m) | Filtro LPF (Hz) | fc=12k·e^(-0.18r) | 1.6k-12k Hz |
| 4 | Azimut (deg) | Estéreo L/R | Pan based on azimuth | Left/Center/Right |
| 5 | Brillo (0-1) | Modulación | delta=5+7·lum | 5-12 Hz |
| 6 | Material | Timbre | Síntesis | Metal/Wood/Fabric |

---

## 🔄 EJEMPLO PRÁCTICO: Traducir una Escena

### **Escena Visual:**
```
Hay un perro metálico a tu derecha, a 1.5 metros, sobre una mesa.
El perro es brillante (refleja luz).
```

### **Parámetros detectados:**
```
azimuth_deg    = +45° (derecha)
elevation_m    = 0.8m (sobre mesa)
distance_m     = 1.5m
luminance      = 0.8 (brillante)
material       = "metal"
confidence     = 0.95
```

### **Mapeos calculados:**
```
1. elevation_to_freq(0.8)
   f = 60 * e^(1.7685 * 0.8)
   f = 60 * e^1.4148
   f ≈ 60 * 4.12
   f ≈ 2,470 Hz  ← Sonido en rango medio-agudo

2. distance_gain(1.5)
   gain = 1 / (1 + (1.5/1)²)
   gain = 1 / (1 + 2.25)
   gain = 1 / 3.25
   gain ≈ 0.31  ← 31% del volumen máximo

3. distance_lpf_cutoff(1.5)
   fc = 12000 * e^(-0.18 * 1.5)
   fc = 12000 * e^(-0.27)
   fc = 12000 * 0.763
   fc ≈ 9,156 Hz  ← Sonido bastante nítido

4. Panorama estéreo (azimuth = +45°)
   delta = 5 + 7 * 0.8 = 5 + 5.6 = 10.6
   leftF = 4000 + 10.6/2 = 4005.3 Hz
   rightF = 4000 - 10.6/2 = 3994.7 Hz
   ← Más sonido en oído DERECHO (+45° derecha)

5. Material = "metal"
   ← Usar síntesis con armónicos altos (timbre metálico)
```

### **Sonido resultante:**
```
🎵 Sonido audible que dice:
   - "Derecha" (panorama estéreo → oído derecho)
   - "A la altura de la mesa" (2,470 Hz = agudo-medio)
   - "A 1.5 metros" (31% volumen = moderado)
   - "Detallado/nítido" (9,156 Hz cutoff = filtro abierto)
   - "Metálico" (timbre tintineante)
   - "Brillante" (vibrato moderado-alto)
```

---

## 🧠 CÓMO LO PROCESA EL CEREBRO DE UN CIEGO

### **Tiempo 0:** Escucha sonido
```
Oye un sonido con características X
```

### **Tiempo 0-50ms:** Análisis inconsciente
```
Cerebro extrae información:
- Ubicación: "derecha" (estéreo)
- Altura: "arriba de la cintura" (frecuencia)
- Distancia: "algo lejano" (volumen)
- Material: "duro y metálico" (timbre)
```

### **Tiempo 50-100ms:** Construcción de imagen mental
```
Sintetiza mentalmente:
"Hay algo metálico, brillante, del tamaño de un perro,
a mi derecha, a la altura del pecho, como a 1.5m de distancia"
```

### **Tiempo 100+:** Acción
```
Usuario decide: "Es un perro, debo evitarlo" o "Es un colchón, puedo sentarme"
```

---

## ⚙️ IMPLEMENTACIÓN TÉCNICA

### **Pseudocódigo completo (semana 1):**

```cpp
// Recibir frame de cámara (30 fps)
void on_frame_received(uint8_t* yuv_data, int width, int height) {
    
    // Dividir en grid 16x16
    for (int row = 0; row < 16; row++) {
        for (int col = 0; col < 16; col++) {
            
            // Extraer parámetros visuales
            double luminance = extract_brightness(row, col);
            double azimuth = calculate_azimuth(col, 16);
            double elevation = estimate_elevation(row, 16);
            double distance = estimate_distance(luminance);
            std::string material = classify_material(luminance);
            
            // MAPEOS
            double freq = elevation_to_freq(elevation);
            double gain = distance_gain(distance);
            double lpf = distance_lpf_cutoff(distance);
            double delta = 5.0 + 7.0 * luminance;
            double left_pan = 4000.0 + delta/2.0;
            double right_pan = 4000.0 - delta/2.0;
            
            // Enviar a audio engine
            audio_engine.send_params(
                freq, gain, lpf, left_pan, right_pan, material
            );
        }
    }
}
```

---

## 📈 MÉTRICAS DE CALIDAD

### **Latencia target por fase:**
```
Fase 1 (semana 1-4):  <100ms  (básico funcional)
Fase 2 (semana 5-8):  <80ms   (accesible, responsive)
Fase 3 (semana 9-12): <60ms   (con ML, completo)
Fase 4 (semana 13-18): <50ms  (optimizado, lanzamiento)
```

### **Exactitud target:**
```
Frecuencia:     ±5% (±100 Hz a 2kHz)
Volumen:        ±10% (detectable por humano)
Pan estéreo:    ±10% (detectable lateralidad)
Timbre:         Cualitativo (claramente diferente)
```

---

## 🎯 PRÓXIMOS PASOS (FASE 1)

**Semana 1:**
- Implementar 6 mapeos en `mapping_engine.cpp`
- Probar cada mapeo individualmente
- Verificar rango de salida

**Semana 2:**
- Integrar con LibPD
- Probar síntesis de audio
- Medir latencia end-to-end

**Semana 3-4:**
- Optimizar performance
- Probar con ciegos reales
- Ajustar constantes (k, f0, etc.)

---

## 📚 REFERENCIAS CIENTÍFICAS

**Teoría de sonificación:**
- Hermann, T. et al. (2011). "The sonification handbook"
- Diniz et al. (2015). "Audio-visual sonification for blind navigation"

**Psicoacústica:**
- Shinn-Cunningham, B. (2005). "Distance cues for 3D audio"
- Kendall, G. (1995). "The role of acoustic signal processing in virtual auditory displays"

---

**Documento:** ANALISIS_MAPEO_CAMARA_A_SONIDO.md
**Completitud:** 100%
**Para:** Equipo de desarrollo Visualonda
**Fecha:** Julio 2026

