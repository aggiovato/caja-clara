package com.cajaclara.app.ui.products.productform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cajaclara.app.feature.products.data.CameraTarget
import com.cajaclara.app.feature.products.domain.model.Category
import com.cajaclara.app.feature.products.domain.model.ProductStatus
import com.cajaclara.app.feature.products.domain.valueobject.CategoryId
import com.cajaclara.app.ui.designsystem.AppConfirmDialog
import com.cajaclara.app.ui.designsystem.AppDropdownField
import com.cajaclara.app.ui.designsystem.AppIconButton
import com.cajaclara.app.ui.designsystem.AppPrimaryButton
import com.cajaclara.app.ui.designsystem.AppSecondaryButton
import com.cajaclara.app.ui.designsystem.AppTextField
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.products.categoryIcon
import com.cajaclara.app.ui.products.productform.components.ImagePickerField
import com.cajaclara.app.ui.products.productform.components.ReadOnlyField
import com.cajaclara.app.ui.products.productform.components.ValueEditDialog
import com.cajaclara.app.ui.theme.CajaClaraTheme
import java.time.Instant

private enum class EditTarget { COST, PVP, STOCK }

@Composable
fun ProductFormScreen(
    onDone: () -> Unit,
    viewModel: ProductFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val name = rememberTextFieldState()
    val cost = rememberTextFieldState()
    val pvp = rememberTextFieldState()
    val stock = rememberTextFieldState()
    val sku = rememberTextFieldState()
    val description = rememberTextFieldState()

    // Prefill the editable fields once when editing.
    LaunchedEffect(state.prefill) {
        state.prefill?.let { p ->
            name.setTextAndPlaceCursorAtEnd(p.name)
            sku.setTextAndPlaceCursorAtEnd(p.sku)
            description.setTextAndPlaceCursorAtEnd(p.description)
            viewModel.onPrefillConsumed()
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let(viewModel::onImagePicked) }

    var cameraTarget by remember { mutableStateOf<CameraTarget?>(null) }
    val takePhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { saved -> if (saved) cameraTarget?.let { viewModel.onPhotoTaken(it.path) } }

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    ProductFormContent(
        state = state,
        name = name,
        cost = cost,
        pvp = pvp,
        stock = stock,
        sku = sku,
        description = description,
        onPickGallery = {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onTakePhoto = {
            val target = viewModel.newCameraTarget()
            cameraTarget = target
            takePhoto.launch(target.uri)
        },
        onRemoveImage = viewModel::onRemoveImage,
        onCategorySelected = viewModel::onCategorySelected,
        onChangeCost = viewModel::changeCost,
        onChangePvp = viewModel::changePvp,
        onChangeStock = viewModel::changeStock,
        onTogglePause = viewModel::togglePause,
        onArchive = viewModel::archive,
        onCancel = onDone,
        onSave = {
            viewModel.save(
                name = name.text.toString(),
                costText = cost.text.toString(),
                pvpText = pvp.text.toString(),
                stockText = stock.text.toString(),
                sku = sku.text.toString(),
                description = description.text.toString(),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormContent(
    state: ProductFormUiState,
    name: TextFieldState,
    cost: TextFieldState,
    pvp: TextFieldState,
    stock: TextFieldState,
    sku: TextFieldState,
    description: TextFieldState,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onRemoveImage: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    onChangeCost: (String) -> Unit,
    onChangePvp: (String) -> Unit,
    onChangeStock: (String) -> Unit,
    onTogglePause: () -> Unit,
    onArchive: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    var editingValue by remember { mutableStateOf<EditTarget?>(null) }
    var showPauseConfirm by remember { mutableStateOf(false) }
    var showArchiveConfirm by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEdit) "Editar producto" else "Nuevo producto") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImagePickerField(
                imagePath = state.imagePath,
                fallbackIcon = categoryIcon(state.selectedCategory?.name),
                onPickGallery = onPickGallery,
                onTakePhoto = onTakePhoto,
                onRemoveImage = onRemoveImage,
                trailing = {
                    if (state.isEdit) {
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        AppIconButton(
                            icon = if (state.status == ProductStatus.PAUSED) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (state.status == ProductStatus.PAUSED) "Reanudar" else "Pausar",
                            // Resume happens immediately; pausing asks for confirmation.
                            onClick = { if (state.status == ProductStatus.PAUSED) onTogglePause() else showPauseConfirm = true },
                            subtle = true,
                        )
                        AppIconButton(Icons.Filled.Archive, "Archivar", { showArchiveConfirm = true }, subtle = true)
                    }
                },
            )
            AppTextField(name, label = "Nombre", placeholder = "Café molido", maxLength = 60)

            if (state.isEdit) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("Coste", state.currentCost?.format() ?: "—", Modifier.weight(1f), onEdit = { editingValue = EditTarget.COST })
                    ReadOnlyField("PVP", state.currentPvp?.format() ?: "—", Modifier.weight(1f), onEdit = { editingValue = EditTarget.PVP })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReadOnlyField("Stock", state.currentStock?.value?.toString() ?: "—", Modifier.weight(1f), onEdit = { editingValue = EditTarget.STOCK })
                    AppDropdownField(
                        label = "Categoría",
                        options = state.categories,
                        selected = state.selectedCategory,
                        optionLabel = { it.name },
                        onSelect = onCategorySelected,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(cost, Modifier.weight(1f), label = "Coste", placeholder = "0,00", keyboardType = KeyboardType.Decimal)
                    AppTextField(pvp, Modifier.weight(1f), label = "PVP", placeholder = "0,00", keyboardType = KeyboardType.Decimal)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(stock, Modifier.weight(1f), label = "Stock", placeholder = "Opcional", keyboardType = KeyboardType.Number)
                    AppDropdownField(
                        label = "Categoría",
                        options = state.categories,
                        selected = state.selectedCategory,
                        optionLabel = { it.name },
                        onSelect = onCategorySelected,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            AppTextField(
                sku,
                label = "SKU",
                tooltip = "Código interno único para identificar y controlar el producto en tu inventario " +
                    "(Stock Keeping Unit). Es opcional.",
            )
            AppTextField(description, label = "Descripción (opcional)", singleLine = false, minLines = 4)

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppSecondaryButton("Cancelar", onCancel, Modifier.weight(1f))
                AppPrimaryButton("Guardar", onSave, Modifier.weight(1f), enabled = !state.isSaving)
            }
        }
    }

    editingValue?.let { target ->
        ValueEditDialog(
            title = when (target) {
                EditTarget.COST -> "Cambiar coste"
                EditTarget.PVP -> "Cambiar PVP"
                EditTarget.STOCK -> "Ajustar stock"
            },
            placeholder = if (target == EditTarget.STOCK) "0" else "0,00",
            keyboardType = if (target == EditTarget.STOCK) KeyboardType.Number else KeyboardType.Decimal,
            onConfirm = { text ->
                when (target) {
                    EditTarget.COST -> onChangeCost(text)
                    EditTarget.PVP -> onChangePvp(text)
                    EditTarget.STOCK -> onChangeStock(text)
                }
                editingValue = null
            },
            onDismiss = { editingValue = null },
        )
    }

    if (showPauseConfirm) {
        AppConfirmDialog(
            title = "Pausar producto",
            message = "Dejará de aparecer en venta rápida. Podrás reanudarlo cuando quieras.",
            confirmText = "Pausar",
            onConfirm = {
                onTogglePause()
                showPauseConfirm = false
            },
            onDismiss = { showPauseConfirm = false },
        )
    }

    if (showArchiveConfirm) {
        AppConfirmDialog(
            title = "Archivar producto",
            message = "Se ocultará de los listados. Su histórico y ventas se conservan; podrás recuperarlo.",
            confirmText = "Archivar",
            onConfirm = {
                onArchive()
                showArchiveConfirm = false
            },
            onDismiss = { showArchiveConfirm = false },
        )
    }
}

@LightPreview
@DarkPreview
@Composable
private fun ProductFormScreenPreview() {
    val otros = Category(CategoryId(5), "Otros", null, Instant.EPOCH)
    CajaClaraTheme {
        ProductFormContent(
            state = ProductFormUiState(categories = listOf(otros), selectedCategory = otros),
            name = rememberTextFieldState("Café molido"),
            cost = rememberTextFieldState("2,10"),
            pvp = rememberTextFieldState("3,50"),
            stock = rememberTextFieldState("28"),
            sku = rememberTextFieldState(),
            description = rememberTextFieldState(),
            onPickGallery = {},
            onTakePhoto = {},
            onRemoveImage = {},
            onCategorySelected = {},
            onChangeCost = {},
            onChangePvp = {},
            onChangeStock = {},
            onTogglePause = {},
            onArchive = {},
            onCancel = {},
            onSave = {},
        )
    }
}
