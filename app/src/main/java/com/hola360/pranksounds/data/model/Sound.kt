package com.hola360.pranksounds.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
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
    val soundUrl: String? = null,
) : Serializable, Parcelable {
    @IgnoredOnParcel
    @Ignore
    var isPlaying = false

    @IgnoredOnParcel
    @Ignore
    var isBanner = false

    @IgnoredOnParcel
    @Ignore
    @DrawableRes
    var thumbRes = Utils.randomThumbRes()
}