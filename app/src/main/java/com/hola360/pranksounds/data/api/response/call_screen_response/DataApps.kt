package com.hola360.pranksounds.data.api.response.call_screen_response

import com.google.gson.annotations.SerializedName
import com.hola360.pranksounds.data.model.Call

data class DataApps(
    @SerializedName("lists")
    val phoneBook: MutableList<Call>,
    @SerializedName("sub_url")
    val subUrl: String
)