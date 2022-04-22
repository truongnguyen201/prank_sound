package com.hola360.pranksounds.ui.hair_cutting

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class HairCuttingViewModel(app: Application) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HairCuttingViewModel::class.java)) {
                return HairCuttingViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}