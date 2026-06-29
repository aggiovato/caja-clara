package com.cajaclara.app.feature.sales.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room row for a cash close. [epochDay] (LocalDate.toEpochDay) is unique so each day has at
 * most one close; saving the same day replaces the previous row (recuadre).
 */
@Entity(
    tableName = "cash_closes",
    indices = [Index(value = ["epochDay"], unique = true)],
)
data class CashCloseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val epochDay: Long,
    val expectedRevenueCents: Long,
    val expectedCostCents: Long,
    val countedCashCents: Long,
    val salesCount: Int,
    val closedAt: Long,
    val note: String?,
)
