package com.hola360.pranksounds.ui.setting

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingViewModel(app: Application) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
                return SettingViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

}