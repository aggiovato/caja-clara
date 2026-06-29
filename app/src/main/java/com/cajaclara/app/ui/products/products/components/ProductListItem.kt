package com.cajaclara.app.ui.products.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.ui.designsystem.AppCard
import com.cajaclara.app.ui.designsystem.AppMoneyText
import com.cajaclara.app.ui.designsystem.AppProductImage
import com.cajaclara.app.ui.designsystem.AppStatusChip
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.products.categoryIcon
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.Instant

/** A product row in the list: thumbnail, name, status and cost/PVP/margin. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProductListItem(product: Product, categoryName: String?, onClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppProductImage(
                imagePath = product.imagePath,
                fallback = categoryIcon(categoryName),
                size = 56.dp,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProductName(product.name, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
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

/**
 * Product name, truncated with an ellipsis; long-press shows the full name in a tooltip.
 * The [modifier]'s weight is applied to a Box (reliable) so the status chip always keeps
 * its natural width.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductName(name: String, modifier: Modifier = Modifier) {
    Box(modifier) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = { PlainTooltip { Text(name) } },
            state = rememberTooltipState(),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LabeledMoney(label: String, money: Money) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AppMoneyText(money, style = MaterialTheme.typography.bodyMedium)
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
        ProductStatus.ARCHIVED -> Triple(
            "Archivado",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    AppStatusChip(text = text, containerColor = container, contentColor = content)
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

@LightPreview
@DarkPreview
@Composable
private fun ProductListItemPreview() {
    CajaClaraTheme {
        ProductListItem(previewProducts().first(), categoryName = "Bebidas", onClick = {})
    }
}
