package com.hola360.pranksounds.ui.policy

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.utils.Constants

@Suppress("UNCHECKED_CAST")
class PolicyViewModel(app: Application) : ViewModel() {

    private val sharedPreferences: SharedPreferences = app.getSharedPreferences(
        Constants.PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    fun setAcceptPolicy() {
        val prefEditor = sharedPreferences.edit()
        prefEditor.putBoolean("isAcceptPolicy", true)
        prefEditor.apply()
    }

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PolicyViewModel::class.java)) {
                return PolicyViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}