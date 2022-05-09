package com.hola360.pranksounds.data.repository

import android.app.Application
import com.hola360.pranksounds.data.api.RetrofitHelper
import com.hola360.pranksounds.data.api.response.sound_category_response.SoundCategoryResponse
import com.hola360.pranksounds.data.room.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SoundCategoryRepository(app: Application) {

    private val retrofitHelper: RetrofitHelper = RetrofitHelper.instance!!
    private val db = AppDatabase.getInstance(app).soundDAO()

    suspend fun getSoundCategory(params: String): SoundCategoryResponse? =
        withContext(Dispatchers.Default) {
            try {
                retrofitHelper.remoteServices.querySoundCategory(params)
            } catch (ex: Exception) {
                null
            }
        }

    suspend fun getQuantityOfFavoriteSound(): Int? = withContext(Dispatchers.Default) {
        try {
            db.getQuantity()
        } catch (ex: Exception) {
            null
        }
    }

}