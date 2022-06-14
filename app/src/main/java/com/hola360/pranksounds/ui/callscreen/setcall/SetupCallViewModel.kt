package com.hola360.pranksounds.ui.callscreen.setcall

import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.R
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository

import kotlinx.coroutines.launch


class SetupCallViewModel(val app: Application) : ViewModel() {
    var callLiveData = MutableLiveData<Call>(null)
    var curCallModel: Call? = null
    val repository = PhoneBookRepository(app)
    var period = MutableLiveData(WaitCallPeriod.Now)
    val startCallingLiveData = MutableLiveData<Call?>()

    init {
        curCallModel = Call()
        period.value = WaitCallPeriod.Now
        callLiveData.postValue(curCallModel)
    }


    fun deleteCall() {
        viewModelScope.launch {
            repository.deleteCall(curCallModel!!)
        }
    }

    fun setCall(call: Call?) {
        curCallModel = call
        callLiveData.postValue(curCallModel)
    }

    fun getCurrentCall(): Call? {
        return curCallModel
    }

    fun startCalling() {
        startCallingLiveData.postValue(curCallModel)
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
    class Factory(private val app: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SetupCallViewModel::class.java)) {
                return SetupCallViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}