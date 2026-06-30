package com.cajaclara.app.ui.preview

import com.cajaclara.app.core.money.Money
import com.cajaclara.app.core.quantity.Quantity
import com.cajaclara.app.feature.products.domain.model.Product
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.ProductId
import com.cajaclara.app.feature.sales.domain.model.Sale
import com.cajaclara.app.feature.sales.domain.valueobject.SaleId
import com.cajaclara.app.feature.stats.domain.model.DailyCashPoint
import com.cajaclara.app.feature.stats.domain.model.DailySalesPoint
import java.time.Instant
import java.time.LocalDate

/** Shared sample data for @Preview composables (UI module only). */
object PreviewSamples {

    fun product(
        name: String = "Café molido",
        cost: String = "2,10",
        pvp: String = "3,50",
        stock: Int = 28,
        status: ProductStatus = ProductStatus.ACTIVE,
    ): Product = Product(
        id = ProductId(name.hashCode().toLong()),
        name = name,
        currentCost = Money.fromPesos(cost),
        currentPvp = Money.fromPesos(pvp),
        stockQuantity = Quantity(stock),
        status = status,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

    fun products(): List<Product> = listOf(
        product("Café molido", "2,10", "3,50", 28),
        product("Agua 1.5L", "0,35", "0,65", 54),
        product("Chocolate", "0,90", "1,50", 0, ProductStatus.SOLD_OUT),
    )

    /** A 7-day daily sales series with a couple of empty days. */
    fun salesPoints(): List<DailySalesPoint> {
        val base = LocalDate.of(2026, 6, 24)
        val revenue = listOf("12,00", "0,00", "30,00", "18,50", "0,00", "22,00", "40,00")
        val cost = listOf("5,00", "0,00", "12,00", "7,00", "0,00", "9,00", "16,00")
        return revenue.indices.map { i ->
            DailySalesPoint(base.plusDays(i.toLong()), Money.fromPesos(revenue[i]), Money.fromPesos(cost[i]))
        }
    }

    /** A few registered sales for today. */
    fun sales(): List<Sale> = listOf("12,50", "3,20", "8,00").mapIndexed { i, amount ->
        Sale(
            id = SaleId((i + 1).toLong()),
            soldAt = Instant.parse("2026-06-30T1$i:00:00Z"),
            totalRevenue = Money.fromPesos(amount),
            totalCost = Money.fromPesos("1,00"),
            createdAt = Instant.parse("2026-06-30T1$i:00:00Z"),
        )
    }

    /** A 7-day cash-flow series mixing sales in and purchases out. */
    fun cashPoints(): List<DailyCashPoint> {
        val base = LocalDate.of(2026, 6, 24)
        val sales = listOf("12,00", "0,00", "30,00", "18,50", "0,00", "22,00", "40,00")
        val purchases = listOf("0,00", "50,00", "0,00", "0,00", "30,00", "0,00", "10,00")
        return sales.indices.map { i ->
            DailyCashPoint(base.plusDays(i.toLong()), Money.fromPesos(sales[i]), Money.fromPesos(purchases[i]))
        }
    }
}
