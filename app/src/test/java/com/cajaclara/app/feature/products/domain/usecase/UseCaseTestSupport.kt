package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import org.junit.Assert.fail
import java.time.Instant

/** Asserts [block] throws [T]; works with suspend calls inside `runTest`. */
inline fun <reified T : Throwable> assertThrowsOf(block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) return
        throw e
    }
    fail("Expected ${T::class.simpleName} but nothing was thrown")
}

/** A ready-to-use product for tests (unsaved, EPOCH timestamps). */
fun sampleProduct(
    name: String = "Coffee",
    cost: Money = Money.fromPesos("4,00"),
    pvp: Money = Money.fromPesos("10,00"),
    stock: Quantity = Quantity(5),
    status: ProductStatus = ProductStatus.ACTIVE,
): Product = Product(
    id = ProductId.UNSAVED,
    name = name,
    currentCost = cost,
    currentPvp = pvp,
    stockQuantity = stock,
    status = status,
    createdAt = Instant.EPOCH,
    updatedAt = Instant.EPOCH,
)
