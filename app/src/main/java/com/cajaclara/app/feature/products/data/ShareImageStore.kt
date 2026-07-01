package com.cajaclara.app.feature.products.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/** Writes a shareable image into cache and returns a URI other apps can read. */
interface ShareImageStore {
    /** Encode [bitmap] as PNG into cache; returns a [FileProvider] URI ready for a share intent. */
    suspend fun saveShareable(bitmap: Bitmap): Uri
}

class AndroidShareImageStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : ShareImageStore {

    override suspend fun saveShareable(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        // A single stable file: each share overwrites the last so the cache never piles up.
        val file = File(dir, "product_share.png")
        file.outputStream().use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
