package com.cajaclara.app.feature.sales.domain.valueobject

/** Typed identifier for a Sale. Prevents mixing it up with other entity ids. */
@JvmInline
value class SaleId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        /** Id for an entity not yet persisted (Room autoGenerate assigns the real one). */
        val UNSAVED = SaleId(0L)
    }
}
