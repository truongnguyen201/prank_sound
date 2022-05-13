package com.hola360.pranksounds.data.api.response.detail_category_response

import com.google.gson.annotations.SerializedName
import com.hola360.pranksounds.data.model.Sound

data class DataApps(
    @SerializedName("total_page")
    val totalPage: Int? = null,
    @SerializedName("current_page")
    val currentPage: String? = null,
    @SerializedName("sub_url")
    val subUrl: String? = null,
    @SerializedName("lists")
    val listSound: MutableList<Sound>? = null,
)
