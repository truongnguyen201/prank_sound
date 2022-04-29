package com.hola360.pranksounds.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.hola360.pranksounds.data.api.base.BaseRetrofitHelper
import com.hola360.pranksounds.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper : BaseRetrofitHelper() {
    lateinit var remoteServices: RemoteServices

    override fun initRemoteService() {
        super.initRemoteService()
        GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient!!).build()

        remoteServices = retrofit.create(RemoteServices::class.java)
    }

    companion object {
        @JvmStatic
        var instance: RetrofitHelper? = null
            get() {
                if (field == null) {
                    field = RetrofitHelper()
                }
                return field
            }
            private set
    }

    init {
        initRemoteService()
    }
}