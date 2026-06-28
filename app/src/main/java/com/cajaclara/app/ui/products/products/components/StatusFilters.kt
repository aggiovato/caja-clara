package com.cajaclara.app.ui.products.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.ui.theme.CajaClaraTheme

private val statusTabs = listOf(
    "Activos" to ProductStatus.ACTIVE,
    "Agotados" to ProductStatus.SOLD_OUT,
    "Pausados" to ProductStatus.PAUSED,
)

/** Status filter chips: three text chips plus an icon-only "Todos" (all) chip. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun StatusFilters(
    selected: ProductStatus?,
    onStatusChange: (ProductStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
        )
        statusTabs.forEach { (label, status) ->
            FilterChip(
                selected = selected == status,
                onClick = { onStatusChange(status) },
                label = { Text(label, maxLines = 1) },
                colors = colors,
            )
        }
        // "Todos" as an icon-only chip so all filters fit on one row.
        FilterChip(
            selected = selected == null,
            onClick = { onStatusChange(null) },
            label = { Icon(Icons.Filled.Apps, contentDescription = "Todos") },
            colors = colors,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusFiltersPreview() {
    CajaClaraTheme {
        StatusFilters(selected = null, onStatusChange = {})
    }
}
