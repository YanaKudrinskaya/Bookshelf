package com.yanakudrinskaya.bookshelf.profile.domain.api

import android.net.Uri
import java.io.File
import com.yanakudrinskaya.bookshelf.utils.Result

interface FileManager {
    fun copyUriToTempFile(uri: Uri) : File?
    fun createTempImageFile(): Result<String>
    fun getUriForFile(filePath: String): Result<Uri>
}