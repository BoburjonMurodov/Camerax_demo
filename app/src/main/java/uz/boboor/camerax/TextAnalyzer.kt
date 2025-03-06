package uz.boboor.camerax

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.combine

class TextAnalyzer(
//    private val listener: (Text, Float) -> Unit,
    private val cardReader: (String?, Rect?) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var previewWidth = 0
    private var previewHeight = 0

    fun setSize(width: Int, height: Int) {
        previewHeight = height
        previewWidth = width
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val scale = previewHeight / imageProxy.width.toFloat()
        val scaleY = previewWidth / imageProxy.height.toFloat()

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener {

                    it.textBlocks.forEach {
                        it.lines.forEach {
                            if (it.text.length == 19 && it.boundingBox != null) {
                                Log.d("TTT", "${it.text}")
                                val old = it.boundingBox!!
                                val rect = Rect(
                                    (old.left * scale).toInt(),
                                    (old.top * scale).toInt(),
                                    (old.right * scale).toInt(),
                                    (old.bottom * scale).toInt()
                                )
                                it.text.isDigitsOnly()
                                cardReader.invoke(it.text, rect)
                            }
                        }
                    }

                    imageProxy.close()
                }.addOnFailureListener {
                    Log.d("TTT", "${it.message}")
                }
        }

    }

}

