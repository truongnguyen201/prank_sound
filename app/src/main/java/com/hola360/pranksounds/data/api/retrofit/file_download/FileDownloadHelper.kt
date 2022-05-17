package com.hola360.pranksounds.data.api.retrofit.file_download

import com.google.gson.GsonBuilder
import com.hola360.pranksounds.data.api.base.BaseRetrofitHelper
import com.hola360.pranksounds.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FileDownloadHelper : BaseRetrofitHelper() {
    var fileDownloadService: FileDownloadService

    init {
        initRemoteService()
        GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.SUB_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient!!).build()
        fileDownloadService = retrofit.create(FileDownloadService::class.java)
    }

    companion object {
        private var instance: FileDownloadHelper? = null
        fun getInstance(): FileDownloadHelper {
            if (instance == null) {
                instance = FileDownloadHelper()
            }
            return instance!!
        }
    }
}