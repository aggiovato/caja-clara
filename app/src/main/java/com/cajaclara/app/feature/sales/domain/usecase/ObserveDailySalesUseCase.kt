package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Observes the sales registered on a given [date] (defaults to today via the caller). */
class ObserveDailySalesUseCase(
    private val repository: SalesRepository,
) {
    operator fun invoke(date: LocalDate): Flow<List<Sale>> = repository.observeDailySales(date)
}
