package uz.boboor.camerax

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.text.Text

class OverLayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var scale = 1f

    private var mlText: Text? = null
    fun setMLText(text: Text, scale: Float) {
        mlText = text
        this.scale = scale
        invalidate()
    }

    private var rect: Rect? = Rect()
    private var cardNum: String? = ""

    fun card(rect: Rect?, text: String?) {
        this.rect = rect
        cardNum = text
        invalidate()
    }

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (rect != null && cardNum != null) {
            val old = rect!!

            val rect = Rect(
                (old.left * scale).toInt(),
                (old.top * scale).toInt(),
                (old.right * scale).toInt(),
                (old.bottom * scale).toInt()
            )

            canvas.drawRect(rect, paint)

            val centerX = rect.centerX().toFloat()
            val textY = rect.top.toFloat() - 10

            canvas.drawText(cardNum!!, centerX, textY, textPaint)
        }


//        val mlText = mlText ?: return
//        mlText.textBlocks.forEach {
//            val old = it.boundingBox!!
//            val rect = Rect(
//                (old.left * scale).toInt(),
//                (old.top * scale).toInt(),
//                (old.right * scale).toInt(),
//                (old.bottom * scale).toInt()
//            )
//
//            canvas.drawRect(rect, paint)
//        }
    }

}