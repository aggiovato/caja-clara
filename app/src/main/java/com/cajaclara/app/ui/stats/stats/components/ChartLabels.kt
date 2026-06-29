package com.cajaclara.app.ui.stats.stats.components

/**
 * How often to draw a day label so they don't overlap: every day for short ranges (≤10),
 * every 5th day for longer ones (e.g. the 30-day range). The last bar is always labeled too.
 */
internal fun dayLabelStep(count: Int): Int = if (count <= 10) 1 else 5
