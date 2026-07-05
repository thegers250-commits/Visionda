package com.visualonda.sensory.accessibility

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 * Fase 2 — Gestos táctiles para usuarios ciegos
 *
 * Gestos soportados:
 *   - Doble toque        → activar/desactivar cámara
 *   - Deslizar derecha   → aumentar volumen
 *   - Deslizar izquierda → disminuir volumen
 *   - Deslizar arriba    → cambiar modo de audio
 *   - Deslizar abajo     → detener todo
 *   - Toque largo        → leer descripción de escena actual
 *   - Dos dedos toque    → pausa/resume
 */
class GestureHandler(
    context: Context,
    private val listener: GestureListener
) {
    interface GestureListener {
        fun onDoubleTap()
        fun onSwipeRight()
        fun onSwipeLeft()
        fun onSwipeUp()
        fun onSwipeDown()
        fun onLongPress()
        fun onTwoFingerTap()
    }

    private val SWIPE_MIN_DISTANCE  = 120f  // px mínimos para considerar swipe
    private val SWIPE_THRESHOLD_VEL = 200f  // velocidad mínima

    private val gestureDetector = GestureDetector(context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent): Boolean {
                listener.onDoubleTap()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                listener.onLongPress()
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val e1 = e1 ?: return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y

                return if (abs(dx) > abs(dy)) {
                    // Swipe horizontal
                    if (abs(dx) > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VEL) {
                        if (dx > 0) listener.onSwipeRight() else listener.onSwipeLeft()
                        true
                    } else false
                } else {
                    // Swipe vertical
                    if (abs(dy) > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VEL) {
                        if (dy < 0) listener.onSwipeUp() else listener.onSwipeDown()
                        true
                    } else false
                }
            }

            override fun onDown(e: MotionEvent) = true
        }
    )

    // Detecta toque con 2 dedos
    private var lastTwoFingerTime = 0L

    fun onTouchEvent(event: MotionEvent): Boolean {
        // Detectar toque con dos dedos
        if (event.pointerCount == 2 && event.action == MotionEvent.ACTION_POINTER_UP) {
            val now = System.currentTimeMillis()
            if (now - lastTwoFingerTime < 300) {
                listener.onTwoFingerTap()
            }
            lastTwoFingerTime = now
            return true
        }
        return gestureDetector.onTouchEvent(event)
    }

    /** Adjunta el handler a una View */
    fun attachTo(view: View) {
        view.setOnTouchListener { _, event ->
            onTouchEvent(event)
            true
        }
    }
}
