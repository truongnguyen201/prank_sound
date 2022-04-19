package com.hola360.pranksounds.ui.splash

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.utils.Constants

@Suppress("UNCHECKED_CAST")
class SplashViewModel(app: Application) : ViewModel() {
    var action: Any
    //use SharedPreferences to save state accept policy
    private var sharedPreferences: SharedPreferences =
        app.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    init {
        action = if (sharedPreferences.getBoolean("isAcceptPolicy", false)) {
            SplashFragmentDirections.actionGlobalHomeFragment()
        } else {
            SplashFragmentDirections.actionGlobalPolicyFragment()
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                return SplashViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}