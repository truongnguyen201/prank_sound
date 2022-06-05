package com.hola360.pranksounds.data.api.response.submit_response

import com.google.gson.annotations.SerializedName

data class DataApps(
    @SerializedName("current_count")
    val currentCount: Int,
    @SerializedName("item_id")
    val soundId: String,
    @SerializedName("msg")
    val message: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("update_type")
    val updateType: String
)