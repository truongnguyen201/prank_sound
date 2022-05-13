package com.hola360.pranksounds.data.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.hola360.pranksounds.R
import com.hola360.pranksounds.utils.Constants

@Entity(tableName = "tblFavoriteSound")
data class Sound(
    @ColumnInfo(name = "sound_id")
    @PrimaryKey
    @SerializedName("item_id")
    val soundId: String = "",

    @ColumnInfo(name = "title")
    val title: String? = null,

    @SerializedName("cat_id")
    @ColumnInfo(name = "category_id")
    val catId: String? = null,

    @ColumnInfo(name = "thumb_url")
    @SerializedName("thumb")
    val thumbUrl: String? = null,

    @ColumnInfo(name = "sound_url")
    @SerializedName("sound")
    val soundUrl: String? = null ,
) {
    @Ignore
    var isPlaying = false
    @Ignore
    var isBanner = false

    @Ignore
    @DrawableRes
    var thumbRes = Constants.randomThumbRes()
}