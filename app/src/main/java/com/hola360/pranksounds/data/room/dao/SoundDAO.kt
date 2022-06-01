package com.hola360.pranksounds.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hola360.pranksounds.data.model.Sound

@Dao
interface SoundDAO {
    @Query("select * from tblFavoriteSound")
    suspend fun getAllFavoriteSong(): MutableList<Sound>

    @Query("select sound_id from tblFavoriteSound")
    suspend fun getFavoriteSoundID(): MutableList<String>

    @Query("select * from tblFavoriteSound where sound_id = (:id)")
    suspend fun getSoundById(id: String): Sound

    @Insert
    suspend fun addToFavorite(sound: Sound)

    @Delete
    suspend fun removeFromFavorite(sound: Sound)

    @Query("select count(*) from tblFavoriteSound")
    suspend fun getQuantity(): Int

    @Query("select count(*) from tblFavoriteSound where sound_id = (:id)")
    suspend fun isExist(id: String): Int

    @Query("select * from tblFavoriteSound limit 10 offset :pageNumber*10")
    suspend fun getPaging(pageNumber: Int): MutableList<Sound>
}