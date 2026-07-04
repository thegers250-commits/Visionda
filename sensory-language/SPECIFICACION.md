# Especificación matemática: Manifiesto Sinestésico - Versión Inicial

Resumen: fórmulas y mapeos para traducir geometría, textura y luz a parámetros de síntesis sonora.

1) Elevación → frecuencia (mapeo logarítmico)

- Fórmula: f(h) = f0 * exp(k * h)
- Condiciones: f0 = 60 Hz en h = 0 m; f(2.5) = 5000 Hz
- Constante: k = ln(5000/60) / 2.5 ≈ 1.7685
- Ejemplo: f(h) = 60 * exp(1.7685 * h)

Justificación: el oído percibe cambios de frecuencia en escala logarítmica; mapeos vertical→pitch se han usado en proyectos de sustitución sensorial (ver referencias).

2) Distancia → ganancia y LPF

- Ganancia (suavizada): G(r) = 1 / (1 + (r / r_ref)^2), con r_ref = 1.0 m
- Filtro de paso bajo (cutoff) dependiente de distancia:
  fc(r) = fc0 * exp(-c * r)
  con fc0 = 12000 Hz, c ≈ 0.18 (ajustable)
- Resultado perceptual: a mayor distancia, menor energía y pérdida de frecuencias altas (simula absorción atmosférica/oclusiones)

Justificación: atenuación por ley de la inversa del cuadrado y reducción de altas frecuencias replican cues naturales de distancia.

3) Azimut → paneo binaural (aproximación HRTF simple)

- Normalizar azimut en grados az ∈ [-90, +90]
- Pan (equal-power): θ = (az + 90) / 180 * (π/2)
  L = cos(θ) ; R = sin(θ)
- ITD delay (ms): d(az) = sign(az) * (|az|/90) * d_max, d_max ≈ 0.8 ms
- ILD (dB): ild(az) = (|az|/90) * ild_max, ild_max ≈ 20 dB
  aplicar como atenuación del canal opuesto: g_opposite = 10^{(-ild/20)}

Justificación: ITD/ILD y panning son cues auditivos primarios para localización horizontal; usar aproximación simple reduce carga computacional.

4) Luz → osciladores senoidales con binaural beat (código de brillo)

- Luminancia L ∈ [0,1]
- Delta binaural: Δ(L) = 5 + 7 * L (Hz) → en rango 5–12 Hz
- Carrier sugerido para señal de brillo: fc_light = 4000 Hz (configurable)
- leftFreq = fc_light + Δ/2 ; rightFreq = fc_light - Δ/2
- Amplitud A = A_max * L (con límites de seguridad en SPL)

Justificación: diferencias micro-frecuencia entre canales inducen ritmos binaurales; usado como indicador de brillo sin ocupar banda baja crítica.

5) Material / textura → motores de síntesis

- Metales/vidrios: FM con carrier alto (3k–6kHz), índice de modulación armónico, modulaciones de anillo para transitorios.
- Madera/orgánicos: síntesis aditiva/sustractiva, armónicos bajos/medios (150–800Hz), envolventes suaves.
- Piedra/concreto (rugosidad): síntesis granular, granos 10–40ms sobre ruido rosa filtrado.

Parámetros comunes:
- Envolventes ADSR por celda: attack ∈ [1–50] ms, decay configurable, sustain relativo, release ∈ [10–200] ms.

6) Control y timings

- Rate de control: 30 Hz (each 33 ms) para actualizar parámetros de cada celda
- Audio: sample rate 44100 Hz, block size 64–256 samples
- Buffer de control: estructura ring-buffer entre hilo de visión y hilo de audio (lock-free recomendado)

7) Seguridad

- Limitar A_max para mantener SPL seguro; implementar Notch/filters temporales para evitar fatigación
- Modo seguro por defecto: volumen y bandas restringidas, recomendación de auriculares de conducción ósea

8) Vinculación a la evidencia

- Los mapeos propuestos están inspirados en trabajos de sustitución sensorial (Meijer, Bach-y-Rita), en principios de localización auditiva (Blauert), y en estudios sobre ecolocación humana y plasticidad cross-modal (Merabet, Thaler, Kolarik, Loomis). Ver `REFERENCES.md`.

---
Archivo creado para soportar prototipado rápido; parámetros empíricos pueden calibrarse con usuarios.
