package com.cajaclara.app.feature.purchases.data.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.purchases.data.local.dao.PurchaseDao
import com.cajaclara.app.feature.purchases.data.mapper.toDomain
import com.cajaclara.app.feature.purchases.data.mapper.toEntity
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.purchases.domain.model.PurchaseLine
import com.cajaclara.app.feature.purchases.domain.repository.PurchasesRepository
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/** Room-backed [PurchasesRepository]. Day boundaries use the device's local time zone. */
class RoomPurchasesRepository @Inject constructor(
    private val dao: PurchaseDao,
    private val zoneId: ZoneId,
) : PurchasesRepository {

    override suspend fun registerPurchase(purchase: Purchase, lines: List<PurchaseLine>): PurchaseId {
        val id = dao.insertPurchaseWithLines(purchase.toEntity(), lines.map { it.toEntity() })
        return PurchaseId(id)
    }

    override fun observeDailyPurchases(date: LocalDate): Flow<List<Purchase>> =
        observePurchasesBetween(DateRange.singleDay(date))

    override fun observePurchasesBetween(range: DateRange): Flow<List<Purchase>> {
        val start = range.start.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endExclusive = range.end.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return dao.observeBetween(start, endExclusive).map { rows -> rows.map { it.toDomain() } }
    }
}
