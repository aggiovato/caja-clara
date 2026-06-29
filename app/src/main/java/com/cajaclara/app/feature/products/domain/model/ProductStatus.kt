package com.cajaclara.app.feature.products.domain.model

/**
 * Lifecycle status of a product. Products are never physically deleted; they are paused
 * (reversible) or archived (soft delete), and sold out is reversible too (section 11.1).
 */
enum class ProductStatus {
    ACTIVE,
    SOLD_OUT,
    PAUSED,
    ARCHIVED;

    /** Only active products are offered in quick sale; the rest are hidden. */
    val isSellable: Boolean get() = this == ACTIVE

    /** Archived products are soft-deleted: kept for history but hidden from every list. */
    val isArchived: Boolean get() = this == ARCHIVED
}
