package com.cajaclara.app.ui.purchases.purchaseform.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.valueobject.Margin
import com.cajaclara.app.ui.designsystem.AppTextField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.preview.PreviewSamples
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Dialog to add a product to the purchase: quantity, unit cost and whether to update its cost. */
@Composable
internal fun AddPurchaseLineDialog(
    product: Product,
    onConfirm: (quantity: Int, unitCost: Money, updateCost: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val quantity = rememberTextFieldState("1")
    val cost = rememberTextFieldState(product.currentCost.format().removeSuffix(" ${Money.CURRENCY_CODE}"))
    var updateCost by remember { mutableStateOf(false) }

    val parsedQty by remember { derivedStateOf { quantity.text.toString().trim().toIntOrNull() } }
    val parsedCost by remember { derivedStateOf { Money.fromPesosOrNull(cost.text.toString()) } }
    val valid = (parsedQty ?: 0) > 0 && parsedCost?.isNegative == false

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AppCornerRadius),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(product.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(quantity, Modifier.weight(1f), label = "Cantidad", placeholder = "1", keyboardType = KeyboardType.Number)
                    AppTextField(cost, Modifier.weight(1f), label = "Coste unidad", placeholder = "0,00", keyboardType = KeyboardType.Decimal)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Actualizar coste del producto", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = updateCost,
                        onCheckedChange = { updateCost = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                        ),
                    )
                }
                if (updateCost) MarginNotice(parsedCost, product)
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = { onConfirm(parsedQty!!, parsedCost!!, updateCost) },
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun MarginNotice(newCost: Money?, product: Product) {
    if (newCost == null || newCost == product.currentCost) return
    val margin = Margin(cost = newCost, price = product.currentPvp)
    val pct = margin.percentOnPrice
    val (text, color) = when {
        margin.isBelowCost -> "El PVP (${product.currentPvp.format()}) es menor que el nuevo coste" to MaterialTheme.colorScheme.error
        pct != null -> "Nuevo margen: ${"%.0f".format(pct)}%  ·  PVP ${product.currentPvp.format()}" to MaterialTheme.colorScheme.onSurfaceVariant
        else -> "" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    if (text.isNotEmpty()) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@LightPreview
@DarkPreview
@Composable
private fun AddPurchaseLineDialogPreview() {
    CajaClaraTheme {
        AddPurchaseLineDialog(product = PreviewSamples.product(), onConfirm = { _, _, _ -> }, onDismiss = {})
    }
}
