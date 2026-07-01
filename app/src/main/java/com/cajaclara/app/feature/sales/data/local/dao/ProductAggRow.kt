package com.cajaclara.app.feature.sales.data.local.dao

/** Query result for a per-product sales aggregate (units sold and total profit in cents). */
data class ProductAggRow(
    val name: String,
    val units: Int,
    val profitCents: Long,
)
