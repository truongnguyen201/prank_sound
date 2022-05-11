package com.hola360.pranksounds.data.repository

import android.app.Application
import android.util.Log
import com.hola360.pranksounds.data.api.RetrofitHelper
import com.hola360.pranksounds.data.api.response.detail_category_response.DetailCategoryResponse
import com.hola360.pranksounds.data.model.Sound
import com.hola360.pranksounds.data.room.database.AppDatabase
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.*

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

    suspend fun addFavoriteSound(sound: Sound) = withContext(Dispatchers.Default) {
        if (isExist(sound.soundId) == 0) {
            db.addToFavorite(sound)
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

    suspend fun downloadFile(url: String, file: File): File {
        withContext(Dispatchers.IO){
            val client = OkHttpClient()
            val request = Request.Builder().url(Constants.SUB_URL + url).build()
            val response = client.newCall(request).execute()

            val sink: BufferedSink = file.sink().buffer()
            sink.writeAll(response.body!!.source())
            sink.close()

            Log.e("FIle size", file.totalSpace.toString())
        }

        Log.e("FIle size", file.totalSpace.toString())
        Log.e("File", file.totalSpace.toString())
        return file
    }
}