package com.hola360.pranksounds.data.model

import com.google.gson.annotations.SerializedName

data class SoundCategory(
    val title: String,
    @SerializedName("cat_id")
    val categoryId: String,
    @SerializedName("thumb")
    val thumbUrl: String,
)
