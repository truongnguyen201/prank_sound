package com.hola360.pranksounds.ui.home

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Prank

@Suppress("UNCHECKED_CAST")
class HomeViewModel(app: Application) : ViewModel(){

    val mPrankList = listOf<Prank>(
        Prank(1, R.drawable.ic_hair_cutting, "Hair Cutting"),
        Prank(2, R.drawable.ic_broken_screen, "Broken Screen"),
        Prank(3, R.drawable.ic_call_screen, "Call Screen"),
        Prank(4, R.drawable.ic_sound_funny, "Sound Funny"),
        Prank(5, R.drawable.ic_taser_prank, "Taser Prank"),
        Prank(6, R.drawable.ic_setting, "Setting")
        )

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
                return HomeViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}