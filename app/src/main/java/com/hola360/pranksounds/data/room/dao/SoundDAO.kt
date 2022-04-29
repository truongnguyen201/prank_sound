package com.hola360.pranksounds.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hola360.pranksounds.data.model.Sound

@Dao
interface SoundDAO {
    @Query("select * from tblFavoriteSound")
    suspend fun getAllFavoriteSong() : List<Sound>

    @Query("select * from tblFavoriteSound where sound_id = (:id)")
    suspend fun getSoundById(id: String) : Sound

    @Insert
    suspend fun addToFavorite(sound: Sound)

    @Delete
    suspend fun removeFromFavorite(sound: Sound)

    @Query("select count(*) from tblFavoriteSound")
    suspend fun getQuantity() : Int
}