package com.hola360.pranksounds.ui.sound_funny.category

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class CategoryViewModel(app: Application) : ViewModel() {

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                return CategoryViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}