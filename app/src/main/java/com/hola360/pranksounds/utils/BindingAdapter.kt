package com.hola360.pranksounds.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.PhotoModel
import com.hola360.pranksounds.ui.callscreen.popup.ActionModel

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

@BindingAdapter("android:iconForAction")
fun ImageView.iconForAction(actionModel: ActionModel) {
    if (actionModel.icon == -1) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        setImageResource(actionModel.icon)
    }
}