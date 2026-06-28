package com.cajaclara.app.ui.products.productform

import android.net.Uri
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.products.data.CameraTarget
import com.cajaclara.app.feature.products.data.ImageStore
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.repository.CategoryRepository
import com.cajaclara.app.feature.products.domain.usecase.CreateProductUseCase
import com.cajaclara.app.feature.products.domain.usecase.FakeProductRepository
import com.cajaclara.app.feature.products.domain.usecase.ObserveCategoriesUseCase
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
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
        observeCategories = ObserveCategoriesUseCase(FakeCategoryRepository(categories)),
        imageStore = FakeImageStore(),
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
        assertEquals(CategoryId(5), product.categoryId)
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
