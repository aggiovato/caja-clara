package com.cajaclara.app.feature.stock.domain.usecase

import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.ProductRepository
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.model.StockMovementType
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import java.time.Clock

/**
 * Sets a product's stock to [newQuantity], recording the change as a [StockMovement]:
 * an increase is logged as [StockMovementType.IN], a decrease without a sale as
 * [StockMovementType.ADJUSTMENT]. Auto-updates the status: reaching 0 → SOLD_OUT, going
 * above 0 from sold out → ACTIVE. PAUSED/ARCHIVED are left untouched. No-op if unchanged.
 */
class AdjustStockUseCase(
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(productId: ProductId, newQuantity: Quantity, note: String? = null) {
        val product = productRepository.getProduct(productId)
            ?: throw NoSuchElementException("Product not found: $productId")

        val old = product.stockQuantity
        if (old == newQuantity) return

        val now = clock.instant()
        val increased = newQuantity > old
        stockRepository.record(
            StockMovement(
                productId = productId,
                type = if (increased) StockMovementType.IN else StockMovementType.ADJUSTMENT,
                quantity = if (increased) newQuantity - old else old - newQuantity,
                note = note,
                createdAt = now,
            ),
        )

        val newStatus = when (product.status) {
            ProductStatus.ACTIVE, ProductStatus.SOLD_OUT ->
                if (newQuantity.isZero) ProductStatus.SOLD_OUT else ProductStatus.ACTIVE
            ProductStatus.PAUSED, ProductStatus.ARCHIVED -> product.status
        }
        productRepository.updateProduct(
            product.copy(stockQuantity = newQuantity, status = newStatus, updatedAt = now),
        )
    }
}
