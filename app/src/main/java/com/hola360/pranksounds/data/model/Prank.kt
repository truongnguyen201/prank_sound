package com.hola360.pranksounds.data.model

import androidx.annotation.DrawableRes

data class Prank(
    val id: Int,
    @DrawableRes
    val img: Int,
    @DrawableRes
    val background: Int,
    val text: String
)
