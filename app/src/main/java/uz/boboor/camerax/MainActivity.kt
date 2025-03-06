package uz.boboor.camerax

import android.content.ContentValues
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.boboor.camerax.databinding.ActivityMainBinding
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var torch: Boolean = false
    private var cameraRotation = 0
    private lateinit var imageCapture: ImageCapture
    private val recentList = ArrayList<Uri>()
    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.shutter)

        imageCapture = ImageCapture.Builder().setTargetRotation(this.display!!.rotation).build()
        loadCamera(CameraSelector.DEFAULT_BACK_CAMERA)


        binding.btnTorch.setOnClickListener {
            torch()
        }

        binding.btnCameraRotate.setOnClickListener {
            rotateCamera()
        }

        binding.btnCapture.setOnClickListener {
            mediaPlayer.start()
            lifecycleScope.launch {
                it.alpha = 0.7f
                delay(100)
                it.alpha = 1f
            }
            capture()
        }

        binding.recentImage.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, PhotoScreen(recentList))
                .addToBackStack("asd")
                .commit()
        }
    }

    private lateinit var camera: Camera
    private fun loadCamera(cameraSelected: CameraSelector) {
        val previewView = findViewById<PreviewView>(R.id.myCamera)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                val textAnalyzer = TextAnalyzer { text, rect ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
//                            binding.overlay.setMLText(text, scale)
                            binding.overlay.card(rect, text)
                        }
                    }
                }

                binding.overlay.post {
                    textAnalyzer.setSize(binding.overlay.width, binding.overlay.height)
                }

                val imageAnalysis = ImageAnalysis.Builder().build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), textAnalyzer)

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelected, imageCapture, preview, imageAnalysis)

            }, ContextCompat.getMainExecutor(this)
        )
    }


    private fun rotateCamera() {
        if (cameraRotation == 1) {
            loadCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            cameraRotation = 0
        } else {
            cameraRotation = 1
            loadCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        }
        camera.cameraInfo.cameraSelector
    }

    private fun torch() {
        camera.cameraControl.enableTorch(torch)
        binding.btnTorch.setImageResource(if (torch) R.drawable.ic_flash else R.drawable.ic_flash_off)
        torch = !torch
    }

    private fun capture() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${UUID.randomUUID()}.png")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/myCameraApp")
        }

        val outputFile = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
            .build()

        lifecycleScope.launch {
            binding.effect.isVisible = true
            delay(100)
            binding.effect.isVisible = false
        }

        imageCapture.takePicture(outputFile, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                binding.recentImage.setImageURI(outputFileResults.savedUri)
                recentList.add(outputFileResults.savedUri!!)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@MainActivity, "${exception.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }
}

