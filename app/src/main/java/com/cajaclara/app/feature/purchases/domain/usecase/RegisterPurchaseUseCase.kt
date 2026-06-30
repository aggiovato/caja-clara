package com.cajaclara.app.feature.purchases.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.PriceHistoryEntry
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.purchases.domain.model.PurchaseLine
import com.cajaclara.app.feature.purchases.domain.repository.PurchasesRepository
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.model.StockMovementType
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import java.time.Clock

/**
 * One requested item in a purchase: how many units of which product, at what unit cost, and
 * whether to update the product's current cost to this purchase's cost.
 */
data class PurchaseItem(
    val productId: ProductId,
    val quantity: Quantity,
    val unitCost: Money,
    val updateProductCost: Boolean = true,
)

/**
 * Registers a purchase: persists it as an investment, raises each product's stock with an IN
 * [StockMovement], and (when requested) updates the product's current cost to the purchase
 * cost — appending a [PriceHistoryEntry]. The PVP is never changed automatically. A sold-out
 * product becomes active again once it has stock.
 *
 * @throws IllegalArgumentException if the list is empty or a quantity/cost is invalid
 * @throws NoSuchElementException if a product does not exist
 */
class RegisterPurchaseUseCase(
    private val purchasesRepository: PurchasesRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(items: List<PurchaseItem>, note: String? = null): PurchaseId {
        require(items.isNotEmpty()) { "A purchase must have at least one item" }
        require(items.all { it.quantity.value > 0 }) { "Quantities must be positive" }
        require(items.all { !it.unitCost.isNegative }) { "Unit cost must not be negative" }

        val now = clock.instant()

        val resolved = items.map { item ->
            val product = productRepository.getProduct(item.productId)
                ?: throw NoSuchElementException("Product not found: ${item.productId}")
            product to item
        }

        val lines = resolved.map { (product, item) ->
            PurchaseLine(
                purchaseId = PurchaseId.UNSAVED,
                productId = product.id,
                productNameSnapshot = product.name,
                quantity = item.quantity,
                unitCost = item.unitCost,
            )
        }

        val purchase = Purchase(
            id = PurchaseId.UNSAVED,
            purchasedAt = now,
            totalInvestment = lines.fold(Money.ZERO) { acc, l -> acc + l.lineTotal },
            note = note,
            createdAt = now,
        )
        val purchaseId = purchasesRepository.registerPurchase(purchase, lines)

        resolved.forEach { (product, item) ->
            val newStock = product.stockQuantity + item.quantity
            stockRepository.record(
                StockMovement(
                    productId = product.id,
                    type = StockMovementType.IN,
                    quantity = item.quantity,
                    note = "Compra",
                    createdAt = now,
                ),
            )

            val updateCost = item.updateProductCost && item.unitCost != product.currentCost
            if (updateCost) {
                productRepository.addPriceHistory(
                    PriceHistoryEntry(
                        productId = product.id,
                        oldCost = product.currentCost,
                        newCost = item.unitCost,
                        oldPvp = product.currentPvp,
                        newPvp = product.currentPvp,
                        reason = "Compra",
                        createdAt = now,
                    ),
                )
            }
            // A sold-out product comes back as active once restocked; others keep their status.
            val newStatus = if (product.status == ProductStatus.SOLD_OUT && !newStock.isZero) {
                ProductStatus.ACTIVE
            } else {
                product.status
            }
            productRepository.updateProduct(
                product.copy(
                    stockQuantity = newStock,
                    currentCost = if (updateCost) item.unitCost else product.currentCost,
                    status = newStatus,
                    updatedAt = now,
                ),
            )
        }
        return purchaseId
    }
}
