package com.pedro.strikersrecomp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Campo listrado desenhado em Canvas: fundo de todas as telas. */
@Composable
fun Pitch(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val stripes = 9
        val stripeW = size.width / stripes
        for (i in 0 until stripes) {
            drawRect(
                color = if (i % 2 == 0) Turf.Dark else Turf.Light,
                topLeft = Offset(i * stripeW, 0f),
                size = Size(stripeW + 1f, size.height)
            )
        }
        val chalk = Turf.Chalk.copy(alpha = 0.14f)
        val sw = 3.dp.toPx()
        val cx = size.width / 2f
        val cy = size.height * 0.5f
        drawLine(chalk, Offset(0f, cy), Offset(size.width, cy), sw)
        drawCircle(chalk, radius = size.width * 0.28f, center = Offset(cx, cy), style = Stroke(sw))
        drawCircle(chalk, radius = 6.dp.toPx(), center = Offset(cx, cy))
        val boxW = size.width * 0.6f
        val boxH = size.height * 0.12f
        drawRect(chalk, Offset((size.width - boxW) / 2f, 0f), Size(boxW, boxH), style = Stroke(sw))
        drawRect(chalk, Offset((size.width - boxW) / 2f, size.height - boxH), Size(boxW, boxH), style = Stroke(sw))
        // Escurece topo e base para o texto ficar legível, como luz de refletor.
        drawRect(
            brush = Brush.verticalGradient(
                listOf(Turf.Ink.copy(alpha = 0.75f), Color.Transparent, Turf.Ink.copy(alpha = 0.88f))
            ),
            size = size
        )
    }
}

/** Raio do Megastrike. */
@Composable
fun Bolt(modifier: Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.58f, 0f)
            lineTo(w * 0.20f, h * 0.56f)
            lineTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.36f, h)
            lineTo(w * 0.82f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.38f)
            lineTo(w * 0.70f, 0f)
            close()
        }
        drawPath(path, color)
    }
}
