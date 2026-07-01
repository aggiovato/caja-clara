package com.cajaclara.app.feature.settings.domain.model

/**
 * Global business settings the user can configure. [minMarginPercent] is the minimum profit
 * margin (over the sale price) that a product must keep; cost/PVP edits are validated against
 * it. 0 means no restriction. [storeAddress] is the shop's physical address, shown on the
 * shareable product images; blank means none configured.
 */
data class AppSettings(
    val minMarginPercent: Double = 0.0,
    val storeAddress: String = "",
) {
    val hasMinMargin: Boolean get() = minMarginPercent > 0.0

    val hasStoreAddress: Boolean get() = storeAddress.isNotBlank()

    companion object {
        val DEFAULT = AppSettings()
    }
}
