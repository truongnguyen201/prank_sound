package com.hola360.pranksounds.data.repository

import android.app.Application
import com.hola360.pranksounds.data.api.RetrofitHelper
import com.hola360.pranksounds.data.api.response.submit_response.SubmitResponse
import com.hola360.pranksounds.data.model.SubmittedSound
import com.hola360.pranksounds.data.room.database.AppDatabase
import com.hola360.pranksounds.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubmitSoundRepository(app: Application) {
    private val retrofitHelper: RetrofitHelper = RetrofitHelper.instance!!
    private val db = AppDatabase.getInstance(app).submittedSoundDAO()

    suspend fun submitSound(
        type: String = Constants.SUBMIT_TYPE,
        soundId: String,
        updateType: String
    ): SubmitResponse? =
        withContext(Dispatchers.Default) {
            try {
                retrofitHelper.remoteServices.submitSound(type, soundId, updateType)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }

    suspend fun getSubmittedSound(soundId: String): SubmittedSound? =
        withContext(Dispatchers.Default) {
            try {
                db.getSubmittedSound(soundId)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }

    suspend fun addSubmitted(submittedSound: SubmittedSound) = withContext(Dispatchers.Default) {
        try {
            db.addSubmitted(submittedSound)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun updateDownloaded(soundId: String) = withContext(Dispatchers.Default){
        try{
            db.updateDownloaded(soundId)
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    suspend fun updateLiked(soundId: String) = withContext(Dispatchers.Default){
        try{
            db.updateLiked(soundId)
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }
}