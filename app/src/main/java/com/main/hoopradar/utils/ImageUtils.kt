package com.main.hoopradar.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImageUtils {
    fun createImageUri(context: Context): Uri {
        val imageFile = File.createTempFile("court_run_", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }
}