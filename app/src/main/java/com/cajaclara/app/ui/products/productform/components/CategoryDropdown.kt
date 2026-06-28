package com.cajaclara.app.ui.products.productform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.ui.theme.AppBorderWidth
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.Instant

/** A bordered field (matching the app inputs) that opens a menu to pick a category. */
@Composable
internal fun CategoryDropdown(
    label: String,
    categories: List<Category>,
    selected: Category?,
    onSelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        Box {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppCornerRadius),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(AppBorderWidth, MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = selected?.name ?: "—",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            onSelected(category)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryDropdownPreview() {
    val cats = listOf(
        Category(CategoryId(1), "Bebidas", null, Instant.EPOCH),
        Category(CategoryId(5), "Otros", null, Instant.EPOCH),
    )
    CajaClaraTheme {
        CategoryDropdown(
            label = "Categoría",
            categories = cats,
            selected = cats.last(),
            onSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
