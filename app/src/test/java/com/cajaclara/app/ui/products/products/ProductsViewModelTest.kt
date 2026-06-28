package com.cajaclara.app.ui.products.products

import app.cash.turbine.test
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveProductsUseCase
import com.cajaclara.app.feature.products.domain.usecase.sampleProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class FakeCategoryRepository(private val categories: List<Category> = emptyList()) : CategoryRepository {
    override fun observeCategories(): Flow<List<Category>> = flowOf(categories)
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repo: FakeProductRepository) =
        ProductsViewModel(ObserveProductsUseCase(repo), ObserveCategoriesUseCase(FakeCategoryRepository()))

    @Test
    fun `emits the products from the repository`() = runTest {
        val repo = FakeProductRepository()
        repo.seed(sampleProduct(name = "Coffee"))
        repo.seed(sampleProduct(name = "Water"))
        val vm = viewModel(repo)

        vm.uiState.test {
            var item = awaitItem()
            while (item.products.isEmpty()) item = awaitItem()
            assertEquals(listOf("Coffee", "Water"), item.products.map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing the status filter re-queries the repository`() = runTest {
        val repo = FakeProductRepository()
        repo.seed(sampleProduct(name = "Coffee"))
        val vm = viewModel(repo)

        vm.uiState.test {
            vm.onStatusChange(ProductStatus.SOLD_OUT)
            var item = awaitItem()
            while (item.filter.status != ProductStatus.SOLD_OUT) item = awaitItem()
            assertEquals(ProductStatus.SOLD_OUT, repo.lastFilter?.status)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
