package com.hola360.pranksounds.data.api.response.call_screen_response

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data_apps")
    val dataApps: DataApps,
    val time: Int,
    val token: String
)