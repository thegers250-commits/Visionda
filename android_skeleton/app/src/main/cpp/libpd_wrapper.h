#ifndef LIBPD_WRAPPER_H
#define LIBPD_WRAPPER_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Inicializar LibPD engine
 * @return true si inicialización exitosa
 */
bool libpd_wrapper_init();

/**
 * Cargar Pure Data patch
 * @param patch_path Ruta al archivo .pd (debe ser accessible)
 * @return true si patch cargado exitosamente
 */
bool libpd_wrapper_load_patch(const char* patch_path);

/**
 * Enviar valor flotante a receptor LibPD
 * @param receiver Nombre del receptor (ej: "light-freq")
 * @param value Valor flotante a enviar
 * @return true si envío exitoso
 */
bool libpd_wrapper_send_float(const char* receiver, float value);

/**
 * Procesar buffer de audio a través de LibPD
 * Llamado desde el audio callback de AAudio
 * @param output Buffer de salida (interleaved stereo)
 * @param frames Número de frames a procesar
 * @return true si procesamiento exitoso
 */
bool libpd_wrapper_process_tick(float* output, int frames);

/**
 * Limpiar recursos LibPD
 */
void libpd_wrapper_cleanup();

/**
 * Verificar si LibPD está inicializado
 * @return true si inicializado
 */
bool libpd_wrapper_is_initialized();

/**
 * Obtener sample rate de LibPD
 * @return Sample rate en Hz (típicamente 44100)
 */
int libpd_wrapper_get_sample_rate();

#ifdef __cplusplus
}
#endif

#endif  // LIBPD_WRAPPER_H
