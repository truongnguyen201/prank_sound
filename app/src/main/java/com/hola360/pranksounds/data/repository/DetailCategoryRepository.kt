package com.hola360.pranksounds.data.repository

import android.app.Application
import com.hola360.pranksounds.data.api.RetrofitHelper
import com.hola360.pranksounds.data.api.response.detail_category_response.DetailCategoryResponse
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.room.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailCategoryRepository(app: Application) {
    private val retrofitHelper: RetrofitHelper = RetrofitHelper.instance!!
    private val db = AppDatabase.getInstance(app).soundDAO()

    suspend fun getDetailCategory(
        type: String,
        catId: String,
        pageNumber: Int,
        itemPerPage: Int
    ): DetailCategoryResponse? = withContext(Dispatchers.Default) {
        try {
            retrofitHelper.remoteServices.getDetailCategory(type, catId, pageNumber, itemPerPage)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getFavoriteSound(): MutableList<Sound>? = withContext(Dispatchers.Default) {
        try {
            db.getAllFavoriteSong()
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getFavoriteSoundID(): MutableList<String>? = withContext(Dispatchers.Default) {
        try {
            db.getFavoriteSoundID()
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun addFavoriteSound(sound: Sound): Boolean = withContext(Dispatchers.Default) {
        try {
            if (isExist(sound.soundId) == 0) {
                db.addToFavorite(sound)
            }
            true
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun removeFavoriteSound(sound: Sound) = withContext(Dispatchers.Default) {
        if (isExist(sound.soundId) == 1) {
            db.removeFromFavorite(sound)
        }
    }

    private suspend fun isExist(id: String): Int? = withContext(Dispatchers.Default) {
        try {
            db.isExist(id)
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

    suspend fun getPageDB(pageNumber: Int): MutableList<Sound>? = withContext(Dispatchers.Default) {
        try {
            db.getPaging(pageNumber - 1)
        } catch (ex: Exception) {
            null
        }
    }
}