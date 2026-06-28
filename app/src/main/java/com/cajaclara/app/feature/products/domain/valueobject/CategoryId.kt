package com.cajaclara.app.feature.products.domain.valueobject

/** Typed identifier for a Category. Prevents mixing it up with other entity ids. */
@JvmInline
value class CategoryId(val value: Long) {
    val isSaved: Boolean get() = value != UNSAVED.value

    companion object {
        /** Id for an entity not yet persisted (Room autoGenerate assigns the real one). */
        val UNSAVED = CategoryId(0L)
    }
}
