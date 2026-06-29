package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.model.StockMovementType
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import java.time.Clock

/** One requested item in a sale: how many units of which product. */
data class SaleItem(val productId: ProductId, val quantity: Quantity)

/** Thrown when a requested quantity exceeds a product's on-hand stock. */
class InsufficientStockException(val productName: String, val available: Quantity) :
    IllegalStateException("Not enough stock for $productName (available: ${available.value})")

/**
 * Registers a sale from a list of [SaleItem]s. For each item it snapshots the product's name,
 * cost and price, builds a [SaleLine], and persists the [Sale] with its lines. It then deducts
 * the sold quantity from each product, logging an OUT [StockMovement] and marking the product
 * SOLD_OUT when it reaches zero.
 *
 * @throws IllegalArgumentException if the cart is empty or a quantity is not positive
 * @throws NoSuchElementException if a product does not exist
 * @throws IllegalStateException if a product is not active, or [InsufficientStockException]
 */
class RegisterSaleUseCase(
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(items: List<SaleItem>, note: String? = null): SaleId {
        require(items.isNotEmpty()) { "A sale must have at least one item" }
        require(items.all { it.quantity.value > 0 }) { "Quantities must be positive" }

        val now = clock.instant()

        // Load + validate every product first, building the snapshot lines.
        val resolved = items.map { item ->
            val product = productRepository.getProduct(item.productId)
                ?: throw NoSuchElementException("Product not found: ${item.productId}")
            check(product.status == ProductStatus.ACTIVE) { "Product is not active: ${product.name}" }
            if (item.quantity > product.stockQuantity) {
                throw InsufficientStockException(product.name, product.stockQuantity)
            }
            product to SaleLine(
                saleId = SaleId.UNSAVED,
                productId = product.id,
                productNameSnapshot = product.name,
                quantity = item.quantity,
                unitCostSnapshot = product.currentCost,
                unitPvpSnapshot = product.currentPvp,
            )
        }

        val lines = resolved.map { it.second }
        val sale = Sale(
            id = SaleId.UNSAVED,
            soldAt = now,
            totalRevenue = lines.fold(Money.ZERO) { acc, l -> acc + l.lineRevenue },
            totalCost = lines.fold(Money.ZERO) { acc, l -> acc + l.lineCost },
            note = note,
            createdAt = now,
        )
        val saleId = salesRepository.registerSale(sale, lines)

        // Deduct stock and log an OUT movement per product.
        resolved.forEach { (product, line) ->
            val newStock = product.stockQuantity - line.quantity
            stockRepository.record(
                StockMovement(
                    productId = product.id,
                    type = StockMovementType.OUT,
                    quantity = line.quantity,
                    createdAt = now,
                ),
            )
            val newStatus = if (newStock.isZero) ProductStatus.SOLD_OUT else ProductStatus.ACTIVE
            productRepository.updateProduct(
                product.copy(stockQuantity = newStock, status = newStatus, updatedAt = now),
            )
        }
        return saleId
    }
}
