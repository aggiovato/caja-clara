package com.cajaclara.app.ui.sales.sales.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.designsystem.AppProductImage
import com.cajaclara.app.ui.products.categoryIcon
import com.cajaclara.app.ui.theme.AppCornerRadius

/** A product row in quick sale: thumbnail, name/price/stock and an add / quantity stepper. */
@Composable
internal fun SaleProductItem(
    product: Product,
    categoryName: String?,
    quantityInCart: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    AppCard {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppProductImage(product.imagePath, categoryIcon(categoryName), size = 48.dp)
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                AppMoneyText(product.currentPvp, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Stock: ${product.stockQuantity.value}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            QuantityStepper(
                quantity = quantityInCart,
                canAdd = quantityInCart < product.stockQuantity.value,
                onAdd = onAdd,
                onRemove = onRemove,
            )
        }
    }
}

@Composable
private fun QuantityStepper(quantity: Int, canAdd: Boolean, onAdd: () -> Unit, onRemove: () -> Unit) {
    if (quantity == 0) {
        StepButton(Icons.Filled.Add, "Añadir", enabled = canAdd, onClick = onAdd)
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StepButton(Icons.Filled.Remove, "Quitar uno", enabled = true, onClick = onRemove)
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            StepButton(Icons.Filled.Add, "Añadir uno", enabled = canAdd, onClick = onAdd)
        }
    }
}

@Composable
private fun StepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val container = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val content = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(AppCornerRadius),
        color = container,
        contentColor = content,
        modifier = Modifier.size(36.dp),
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.padding(6.dp))
    }
}
