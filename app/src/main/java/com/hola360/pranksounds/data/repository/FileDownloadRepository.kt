package com.hola360.pranksounds.data.repository

import com.hola360.pranksounds.data.api.retrofit.file_download.FileDownloadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

class FileDownloadRepository {
    private var fileDownloadHelper = FileDownloadHelper.getInstance()
    suspend fun downloadSoundFile(url: String): Response<ResponseBody>? =
        withContext(Dispatchers.Default) {
            try {
                fileDownloadHelper.fileDownloadService.downloadSound(url)
            } catch (ex: Exception) {
                null
            }
        }
}