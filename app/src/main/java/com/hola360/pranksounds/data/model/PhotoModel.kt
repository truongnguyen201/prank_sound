package com.hola360.pranksounds.data.model

import android.net.Uri
import java.io.File

data class PhotoModel(
    val uri: Uri,
    val file: File,
    val title: String,
    val albumId: Long,
    val albumName: String
) {
    var numberImage: Int = 0
}
