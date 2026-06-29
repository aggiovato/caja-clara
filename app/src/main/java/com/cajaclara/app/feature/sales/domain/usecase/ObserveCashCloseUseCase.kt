package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Observes the cash close for a [date] (null while the day is still open). */
class ObserveCashCloseUseCase(
    private val repository: CashCloseRepository,
) {
    operator fun invoke(date: LocalDate): Flow<CashClose?> = repository.observeClose(date)
}
