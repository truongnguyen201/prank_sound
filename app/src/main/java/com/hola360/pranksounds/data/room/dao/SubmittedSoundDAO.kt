package com.hola360.pranksounds.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hola360.pranksounds.data.model.SubmittedSound

@Dao
interface SubmittedSoundDAO {
    @Query("select * from tblSubmittedSound")
    suspend fun getAllSubmittedSound(): MutableList<SubmittedSound>

    @Query("select * from tblSubmittedSound where sound_id = (:soundId)")
    suspend fun getSubmittedSound(soundId: String): SubmittedSound

    @Query("select count(*) from tblSubmittedSound where sound_id = (:soundId)")
    suspend fun checkIsSubmitted(soundId: String): Int

    @Query("select count(*) from tblSubmittedSound where sound_id=(:soundId) and downloaded")
    suspend fun checkIsDownloaded(soundId: String): Int

    @Query("select count(*) from tblSubmittedSound where sound_id=(:soundId) and liked")
    suspend fun checkIsLiked(soundId: String): Int

    @Insert
    suspend fun addSubmitted(sound: SubmittedSound)

    @Query("update tblSubmittedSound set downloaded = 1 where sound_id = (:soundId) ")
    suspend fun updateDownloaded(soundId: String)

    @Query("update tblSubmittedSound set liked = 1 where sound_id = (:soundId) ")
    suspend fun updateLiked(soundId: String)
}