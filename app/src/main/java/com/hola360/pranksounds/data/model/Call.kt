package com.hola360.pranksounds.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tblLocalCall")
data class Call(
    @PrimaryKey
    val id: String,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,

    val name: String,
    val phone: String,

    @ColumnInfo(name = "is_local")
    val isLocal: Boolean
) {
    constructor(): this("", "", "", "", false)
}