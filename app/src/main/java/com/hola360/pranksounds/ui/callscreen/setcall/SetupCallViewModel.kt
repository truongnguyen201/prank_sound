package com.hola360.pranksounds.ui.callscreen.setcall

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import com.hola360.pranksounds.ui.callscreen.callingscreen.receiver.CallingReceiver
import com.hola360.pranksounds.utils.Constants
import com.hola360.pranksounds.utils.Utils
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.coroutineContext

class SetupCallViewModel(val app: Application, val call: Call?) : ViewModel() {
    val callLiveData = MutableLiveData<Call>(null)
    var curCallModel: Call? = null
    val repository = PhoneBookRepository(app)
    var period = MutableLiveData<WaitCallPeriod>(WaitCallPeriod.Now)
    val startCallingLiveData = MutableLiveData<Call?>()

    init {
        curCallModel = call ?: Call()
        period.value = WaitCallPeriod.Now
        callLiveData.value = curCallModel
    }


    fun deleteCall() {
        viewModelScope.launch {
            repository.deleteCall(curCallModel!!)
        }
    }

    fun setCall(call: Call?) {
        curCallModel = call
        callLiveData.value = curCallModel
    }

    fun getCurrentCall(): Call? {
        return callLiveData.value
    }

    fun startCalling() {
        startCallingLiveData.value = null
        startCallingLiveData.value = curCallModel
    }

    val periodOfTime: LiveData<String> = Transformations.map(period) {
        when (period.value) {
            WaitCallPeriod.Now -> app.resources.getString(R.string.now)
            WaitCallPeriod.FiveSeconds -> app.resources.getString(R.string.five_seconds)
            WaitCallPeriod.ThirtySeconds -> app.resources.getString(R.string.thirty_seconds)
            WaitCallPeriod.OneMinute -> app.resources.getString(R.string.one_minute)
            else -> app.resources.getString(R.string.now)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application, val call: Call?) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SetupCallViewModel::class.java)) {
                return SetupCallViewModel(app, call) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}