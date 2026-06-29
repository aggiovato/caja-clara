package com.cajaclara.app.feature.products.domain.usecase

import java.text.Normalizer

/**
 * Suggests a SKU slug from a product name: lowercase, accents stripped, and any run of
 * non-alphanumeric characters collapsed to a single hyphen. E.g. "Café molido" -> "cafe-molido".
 * It is only a suggestion: the user can override it.
 */
class SuggestSkuUseCase {
    operator fun invoke(name: String): String {
        // NFD splits accented letters into base + combining mark; drop the marks (Café -> Cafe).
        val withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD)
            .replace(COMBINING_MARKS, "")
        return withoutAccents
            .lowercase()
            .replace(NON_ALPHANUMERIC, "-")
            .trim('-')
    }

    private companion object {
        val COMBINING_MARKS = "\\p{Mn}+".toRegex()
        val NON_ALPHANUMERIC = "[^a-z0-9]+".toRegex()
    }
}
