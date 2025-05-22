package com.yanakudrinskaya.bookshelf.settings.domain.impl

import android.net.Uri
import com.yanakudrinskaya.bookshelf.Result
import com.yanakudrinskaya.bookshelf.settings.domain.FileManager
import com.yanakudrinskaya.bookshelf.settings.domain.FileManagerInteractor

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