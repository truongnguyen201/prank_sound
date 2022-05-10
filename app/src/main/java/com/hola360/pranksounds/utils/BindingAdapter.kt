package com.hola360.pranksounds.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.PhotoModel

@BindingAdapter("android:bindThumbnailFile")
fun ImageView.bindThumbFile(photoModel: PhotoModel?) {
    if (photoModel != null) {
        Glide.with(this).load(photoModel.file)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image).into(this)
    }
    else {
        Glide.with(this).load(R.drawable.default_image)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image).into(this)
    }
}