package com.hola360.pranksounds.data.api

import com.google.gson.GsonBuilder
import com.hola360.pranksounds.data.api.base.BaseRetrofitHelper
import com.hola360.pranksounds.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitHelper : BaseRetrofitHelper() {
    protected lateinit var remoteServices: RemoteServices


    override fun initRemoteService() {
        super.initRemoteService()
        GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
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