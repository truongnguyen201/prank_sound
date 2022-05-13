package com.hola360.pranksounds.data.api.response.sound_category_response

import com.google.gson.annotations.SerializedName
import com.hola360.pranksounds.data.model.SoundCategory

data class DataApps(
    @SerializedName("lists")
    val listSoundCategory: MutableList<SoundCategory>,
    @SerializedName("sub_url")
    val subUrl: String?,
)