package com.cajaclara.app.ui.designsystem

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** How a decorative circle is painted: outline ring, solid disc, or a striped (hatched) fill. */
private enum class CircleStyle { OUTLINE, FILLED, STRIPED }

/** A decorative circle: position as a fraction of the canvas, radius in dp, and its style. */
private data class DecorCircle(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val style: CircleStyle,
    val alpha: Float,
    val phase: Float,
    val driftDp: Float,
)

// A few circles tucked around the edges so the content area stays clean.
private val circles = listOf(
    DecorCircle(xFraction = 0.85f, yFraction = 0.08f, radiusDp = 120f, style = CircleStyle.OUTLINE, alpha = 0.18f, phase = 0f, driftDp = 14f),
    DecorCircle(xFraction = 0.12f, yFraction = 0.22f, radiusDp = 70f, style = CircleStyle.STRIPED, alpha = 0.16f, phase = 1.6f, driftDp = 18f),
    DecorCircle(xFraction = 0.05f, yFraction = 0.92f, radiusDp = 150f, style = CircleStyle.OUTLINE, alpha = 0.12f, phase = 3.1f, driftDp = 12f),
    DecorCircle(xFraction = 0.92f, yFraction = 0.8f, radiusDp = 95f, style = CircleStyle.FILLED, alpha = 0.06f, phase = 4.5f, driftDp = 16f),
    // A smaller striped circle overlapping the solid one above for a layered accent.
    DecorCircle(xFraction = 0.74f, yFraction = 0.86f, radiusDp = 58f, style = CircleStyle.STRIPED, alpha = 0.14f, phase = 5.4f, driftDp = 13f),
)

/**
 * App background: the theme background color with a few primary-tinted circles (one outlined,
 * one translucent disc) that gently float around their resting position. Place behind content;
 * keep the foreground containers transparent so it shows through the gaps.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background

    // A single looping 0..1 progress drives every circle; each has its own phase/amplitude,
    // so they oscillate out of sync for an organic floating feel.
    val transition = rememberInfiniteTransition(label = "bgFloat")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            // Very slow loop so the drift is barely perceptible and never distracting.
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "bgProgress",
    )

    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(background)
            val angle = progress * 2f * PI.toFloat()
            circles.forEach { c -> drawDecorCircle(c, primary, angle) }
        }
        content()
    }
}

private fun DrawScope.drawDecorCircle(c: DecorCircle, primary: Color, angle: Float) {
    val drift = c.driftDp.dp.toPx()
    // Elliptical drift around the resting point (sin/cos with a per-circle phase offset).
    val dx = cos(angle + c.phase) * drift
    val dy = sin(angle + c.phase) * drift * 0.7f
    val center = Offset(size.width * c.xFraction + dx, size.height * c.yFraction + dy)
    val radius = c.radiusDp.dp.toPx()
    val color = primary.copy(alpha = c.alpha)
    when (c.style) {
        CircleStyle.FILLED -> drawCircle(color = color, radius = radius, center = center)
        CircleStyle.OUTLINE -> drawCircle(color = color, radius = radius, center = center, style = Stroke(width = 2.dp.toPx()))
        CircleStyle.STRIPED -> drawStripedCircle(color, center, radius)
    }
}

/** Hatched fill: diagonal stripes clipped to the circle. */
private fun DrawScope.drawStripedCircle(color: Color, center: Offset, radius: Float) {
    val circlePath = Path().apply {
        addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
    }
    val spacing = 9.dp.toPx()
    val strokeWidth = 2.dp.toPx()
    clipPath(circlePath) {
        // 45° lines (slope 1). Sweep the top x-intercept wide enough that every line crossing
        // the circle is drawn; the clip trims the overshoot so the whole disc gets striped.
        var x = center.x - 3 * radius
        val endX = center.x + 3 * radius
        while (x <= endX) {
            drawLine(
                color = color,
                start = Offset(x, center.y - radius),
                end = Offset(x + 2 * radius, center.y + radius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            x += spacing
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AppBackgroundPreview() {
    CajaClaraTheme {
        AppBackground { Box(Modifier.fillMaxSize()) }
    }
}
