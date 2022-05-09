package com.hola360.pranksounds.utils.listener

import com.hola360.pranksounds.data.model.Sound

interface ControlPanelListener {
    fun onPlayPauseClick()
    fun onPanelClick(sound: Sound)
    fun onDetachFragment()
    fun isPlaying() : Boolean
    fun onSeekBarChange(fromUser: Boolean, progress: Int)
    fun onReset()
}