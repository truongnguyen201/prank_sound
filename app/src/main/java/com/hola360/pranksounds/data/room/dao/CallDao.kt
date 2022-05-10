package com.hola360.pranksounds.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.model.Sound

@Dao
interface CallDao {
    @Query("select * from tblLocalCall")
    suspend fun getAllLocalCall() : List<Call>

    @Query("select * from tblLocalCall where id = (:id)")
    suspend fun getLocalCallById(id: String) : Call

    @Insert
    suspend fun addCallToLocal(call: Call)

    @Delete
    suspend fun removeCallFromLocal(call: Call)
}