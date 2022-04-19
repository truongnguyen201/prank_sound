package com.hola360.pranksounds.ui.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class SplashViewModel(app: Application) {
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SplashViewModel::class.java)){
                return SplashViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}