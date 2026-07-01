package com.cajaclara.app.ui.products.productform.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cajaclara.app.core.money.Money
import com.cajaclara.app.ui.designsystem.AppBackground
import com.cajaclara.app.ui.preview.DarkPreview
import com.cajaclara.app.ui.preview.LightPreview
import com.cajaclara.app.ui.theme.CajaClaraTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The 9:16 shareable card for a product: the app's decorative background (in the current
 * light/dark theme) with the product image large and centered, then its name and price, and
 * the shop address in small print near the bottom. Designed to be captured to a bitmap and
 * shared (e.g. as a WhatsApp story). Give it a 9:16 size via [modifier].
 */
@Composable
fun ProductShareCard(
    imagePath: String?,
    fallbackIcon: ImageVector,
    name: String,
    price: String,
    storeAddress: String,
    modifier: Modifier = Modifier,
) {
    val productImage = rememberImageBitmap(imagePath)
    Box(modifier.aspectRatio(9f / 16f)) {
        AppBackground(circleScale = 0.5f) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hero group (image + name + price) centered in the space above the address.
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (productImage != null) {
                            Image(
                                bitmap = productImage,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                imageVector = fallbackIcon,
                                contentDescription = null,
                                tint = if (isSystemInDarkTheme()) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.fillMaxSize(0.45f),
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    // The name is the hero: primary color, larger. Long names wrap to at most
                    // two lines and then ellipsize so they never push the price/address away.
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    // The price is secondary: smaller, in the foreground color (white on dark,
                    // dark on light).
                    Text(
                        text = price,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }

                // Address pinned near the bottom so it's always visible, with a location pin.
                if (storeAddress.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = storeAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/** Decode the product image file off the main thread; null (or a bad path) shows the fallback. */
@Composable
private fun rememberImageBitmap(path: String?): ImageBitmap? {
    val state = produceState<ImageBitmap?>(initialValue = null, path) {
        value = if (path == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching { BitmapFactory.decodeFile(path)?.asImageBitmap() }.getOrNull()
            }
        }
    }
    return state.value
}

@LightPreview
@DarkPreview
@Composable
private fun ProductShareCardPreview() {
    CajaClaraTheme {
        ProductShareCard(
            imagePath = null,
            fallbackIcon = Icons.Filled.Category,
            name = "Café molido premium",
            price = Money.fromPesos("350,00").format(),
            storeAddress = "Calle 23 #456, La Habana",
            modifier = Modifier.fillMaxWidth(0.6f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductShareCardNoAddressPreview() {
    CajaClaraTheme {
        ProductShareCard(
            imagePath = null,
            fallbackIcon = Icons.Filled.Category,
            name = "Café molido premium",
            price = Money.fromPesos("350,00").format(),
            storeAddress = "",
            modifier = Modifier.fillMaxWidth(0.6f),
        )
    }
}
