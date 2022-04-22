package com.hola360.pranksounds.ui.taser_prank

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class TaserPrankViewModel(app: Application) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(TaserPrankViewModel::class.java)){
                return TaserPrankViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}