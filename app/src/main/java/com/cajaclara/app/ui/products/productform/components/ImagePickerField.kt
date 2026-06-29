package com.cajaclara.app.ui.products.productform.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.cajaclara.app.ui.designsystem.AppConfirmDialog
import com.cajaclara.app.ui.designsystem.AppIconButton
import com.cajaclara.app.ui.designsystem.AppProductImage
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.AppCornerRadius
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.io.File

/**
 * Product image with: tap to preview, an X badge to remove it (with confirmation), and the
 * gallery/camera pickers. [trailing] holds extra actions (e.g. pause/archive in edit mode).
 */
@Composable
internal fun ImagePickerField(
    imagePath: String?,
    fallbackIcon: ImageVector,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    var showRemove by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            AppProductImage(
                imagePath = imagePath,
                fallback = fallbackIcon,
                size = 72.dp,
                modifier = if (imagePath != null) Modifier.clickable { showPreview = true } else Modifier,
            )
            if (imagePath != null) {
                Surface(
                    onClick = { showRemove = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.align(Alignment.TopEnd).size(22.dp),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Quitar imagen", modifier = Modifier.padding(3.dp))
                }
            }
        }
        AppIconButton(Icons.Filled.PhotoLibrary, "Elegir de la galería", onPickGallery)
        AppIconButton(Icons.Filled.PhotoCamera, "Tomar foto", onTakePhoto)
        trailing()
    }

    if (showRemove) {
        AppConfirmDialog(
            title = "Eliminar imagen",
            message = "¿Quieres quitar la imagen de este producto?",
            onConfirm = {
                onRemoveImage()
                showRemove = false
            },
            onDismiss = { showRemove = false },
        )
    }
    if (showPreview && imagePath != null) {
        ImagePreviewDialog(path = imagePath, onDismiss = { showPreview = false })
    }
}

@Composable
private fun ImagePreviewDialog(path: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(AppCornerRadius),
            color = MaterialTheme.colorScheme.surface,
        ) {
            AsyncImage(
                model = File(path),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
        }
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ImagePickerFieldPreview() {
    CajaClaraTheme {
        ImagePickerField(
            imagePath = null,
            fallbackIcon = Icons.Filled.Category,
            onPickGallery = {},
            onTakePhoto = {},
            onRemoveImage = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
