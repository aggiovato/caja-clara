package com.cajaclara.app.ui.stats.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.preview.PreviewSamples
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.max

private val dayLabelFormatter = DateTimeFormatter.ofPattern("d/M")

/**
 * Bar chart of daily profit with day labels under each bar (thinned out when there are many)
 * and a tooltip showing the date and exact profit when a bar is tapped.
 */
@Composable
internal fun ProfitBarChart(
    points: List<DailySalesPoint>,
    modifier: Modifier = Modifier,
) {
    val barColor = MaterialTheme.colorScheme.primary
    val negativeColor = MaterialTheme.colorScheme.error
    val baselineColor = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val tooltipBg = MaterialTheme.colorScheme.inverseSurface
    val tooltipText = MaterialTheme.colorScheme.inverseOnSurface
    val measurer = rememberTextMeasurer()

    var selected by remember(points) { mutableIntStateOf(-1) }

    val values = points.map { it.profit.cents }
    val maxValue = max(values.maxOrNull() ?: 0L, 0L)
    val minValue = minOf(values.minOrNull() ?: 0L, 0L)
    val span = (maxValue - minValue).coerceAtLeast(1L).toFloat()
    val labelStep = dayLabelStep(points.size)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .pointerInput(points) {
                detectTapGestures { tap ->
                    if (points.isNotEmpty()) {
                        val slot = size.width / points.size
                        val i = (tap.x / slot).toInt().coerceIn(0, points.size - 1)
                        selected = if (selected == i) -1 else i
                    }
                }
            },
    ) {
        if (values.isEmpty()) return@Canvas
        val labelHeight = 18.dp.toPx()
        val chartHeight = size.height - labelHeight
        val n = values.size
        val slot = size.width / n
        val barWidth = slot * 0.6f
        val gap = (slot - barWidth) / 2f
        val baselineY = chartHeight * (maxValue / span)

        drawLine(
            color = baselineColor,
            start = Offset(0f, baselineY),
            end = Offset(size.width, baselineY),
            strokeWidth = 1.dp.toPx(),
        )

        values.forEachIndexed { i, value ->
            val x = i * slot + gap
            val barHeight = (abs(value) / span) * chartHeight
            val color = if (value >= 0) barColor else negativeColor
            val topLeft = if (value >= 0) Offset(x, baselineY - barHeight) else Offset(x, baselineY)
            if (barHeight > 0f) {
                drawRoundRect(color = color, topLeft = topLeft, size = Size(barWidth, barHeight), cornerRadius = CornerRadius(4f, 4f))
            }
            // Day label (thinned), always including the last bar.
            if (i % labelStep == 0 || i == n - 1) {
                val label = measurer.measure(points[i].date.format(dayLabelFormatter), TextStyle(fontSize = 9.sp, color = labelColor))
                drawText(label, topLeft = Offset(x + barWidth / 2 - label.size.width / 2, chartHeight + 3.dp.toPx()))
            }
        }

        if (selected in values.indices) {
            drawTooltip(measurer, points[selected], selected, slot, tooltipBg, tooltipText)
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ProfitBarChartPreview() {
    CajaClaraTheme {
        Surface { ProfitBarChart(PreviewSamples.salesPoints(), Modifier.padding(16.dp)) }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTooltip(
    measurer: TextMeasurer,
    point: DailySalesPoint,
    index: Int,
    slot: Float,
    background: Color,
    textColor: Color,
) {
    val text: TextLayoutResult = measurer.measure(
        "${point.date.format(dayLabelFormatter)}  ${point.profit.format()}",
        TextStyle(fontSize = 11.sp, color = textColor),
    )
    val padding = 8.dp.toPx()
    val boxW = text.size.width + padding * 2
    val boxH = text.size.height + padding
    // Center over the bar, clamped to the canvas width.
    val centerX = index * slot + slot / 2
    val left = (centerX - boxW / 2).coerceIn(0f, size.width - boxW)
    drawRoundRect(
        color = background,
        topLeft = Offset(left, 0f),
        size = Size(boxW, boxH),
        cornerRadius = CornerRadius(8f, 8f),
    )
    drawText(text, topLeft = Offset(left + padding, padding / 2))
}
