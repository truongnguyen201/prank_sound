package com.hola360.pranksounds.ui.sound_funny.detail_category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hola360.pranksounds.data.model.Sound

class SharedViewModel() : ViewModel() {
    var isComplete = MutableLiveData<Boolean>()
    var isPlaying = MutableLiveData<Boolean>()
    var soundList = MutableLiveData<MutableList<Sound>>()
    var currentPosition = MutableLiveData<Int>()
    var soundDuration = MutableLiveData<Int>()
    var seekBarProgress = MutableLiveData<Int>()

    init{
        soundList.value = mutableListOf()
    }
}
