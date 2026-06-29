package com.cajaclara.app.ui.sales.sales.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.designsystem.AppPrimaryButton
import com.cajaclara.app.ui.designsystem.AppTextField
import com.cajaclara.app.ui.designsystem.ScrollToBottomButton
import com.cajaclara.app.ui.sales.sales.SalesUiState
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

/** Bottom-sheet content: today's registered sales, the forecast total, and the cash close. */
@Composable
internal fun DailySalesSheet(
    state: SalesUiState,
    isSaving: Boolean,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Ventas de hoy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Scrollable block (capped height): total, sales list and the close summary. The cash
        // close form below stays fixed so its input and button never get squeezed. A floating
        // "scroll to end" button appears while there is more content below.
        val scrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        Box {
            Column(
                modifier = Modifier
                    .heightIn(max = 340.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SummaryRow("Total previsto", state.dailyExpected, emphasize = true)
                HorizontalDivider()

                if (state.dailySales.isEmpty()) {
                    Text(
                        "Aún no hay ventas registradas hoy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    state.dailySales.forEach { sale -> SaleRow(sale) }
                }

                if (state.isDayClosed) {
                    HorizontalDivider()
                    CashCloseSummary(state.cashClose!!, currentExpected = state.dailyExpected)
                }
            }
            ScrollToBottomButton(
                visible = scrollState.canScrollForward,
                onClick = { scope.launch { scrollState.animateScrollTo(scrollState.maxValue) } },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
            )
        }

        HorizontalDivider()

        CashCloseForm(
            expected = state.dailyExpected,
            isReClose = state.isDayClosed,
            isSaving = isSaving,
            onClose = onClose,
        )
    }
}

@Composable
private fun SaleRow(sale: Sale) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = timeFormatter.format(sale.soldAt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AppMoneyText(sale.totalRevenue, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SummaryRow(label: String, amount: Money, emphasize: Boolean = false, color: androidx.compose.ui.graphics.Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val style = if (emphasize) {
            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        } else {
            MaterialTheme.typography.bodyLarge
        }
        AppMoneyText(amount, style = style, color = color ?: androidx.compose.ui.graphics.Color.Unspecified)
    }
}

@Composable
private fun CashCloseSummary(close: CashClose, currentExpected: Money) {
    val diffColor = if (close.difference.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    // Sales registered after this close (the day's expected total grew since then).
    val newSales = currentExpected - close.expectedRevenue

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = if (close.isBalanced) "✓ Caja cuadrada" else "Caja cerrada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
        SummaryRow("Cerraste con", close.countedCash)
        SummaryRow(
            label = if (close.difference.isNegative) "Faltante" else "Sobrante",
            amount = close.difference,
            color = diffColor,
        )
        SummaryRow("Ganancia", close.profit, emphasize = true, color = MaterialTheme.colorScheme.secondary)

        if (newSales.isPositive) {
            HorizontalDivider()
            SummaryRow("Ventas nuevas desde el cierre", newSales, color = MaterialTheme.colorScheme.primary)
            Text(
                "Conviene recuadrar para incluirlas.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun CashCloseForm(expected: Money, isReClose: Boolean, isSaving: Boolean, onClose: (String) -> Unit) {
    val counted = rememberTextFieldState()
    // On re-close, prefill with the current expected total so the user only adjusts the delta.
    LaunchedEffect(isReClose) {
        if (isReClose && counted.text.isEmpty()) {
            counted.setTextAndPlaceCursorAtEnd(expected.format().removeSuffix(" ${Money.CURRENCY_CODE}"))
        }
    }
    // Live difference preview as the user types the counted cash.
    val difference by remember {
        derivedStateOf { Money.fromPesosOrNull(counted.text.toString())?.let { it - expected } }
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AppTextField(
            counted,
            label = "Efectivo contado",
            placeholder = "0,00",
            keyboardType = KeyboardType.Decimal,
        )
        difference?.let { diff ->
            val color = if (diff.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (diff.isNegative) "Falta" else if (diff.isPositive) "Sobra" else "Cuadra",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                )
                AppMoneyText(diff, style = MaterialTheme.typography.bodyLarge, color = color)
            }
        }
        AppPrimaryButton(
            text = if (isReClose) "Recuadrar caja" else "Cuadrar caja",
            onClick = { onClose(counted.text.toString()) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
