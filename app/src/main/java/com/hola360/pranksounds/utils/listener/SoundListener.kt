package com.hola360.pranksounds.utils.listener

import android.view.View
import com.hola360.pranksounds.data.model.Sound

interface SoundListener {
    fun onCheckedButton(sound: Sound)
    fun onUncheckedButton(sound: Sound)
    fun onItemClick(position: Int)
    fun onMoreIconClick(view: View, position: Int)
    fun onFavoriteEmpty()
}