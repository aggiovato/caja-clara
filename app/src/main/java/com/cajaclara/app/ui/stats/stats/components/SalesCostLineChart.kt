package com.cajaclara.app.ui.stats.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import java.time.format.DateTimeFormatter
import kotlin.math.max

private val dayFormatter = DateTimeFormatter.ofPattern("d/M")

/** Connected-points line chart of daily revenue and cost, with a legend and day labels. */
@Composable
internal fun SalesCostLineChart(
    points: List<DailySalesPoint>,
    modifier: Modifier = Modifier,
) {
    val revenueColor = MaterialTheme.colorScheme.primary
    val costColor = MaterialTheme.colorScheme.error
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outline
    val measurer = rememberTextMeasurer()

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendItem("Ventas", revenueColor)
            LegendItem("Costes", costColor)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
        ) {
            if (points.isEmpty()) return@Canvas
            val labelHeight = 18.dp.toPx()
            val chartHeight = size.height - labelHeight
            val maxValue = max(
                points.maxOf { it.revenue.cents },
                points.maxOf { it.cost.cents },
            ).coerceAtLeast(1L).toFloat()

            fun y(cents: Long) = chartHeight - (cents / maxValue) * chartHeight
            fun x(i: Int) = if (points.size == 1) size.width / 2 else size.width * i / (points.size - 1)

            // Baseline.
            drawLine(gridColor, Offset(0f, chartHeight), Offset(size.width, chartHeight), strokeWidth = 1.dp.toPx())

            drawSeries(points.map { it.revenue.cents }, ::x, ::y, revenueColor)
            drawSeries(points.map { it.cost.cents }, ::x, ::y, costColor)

            // Day labels (thinned).
            val step = dayLabelStep(points.size)
            points.forEachIndexed { i, p ->
                if (i % step == 0 || i == points.size - 1) {
                    val label = measurer.measure(p.date.format(dayFormatter), TextStyle(fontSize = 9.sp, color = labelColor))
                    drawText(label, topLeft = Offset((x(i) - label.size.width / 2).coerceIn(0f, size.width - label.size.width), chartHeight + 3.dp.toPx()))
                }
            }
        }
    }
}

private fun DrawScope.drawSeries(values: List<Long>, x: (Int) -> Float, y: (Long) -> Float, color: Color) {
    val path = Path()
    values.forEachIndexed { i, v ->
        val px = x(i)
        val py = y(v)
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
    values.forEachIndexed { i, v -> drawCircle(color, radius = 3.dp.toPx(), center = Offset(x(i), y(v))) }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
