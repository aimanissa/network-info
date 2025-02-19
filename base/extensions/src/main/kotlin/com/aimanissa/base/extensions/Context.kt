package com.aimanissa.base.extensions

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.io.File

val Context.activity: AppCompatActivity?
    get() = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.activity
        else -> null
    }

fun Context.openUrl(url: String) {
    startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse(url)), null))
}

fun Context.call(phone: String) {
    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_DIAL, Uri.parse(phone.toActionCallFormat())),
            null
        )
    )
}

fun Context.openApp(packageName: String, googlePlayUrl: String? = null, hmsUrl: String? = null) {
    startActivity(packageManager.getLaunchIntentForPackage(packageName))
}

fun Context.openSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

private const val FILE_DIRECTORY_NAME: String = "documents"
val Context.documentsDirectory
    get() = File("$cacheDir/$FILE_DIRECTORY_NAME")

private const val IMAGE_FILE_EXTENSION: String = "jpg"
fun Context.getFilePath(
    fileName: String,
    extension: String = IMAGE_FILE_EXTENSION
): File {
    val mediaStorageDir = documentsDirectory
    if (mediaStorageDir.exists().not()) {
        mediaStorageDir.mkdirs()
    }
    val name = if (fileName.contains(extension)) fileName else "$fileName.$extension"
    return File(mediaStorageDir.path, name)
}

fun Context.saveImageAsFile(bitmap: Bitmap, fileName: String, quality: Int): File {
    val file = getFilePath(fileName)
    file.delete()
    file.writeBitmap(bitmap, Bitmap.CompressFormat.JPEG, quality)
    return file
}

fun Context.uriToBitmap(path: Uri): Bitmap? {
    return try {
        if (isGreaterThanP()) {
            val source = ImageDecoder.createSource(contentResolver, path)
            ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.ARGB_8888, true)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, path)
                .copy(Bitmap.Config.ARGB_8888, true)
        }
    } catch (exception: Exception) {
        Timber.e(exception)
        null
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
private fun isGreaterThanP(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
fun Context.getOutputDirectory(): File {
    val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
        File(it, "").apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else this.filesDir
}
