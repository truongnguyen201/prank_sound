package com.hola360.pranksounds.ui.call_screen

import android.app.Application
import android.telecom.Call
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CallScreenViewModel(app: Application) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CallScreenViewModel::class.java)){
                return CallScreenViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}