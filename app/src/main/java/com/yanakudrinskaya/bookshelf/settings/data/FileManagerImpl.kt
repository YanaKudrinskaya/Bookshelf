package com.yanakudrinskaya.bookshelf.settings.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.yanakudrinskaya.bookshelf.settings.domain.FileManager
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.yanakudrinskaya.bookshelf.Result

class FileManagerImpl(
    private val context: Context
) : FileManager {

    private fun createTempFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.cacheDir
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun copyUriToTempFile(uri: Uri): File? {
        return try {
            val tempFile = createTempFile()
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    override fun createTempImageFile(): Result<String> {
        return try {
            val tempFile = createTempFile()
            Result.Success(tempFile.absolutePath)
        } catch (e: IOException) {
            Result.Failure(e)
        }
    }

    override fun getUriForFile(filePath: String): Result<Uri> {
        return try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Result.Success(uri)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

}