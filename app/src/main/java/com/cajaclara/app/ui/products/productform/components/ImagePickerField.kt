package com.cajaclara.app.ui.products.productform.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.ui.designsystem.AppIconButton
import com.cajaclara.app.ui.designsystem.ProductImage
import com.cajaclara.app.ui.theme.CajaClaraTheme

/** Product image (or category fallback) with icon buttons to pick from gallery or take a photo. */
@Composable
internal fun ImagePickerField(
    imagePath: String?,
    fallbackIcon: ImageVector,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductImage(imagePath = imagePath, fallback = fallbackIcon, size = 72.dp)
        AppIconButton(Icons.Filled.PhotoLibrary, "Elegir de la galería", onPickGallery)
        AppIconButton(Icons.Filled.PhotoCamera, "Tomar foto", onTakePhoto)
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePickerFieldPreview() {
    CajaClaraTheme {
        ImagePickerField(
            imagePath = null,
            fallbackIcon = Icons.Filled.Category,
            onPickGallery = {},
            onTakePhoto = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
