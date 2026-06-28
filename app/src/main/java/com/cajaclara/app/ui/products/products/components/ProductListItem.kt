package com.cajaclara.app.ui.products.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.MoneyText
import com.cajaclara.app.ui.designsystem.ProductImage
import com.cajaclara.app.ui.designsystem.StatusChip
import com.cajaclara.app.ui.products.categoryIcon
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.Instant

/** A product row in the list: thumbnail, name, status and cost/PVP/margin. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProductListItem(product: Product, categoryName: String?) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProductImage(
                imagePath = product.imagePath,
                fallback = categoryIcon(categoryName),
                size = 56.dp,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    ProductStatusChip(product)
                }
                // FlowRow so that when cost/PVP are very large, the margin wraps to a second
                // line instead of being squeezed.
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LabeledMoney("Coste", product.currentCost)
                    LabeledMoney("PVP", product.currentPvp)
                    MarginColumn(product)
                }
            }
        }
    }
}

@Composable
private fun LabeledMoney(label: String, money: Money) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        MoneyText(money, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MarginColumn(product: Product) {
    val margin = product.margin
    val text = margin.percentOnPrice?.let { "%.0f%%".format(it) } ?: "—"
    val color = if (margin.isBelowCost) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
    Column {
        Text("Margen", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProductStatusChip(product: Product) {
    val (text, container, content) = when (product.status) {
        ProductStatus.ACTIVE -> Triple(
            "En stock (${product.stockQuantity.value})",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )
        ProductStatus.SOLD_OUT -> Triple(
            "Agotado",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )
        ProductStatus.PAUSED -> Triple(
            "Pausado",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    StatusChip(text = text, containerColor = container, contentColor = content)
}

// ---- Preview sample data (shared by this screen's previews) ----

internal fun previewProduct(name: String, status: ProductStatus, stock: Int, cost: String, pvp: String) = Product(
    id = ProductId(name.hashCode().toLong()),
    name = name,
    currentCost = Money.fromPesos(cost),
    currentPvp = Money.fromPesos(pvp),
    stockQuantity = Quantity(stock),
    status = status,
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH,
)

internal fun previewProducts() = listOf(
    previewProduct("Café molido", ProductStatus.ACTIVE, 28, "2,10", "3,50"),
    previewProduct("Agua 1.5L", ProductStatus.ACTIVE, 54, "0,35", "0,65"),
    previewProduct("Chocolate", ProductStatus.SOLD_OUT, 0, "0,90", "1,50"),
)

@Preview(showBackground = true)
@Composable
private fun ProductListItemPreview() {
    CajaClaraTheme {
        ProductListItem(previewProducts().first(), categoryName = "Bebidas")
    }
}
