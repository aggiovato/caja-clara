package com.cajaclara.app.feature.purchases.domain.repository

import com.cajaclara.app.core.date.DateRange
import com.cajaclara.app.feature.purchases.domain.model.Purchase
import com.cajaclara.app.feature.purchases.domain.model.PurchaseLine
import com.cajaclara.app.feature.purchases.domain.valueobject.PurchaseId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Port for purchase persistence. Implemented by Room in the data layer. */
interface PurchasesRepository {

    /** Register a purchase together with its lines, returning the assigned purchase id. */
    suspend fun registerPurchase(purchase: Purchase, lines: List<PurchaseLine>): PurchaseId

    /** Observe the purchases of a given [date]. */
    fun observeDailyPurchases(date: LocalDate): Flow<List<Purchase>>

    /** Observe the purchases within an inclusive [range]. */
    fun observePurchasesBetween(range: DateRange): Flow<List<Purchase>>
}
