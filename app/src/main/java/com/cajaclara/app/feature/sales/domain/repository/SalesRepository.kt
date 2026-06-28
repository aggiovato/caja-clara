package com.cajaclara.app.feature.sales.domain.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Port for sales persistence (section 14). Implemented by Room in the data layer
 * (`RoomSalesRepository`).
 */
interface SalesRepository {

    /** Register a sale together with its lines, returning the assigned sale id. */
    suspend fun registerSale(sale: Sale, lines: List<SaleLine>): SaleId

    /** Observe the sales of a given [date]. */
    fun observeDailySales(date: LocalDate): Flow<List<Sale>>

    /** Observe the sales within an inclusive [range]. */
    fun observeSalesBetween(range: DateRange): Flow<List<Sale>>
}
