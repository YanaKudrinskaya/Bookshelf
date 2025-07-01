package com.yanakudrinskaya.bookshelf.profile.domain

import android.net.Uri
import com.yanakudrinskaya.bookshelf.utils.Result

interface FileManagerInteractor {
    fun createTempImageFile() : Result<String>
    fun getUriForFile(filePath: String) : Result<Uri>
}