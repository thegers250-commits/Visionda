#ifndef AUDIO_ENGINE_H
#define AUDIO_ENGINE_H

#ifdef __cplusplus
extern "C" {
#endif

/** Inicializa AAudio stream a 44100 Hz, estéreo, baja latencia */
bool audio_engine_init();

/** Frecuencia del oscilador (Hz) — usado cuando LibPD no está activo */
void audio_engine_set_frequency(float freq_hz);

/** Amplitud 0.0–1.0 (clipped internamente a 0.5 por seguridad) */
void audio_engine_set_amplitude(float amp);

/** Pan estéreo 0.0=izq, 0.5=centro, 1.0=der */
void audio_engine_set_pan(float pan);

/** Detiene el stream sin destruirlo */
void audio_engine_stop();

/** Para y libera todos los recursos */
void audio_engine_cleanup();

bool  audio_engine_is_running();
int32_t audio_engine_get_sample_rate();
int32_t audio_engine_get_latency_ms();

#ifdef __cplusplus
}
#endif

#endif  // AUDIO_ENGINE_H
