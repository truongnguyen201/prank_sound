package com.hola360.pranksounds.data.repository

import android.app.Application
import com.hola360.pranksounds.data.api.RetrofitHelper
import com.hola360.pranksounds.data.api.response.call_screen_response.PhoneBookResponse
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.room.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class PhoneBookRepository(app: Application) {
    var retrofitHelper: RetrofitHelper = RetrofitHelper.instance!!
    private val db = AppDatabase.getInstance(app).callDao()

    suspend fun getPhoneBook(params: String): PhoneBookResponse? {
        return withContext(Dispatchers.Default) {
            try {
                retrofitHelper.remoteServices.getPhoneBook(params)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getPhoneBookFromLocal(): List<Call>? = withContext(Dispatchers.Default) {
        try {
            db.getAllLocalCall()
        } catch (ex: Exception) {
            throw IOException(ex.message)
        }
    }

    suspend fun addNewCallToLocal(call: Call) = withContext(Dispatchers.Default) {
        db.addCallToLocal(call)
    }

}