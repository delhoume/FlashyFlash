package com.delhoume.flashbattle.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

class ColorFilterModifier(val redScale: Float,
                          val greenScale: Float,
                          val blueScale: Float,) : DrawModifier {
    override fun ContentDrawScope.draw() {
        val saturationMatrix = ColorMatrix().apply { setToScale(redScale = redScale, greenScale = greenScale, blueScale = blueScale, alphaScale = 1f) }
        val saturationFilter = ColorFilter.colorMatrix(saturationMatrix)
        val paint = Paint().apply {
            colorFilter = saturationFilter
        }
        drawIntoCanvas {
            it.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            it.restore()
        }
    }
}

fun Modifier.colorfilter(
    redScale: Float,
    greenScale: Float,
    blueScale: Float,
) = this.then(ColorFilterModifier(redScale, greenScale, blueScale))