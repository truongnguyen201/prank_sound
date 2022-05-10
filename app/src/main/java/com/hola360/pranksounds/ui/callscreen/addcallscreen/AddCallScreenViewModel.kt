package com.hola360.pranksounds.ui.callscreen.addcallscreen

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddCallScreenViewModel: ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(AddCallScreenViewModel::class.java)){
                return AddCallScreenViewModel() as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}