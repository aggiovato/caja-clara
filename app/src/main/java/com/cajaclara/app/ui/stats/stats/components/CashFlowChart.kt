package com.cajaclara.app.ui.stats.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.preview.PreviewSamples
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.format.DateTimeFormatter
import kotlin.math.max

private val dayFormatter = DateTimeFormatter.ofPattern("d/M")

/**
 * Cash-flow chart: each day shows sales going up (green) and purchase investment going down
 * (red) from a central baseline, on a shared scale. Cash flow, not sales profit.
 */
@Composable
internal fun CashFlowChart(
    points: List<DailyCashPoint>,
    modifier: Modifier = Modifier,
) {
    val salesColor = MaterialTheme.colorScheme.secondary
    val purchaseColor = MaterialTheme.colorScheme.error
    val baselineColor = MaterialTheme.colorScheme.outline
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val measurer = rememberTextMeasurer()

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendDot("Ventas", salesColor)
            LegendDot("Compras", purchaseColor)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
        ) {
            if (points.isEmpty()) return@Canvas
            val labelHeight = 18.dp.toPx()
            val plotHeight = size.height - labelHeight
            val baselineY = plotHeight / 2f
            val maxMagnitude = max(
                points.maxOf { it.salesRevenue.cents },
                points.maxOf { it.purchaseInvestment.cents },
            ).coerceAtLeast(1L).toFloat()
            val halfHeight = baselineY

            val n = points.size
            val slot = size.width / n
            val barWidth = slot * 0.5f
            val gap = (slot - barWidth) / 2f

            drawLine(baselineColor, Offset(0f, baselineY), Offset(size.width, baselineY), strokeWidth = 1.dp.toPx())

            val step = dayLabelStep(n)
            points.forEachIndexed { i, p ->
                val x = i * slot + gap
                val salesH = (p.salesRevenue.cents / maxMagnitude) * halfHeight
                val purchaseH = (p.purchaseInvestment.cents / maxMagnitude) * halfHeight
                if (salesH > 0f) {
                    drawRoundRect(salesColor, Offset(x, baselineY - salesH), Size(barWidth, salesH), CornerRadius(4f, 4f))
                }
                if (purchaseH > 0f) {
                    drawRoundRect(purchaseColor, Offset(x, baselineY), Size(barWidth, purchaseH), CornerRadius(4f, 4f))
                }
                if (i % step == 0 || i == n - 1) {
                    val label = measurer.measure(p.date.format(dayFormatter), TextStyle(fontSize = 9.sp, color = labelColor))
                    drawText(label, topLeft = Offset(x + barWidth / 2 - label.size.width / 2, plotHeight + 3.dp.toPx()))
                }
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@LightPreview
@DarkPreview
@Composable
private fun CashFlowChartPreview() {
    CajaClaraTheme {
        Surface { CashFlowChart(PreviewSamples.cashPoints(), Modifier.padding(16.dp)) }
    }
}
