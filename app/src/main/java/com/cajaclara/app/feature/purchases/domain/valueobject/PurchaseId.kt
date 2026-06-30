package com.cajaclara.app.feature.purchases.domain.valueobject

/** Typed identifier for a Purchase. Prevents mixing it up with other entity ids. */
@JvmInline
value class PurchaseId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        /** Id for an entity not yet persisted (Room autoGenerate assigns the real one). */
        val UNSAVED = PurchaseId(0L)
    }
}
