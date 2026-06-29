package com.cajaclara.app.feature.sales.domain.usecase

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.feature.sales.domain.model.CashClose
import com.cajaclara.app.feature.sales.domain.repository.CashCloseRepository
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate

/**
 * Closes today's cash: snapshots the day's registered sales (revenue and cost), records the
 * real [countedCash], and persists a [CashClose]. Re-closing the same day overwrites it
 * (recuadre), so the latest count always wins.
 */
class CloseCashUseCase(
    private val salesRepository: SalesRepository,
    private val cashCloseRepository: CashCloseRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(countedCash: Money, note: String? = null): CashClose {
        require(!countedCash.isNegative) { "Counted cash cannot be negative" }
        val today = LocalDate.now(clock)
        val sales = salesRepository.observeDailySales(today).first()

        val revenue = sales.fold(Money.ZERO) { acc, s -> acc + s.totalRevenue }
        val cost = sales.fold(Money.ZERO) { acc, s -> acc + s.totalCost }
        val close = CashClose(
            date = today,
            expectedRevenue = revenue,
            expectedCost = cost,
            countedCash = countedCash,
            salesCount = sales.size,
            closedAt = clock.instant(),
            note = note,
        )
        cashCloseRepository.save(close)
        return close
    }
}
