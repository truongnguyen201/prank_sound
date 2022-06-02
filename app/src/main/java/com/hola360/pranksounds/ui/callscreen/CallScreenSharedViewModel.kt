package com.hola360.pranksounds.ui.callscreen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.sound_funny.detail_category.SharedViewModel

class CallScreenSharedViewModel(private val app: Application): ViewModel() {
    private var _myCall = MutableLiveData<Call?>()
    val myCall: LiveData<Call?> = _myCall

    var isBackToMyCaller = MutableLiveData<Boolean>(false)

    fun setCall(call: Call?) {
        _myCall.postValue(call)
    }
    fun getCall(): Call? {
        return _myCall.value
    }

    fun setBackToMyCaller(boolean: Boolean) {
        isBackToMyCaller.postValue(boolean)
    }
    fun isBackToMyCaller(): Boolean {
        return isBackToMyCaller.value!!
    }

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CallScreenSharedViewModel::class.java)) {
                return CallScreenSharedViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown view model class")
        }
    }
}