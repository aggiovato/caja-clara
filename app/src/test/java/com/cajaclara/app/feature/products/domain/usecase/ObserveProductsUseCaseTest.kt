package com.cajaclara.app.feature.products.domain.usecase

import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveProductsUseCaseTest {

    private val repo = FakeProductRepository()
    private val observe = ObserveProductsUseCase(repo)

    @Test
    fun `emits the repository products`() = runTest {
        repo.seed(sampleProduct(name = "Coffee"))
        repo.seed(sampleProduct(name = "Water"))

        val products = observe().first()

        assertEquals(listOf("Coffee", "Water"), products.map { it.name })
    }

    @Test
    fun `passes the filter through to the repository`() = runTest {
        val filter = ProductFilter(status = ProductStatus.SOLD_OUT, query = "cof")

        observe(filter).first()

        assertEquals(filter, repo.lastFilter)
    }
}
