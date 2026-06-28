package com.cajaclara.app.feature.products.domain.model

import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import java.time.Instant

/** Groups products together (section 11.2). */
data class Category(
    val id: CategoryId,
    val name: String,
    val colorHex: String? = null,
    val createdAt: Instant,
)
