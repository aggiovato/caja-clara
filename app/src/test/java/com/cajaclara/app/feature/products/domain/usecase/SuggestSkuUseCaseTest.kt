package com.cajaclara.app.feature.products.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestSkuUseCaseTest {

    private val suggest = SuggestSkuUseCase()

    @Test
    fun `strips accents, lowercases and hyphenates spaces`() {
        assertEquals("cafe-molido", suggest("Café molido"))
    }

    @Test
    fun `collapses separators and trims hyphens`() {
        assertEquals("agua-1-5l", suggest("  Agua 1.5L  "))
        assertEquals("coca-cola", suggest("Coca-Cola"))
    }

    @Test
    fun `handles tilde and other diacritics`() {
        assertEquals("nono", suggest("Ñoño"))
        assertEquals("te", suggest("Té"))
    }

    @Test
    fun `blank name yields empty slug`() {
        assertEquals("", suggest("   "))
        assertEquals("", suggest(""))
    }
}
