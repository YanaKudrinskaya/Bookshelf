package com.yanakudrinskaya.bookshelf.settings.domain

import android.net.Uri
import java.io.File
import com.yanakudrinskaya.bookshelf.Result

interface FileManager {
    fun copyUriToTempFile(uri: Uri) : File?
    fun createTempImageFile(): Result<String>
    fun getUriForFile(filePath: String): Result<Uri>
}