package com.hola360.pranksounds.ui.callscreen.setcall

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hola360.pranksounds.data.model.Call

class SetupCallViewModel(app: Application, val call: Call?) : ViewModel() {
    val callLiveData = MutableLiveData<Call>(null)
    var curCallModel: Call? = null
    var period = MutableLiveData<WaitCallPeriod>(WaitCallPeriod.Now)

    init {
        curCallModel = call ?: Call()
        period.value = WaitCallPeriod.Now
    }

    fun setCall(call: Call?) {
        curCallModel = call
    }

    val periodOfTime: LiveData<String> = Transformations.map(period) {
        when (period.value) {
            WaitCallPeriod.Now -> "Now"
            WaitCallPeriod.FiveSeconds -> "5 seconds"
            WaitCallPeriod.ThirtySeconds -> "30 seconds"
            WaitCallPeriod.OneMinute -> "1 minute"
            else -> "Now"
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application, val call: Call?) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SetupCallViewModel::class.java)) {
                return SetupCallViewModel(app,call) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}