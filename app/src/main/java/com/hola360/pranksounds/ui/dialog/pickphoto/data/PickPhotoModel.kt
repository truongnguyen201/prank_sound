package com.hola360.pranksounds.ui.dialog.pickphoto.data

import com.hola360.pranksounds.data.model.PhotoModel

data class PickPhotoModel(
    val pickModelDataType: PickModelDataType,
    val photoList: MutableList<PhotoModel>
)