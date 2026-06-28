package com.cajaclara.app.feature.products.domain.valueobject

/** Typed identifier for a Product. Prevents mixing it up with other entity ids. */
@JvmInline
value class ProductId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        /** Id for an entity not yet persisted (Room autoGenerate assigns the real one). */
        val UNSAVED = ProductId(0L)
    }
}
