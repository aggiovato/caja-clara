package com.cajaclara.app.domain.model

/**
 * Where a daily profit figure comes from. Manual and automatic figures must stay
 * distinguishable so the daily balance never double-counts (rule 15.6).
 */
enum class DailyProfitSource {
    /** Entered by hand by the user. */
    MANUAL,

    /** Derived automatically from registered sales. */
    SALES_AUTOMATIC,
}
