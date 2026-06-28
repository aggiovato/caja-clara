package com.cajaclara.app.feature.products.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

/** A file the camera writes into: [uri] to hand to the camera, [path] to store on the product. */
data class CameraTarget(val uri: Uri, val path: String)

/** Persists product images into app storage. */
interface ImageStore {
    /** Copy a picked content URI (gallery) into internal storage; returns the file path. */
    suspend fun saveProductImage(uri: Uri): String

    /** Create an empty destination for a camera capture; returns its content URI and path. */
    fun newCameraTarget(): CameraTarget
}

class AndroidImageStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : ImageStore {

    override suspend fun saveProductImage(uri: Uri): String = withContext(Dispatchers.IO) {
        val file = newImageFile()
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Could not open image at $uri")
        file.absolutePath
    }

    override fun newCameraTarget(): CameraTarget {
        val file = newImageFile()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return CameraTarget(uri = uri, path = file.absolutePath)
    }

    private fun newImageFile(): File {
        val dir = File(context.filesDir, "product_images").apply { mkdirs() }
        return File(dir, "${UUID.randomUUID()}.jpg")
    }
}
