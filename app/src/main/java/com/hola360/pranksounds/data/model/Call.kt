package com.hola360.pranksounds.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "tblLocalCall")
data class Call (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    var name: String,
    var phone: String,

    @ColumnInfo(name = "is_local")
    var isLocal: Boolean
): Serializable, Parcelable {
    constructor() : this(0, "", "", "", false)
}