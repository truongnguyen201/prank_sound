package com.hola360.pranksounds.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblSubmittedSound")
data class SubmittedSound(
    @PrimaryKey
    @ColumnInfo(name = "sound_id")
    val soundId: String = "",

    @ColumnInfo(name = "downloaded")
    val isDownloaded: Boolean,

    @ColumnInfo(name = "liked")
    val isLiked: Boolean
)
