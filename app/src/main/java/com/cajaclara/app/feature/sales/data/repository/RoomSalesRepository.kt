package com.cajaclara.app.feature.sales.data.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.sales.data.local.dao.SaleDao
import com.cajaclara.app.feature.sales.data.mapper.toDomain
import com.cajaclara.app.feature.sales.data.mapper.toEntity
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.model.SaleLine
import com.cajaclara.app.feature.sales.domain.repository.SalesRepository
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Room-backed [SalesRepository]. Day boundaries use the device's local time zone. */
class RoomSalesRepository @Inject constructor(
    private val dao: SaleDao,
    private val zoneId: ZoneId,
) : SalesRepository {

    override suspend fun registerSale(sale: Sale, lines: List<SaleLine>): SaleId {
        val id = dao.insertSaleWithLines(sale.toEntity(), lines.map { it.toEntity() })
        return SaleId(id)
    }

    override fun observeDailySales(date: LocalDate): Flow<List<Sale>> =
        observeSalesBetween(DateRange.singleDay(date))

    override fun observeSalesBetween(range: DateRange): Flow<List<Sale>> {
        val start = range.start.atStartOfDay(zoneId).toInstant().toEpochMilli()
        // Exclusive upper bound: start of the day after the range's end.
        val endExclusive = range.end.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return dao.observeBetween(start, endExclusive).map { rows -> rows.map { it.toDomain() } }
    }
}
