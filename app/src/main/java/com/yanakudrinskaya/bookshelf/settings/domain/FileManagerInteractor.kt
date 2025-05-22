package com.yanakudrinskaya.bookshelf.settings.domain

import android.net.Uri
import com.yanakudrinskaya.bookshelf.Result

interface FileManagerInteractor {
    fun createTempImageFile() : Result<String>
    fun getUriForFile(filePath: String) : Result<Uri>
}