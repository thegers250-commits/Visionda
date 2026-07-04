package com.visualonda.sensory

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.visualonda.sensory.ui.viewmodel.AudioViewModel
import com.visualonda.sensory.ui.viewmodel.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private val audioViewModel: AudioViewModel by viewModels()
    private val cameraViewModel: CameraViewModel by viewModels()
    
    external fun pdInit()
    external fun audioEngineInit()
    external fun sendControlJson(json: String)
    
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)
        
        // Botón 1: Init Audio
        val btnAudioInit = Button(this).apply {
            text = "Init Audio Engine"
            setOnClickListener {
                try {
                    audioViewModel.initialize()
                    audioEngineInit()
                    Toast.makeText(this@MainActivity, "Audio initialized", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Botón 2: Init PD
        val btnPDInit = Button(this).apply {
            text = "Init PD (Week 1)"
            setOnClickListener {
                try {
                    pdInit()
                    Toast.makeText(this@MainActivity, "PD initialized", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Botón 3: Start Camera
        val btnCameraStart = Button(this).apply {
            text = "Start Camera (Week 3)"
            setOnClickListener {
                try {
                    cameraViewModel.startCapture()
                    Toast.makeText(this@MainActivity, "Camera started", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        layout.addView(btnAudioInit)
        layout.addView(btnPDInit)
        layout.addView(btnCameraStart)
        setContentView(layout)
    }
}
