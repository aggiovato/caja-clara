package com.cajaclara.app.feature.settings.domain.usecase

import com.cajaclara.app.feature.products.domain.usecase.FakeSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateStoreAddressUseCaseTest {

    @Test
    fun `stores the trimmed address`() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = UpdateStoreAddressUseCase(repository)

        useCase("  Calle 23 #456, La Habana  ")

        assertEquals("Calle 23 #456, La Habana", repository.observe().first().storeAddress)
    }

    @Test
    fun `keeps the minimum margin untouched`() = runTest {
        val repository = FakeSettingsRepository(minMarginPercent = 40.0)
        val useCase = UpdateStoreAddressUseCase(repository)

        useCase("Somewhere")

        assertEquals(40.0, repository.observe().first().minMarginPercent, 0.0)
    }
}
