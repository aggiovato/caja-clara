package com.cajaclara.app.domain.model

/** Type of a stock movement (section 11.4). */
enum class StockMovementType {
    /** Stock added (restock, purchase). */
    IN,

    /** Stock removed (typically a sale). */
    OUT,

    /** Manual correction of the on-hand quantity. */
    ADJUSTMENT,

    /** Product marked as sold out. */
    SOLD_OUT,

    /** Product restored after being sold out. */
    RESTORED,
}
