package com.cajaclara.app.ui.products.productform

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.data.CameraTarget
import com.cajaclara.app.feature.products.data.ImageStore
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.usecase.ArchiveProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.GetProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.usecase.PauseProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.ResumeProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.SuggestSkuUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductCostUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductPvpUseCase
import com.cajaclara.app.feature.products.domain.usecase.UpdateProductUseCase
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.stock.domain.model.StockMovement
import com.cajaclara.app.feature.stock.domain.repository.StockRepository
import com.cajaclara.app.feature.stock.domain.usecase.AdjustStockUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

private class FakeCategoryRepository(categories: List<Category>) : CategoryRepository {
    private val flow = MutableStateFlow(categories)
    override fun observeCategories(): Flow<List<Category>> = flow
}

private class FakeImageStore : ImageStore {
    override suspend fun saveProductImage(uri: Uri): String = "fake/path.jpg"
    override fun newCameraTarget(): CameraTarget = throw UnsupportedOperationException()
}

private class FakeStockRepository : StockRepository {
    override suspend fun record(movement: StockMovement) {}
    override fun observeMovements(productId: ProductId) = flowOf(emptyList<StockMovement>())
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProductFormViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val productRepo = FakeProductRepository()
    private val clock = Clock.fixed(Instant.parse("2026-06-28T10:00:00Z"), ZoneOffset.UTC)
    private val categories = listOf(
        Category(CategoryId(1), "Bebidas", null, Instant.EPOCH),
        Category(CategoryId(5), "Otros", null, Instant.EPOCH),
    )

    private fun viewModel() = ProductFormViewModel(
        createProduct = CreateProductUseCase(productRepo, clock),
        getProduct = GetProductUseCase(productRepo),
        updateProduct = UpdateProductUseCase(productRepo, clock),
        updateCost = UpdateProductCostUseCase(productRepo, clock),
        updatePvp = UpdateProductPvpUseCase(productRepo, clock),
        pauseProduct = PauseProductUseCase(productRepo, clock),
        resumeProduct = ResumeProductUseCase(productRepo, clock),
        archiveProduct = ArchiveProductUseCase(productRepo, clock),
        adjustStock = AdjustStockUseCase(productRepo, FakeStockRepository(), clock),
        suggestSku = SuggestSkuUseCase(),
        observeCategories = ObserveCategoriesUseCase(FakeCategoryRepository(categories)),
        imageStore = FakeImageStore(),
        savedStateHandle = SavedStateHandle(),
    )

    @Test
    fun `defaults to the Otros category`() = runTest {
        assertEquals("Otros", viewModel().state.value.selectedCategory?.name)
    }

    @Test
    fun `saving valid data creates the product and marks saved`() = runTest {
        val vm = viewModel()
        vm.save(name = "Café", costText = "2,10", pvpText = "3,50", stockText = "28", sku = "", description = "")

        assertTrue(vm.state.value.saved)
        assertEquals(1, productRepo.stored.size)
        val product = productRepo.stored.values.first()
        assertEquals("Café", product.name)
        assertEquals(Money(210), product.currentCost)
        assertEquals(5L, product.categoryId?.value)
    }

    @Test
    fun `blank SKU falls back to the name-based suggestion`() = runTest {
        val vm = viewModel()
        vm.save(name = "Café molido", costText = "2,10", pvpText = "3,50", stockText = "1", sku = "", description = "")
        assertEquals("cafe-molido", productRepo.stored.values.first().sku)
    }

    @Test
    fun `explicit SKU is kept over the suggestion`() = runTest {
        val vm = viewModel()
        vm.save(name = "Café molido", costText = "2,10", pvpText = "3,50", stockText = "1", sku = "CM-01", description = "")
        assertEquals("CM-01", productRepo.stored.values.first().sku)
    }

    @Test
    fun `blank name shows an error and does not save`() = runTest {
        val vm = viewModel()
        vm.save(name = "  ", costText = "1,00", pvpText = "2,00", stockText = "", sku = "", description = "")

        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.saved)
        assertEquals(0, productRepo.stored.size)
    }

    @Test
    fun `invalid cost shows an error`() = runTest {
        val vm = viewModel()
        vm.save(name = "Café", costText = "abc", pvpText = "2,00", stockText = "", sku = "", description = "")

        assertNotNull(vm.state.value.error)
        assertEquals(0, productRepo.stored.size)
    }
}
