package com.hola360.pranksounds.utils.listener

import com.hola360.pranksounds.data.model.Sound

interface SoundListener {
    fun onCheckedButton(sound : Sound)
    fun onUncheckedButton(sound : Sound)
    fun onItemClick(position : Int)
}