package com.hola360.pranksounds.ui.callscreen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.pranksounds.data.api.response.ResultDataResponse
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.ui.callscreen.data.ShareViewModelStatus
import com.hola360.pranksounds.utils.SingletonHolder

class CallScreenSharedViewModel(private val app: Application) : ViewModel() {

    val resultLiveData =
        MutableLiveData<ResultDataResponse<Call>>(ResultDataResponse.ResultDataEmpty())

    private var _myCall = MutableLiveData<Call?>()
    val myCall: LiveData<Call?> = _myCall


    companion object :
        SingletonHolder<CallScreenSharedViewModel, Application>(::CallScreenSharedViewModel)

    private var _useStatus = MutableLiveData(ShareViewModelStatus.Default)
    val useStatus: LiveData<ShareViewModelStatus> = _useStatus

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

    fun setResultData(resultCode: Int, call: Call) {
        resultLiveData.postValue(ResultDataResponse.ResultDataSuccess(resultCode, call).copy())
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