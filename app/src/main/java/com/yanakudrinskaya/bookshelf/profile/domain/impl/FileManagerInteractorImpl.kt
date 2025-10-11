package com.yanakudrinskaya.bookshelf.profile.domain.impl

import android.net.Uri
import com.yanakudrinskaya.bookshelf.utils.Result
import com.yanakudrinskaya.bookshelf.profile.domain.FileManager
import com.yanakudrinskaya.bookshelf.profile.domain.FileManagerInteractor

class FileManagerInteractorImpl (
    private val fileManager: FileManager
) : FileManagerInteractor {

    override fun createTempImageFile(): Result<String> {
        return fileManager.createTempImageFile()
    }

    override fun getUriForFile(filePath: String): Result<Uri> {
        return fileManager.getUriForFile(filePath)
    }
}