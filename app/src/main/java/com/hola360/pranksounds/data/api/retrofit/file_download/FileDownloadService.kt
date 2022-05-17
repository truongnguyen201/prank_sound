package com.hola360.pranksounds.data.api.retrofit.file_download

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileDownloadService {
    @Streaming
    @GET
    suspend fun downloadSound(@Url fileUrl: String): Response<ResponseBody>?
}