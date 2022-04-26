package com.hola360.pranksounds.data.model

import androidx.annotation.DrawableRes

data class Prank(
    val id: Int,
    val img: Int,
    @DrawableRes
    val background: Int,
    @DrawableRes
    val text: String
)
