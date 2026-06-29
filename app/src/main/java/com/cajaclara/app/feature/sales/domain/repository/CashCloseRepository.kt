package com.cajaclara.app.feature.sales.domain.repository

import com.cajaclara.app.feature.sales.domain.model.CashClose
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Port for cash-close persistence. One row per day; saving the same day overwrites it. */
interface CashCloseRepository {

    /** Observe the cash close for [date], or null if the day is still open. */
    fun observeClose(date: LocalDate): Flow<CashClose?>

    /** Persist (insert or replace) a day's cash close. */
    suspend fun save(close: CashClose)
}
