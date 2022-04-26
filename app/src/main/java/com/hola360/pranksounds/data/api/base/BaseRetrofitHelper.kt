package com.hola360.pranksounds.data.api.base

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

open class BaseRetrofitHelper {
    protected var okHttpClient: OkHttpClient? = null
    protected open fun initRemoteService() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .writeTimeout(6 * 1000.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(6 * 1000.toLong(), TimeUnit.MILLISECONDS)
            .addInterceptor(interceptor)

        okHttpClient = builder.build()
    }
}