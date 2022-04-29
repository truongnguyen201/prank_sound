package com.hola360.pranksounds.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tblFavoriteSound")
data class Sound(
    @ColumnInfo(name = "sound_id")
    @PrimaryKey
    @SerializedName("item_id")
    val soundId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @SerializedName("cat_id")
    @ColumnInfo(name = "category_id")
    val catId: String,

    @ColumnInfo(name = "thumb_url")
    @SerializedName("thumb")
    val thumbUrl: String,

    @ColumnInfo(name = "sound_url")
    @SerializedName("sound")
    val soundUrl: String
) {
    constructor() : this("", "", "", "", "" )
}