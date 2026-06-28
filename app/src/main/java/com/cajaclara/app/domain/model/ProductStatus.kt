package com.cajaclara.app.domain.model

/**
 * Lifecycle status of a product. Products are never physically deleted; they are
 * paused instead, and sold out is reversible (section 11.1).
 */
enum class ProductStatus {
    ACTIVE,
    SOLD_OUT,
    PAUSED;

    /** Only active products are offered in quick sale; sold out / paused are hidden. */
    val isSellable: Boolean get() = this == ACTIVE
}
