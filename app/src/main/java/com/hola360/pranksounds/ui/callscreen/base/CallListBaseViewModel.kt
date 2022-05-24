package com.hola360.pranksounds.ui.callscreen.base

import android.app.Application
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository

abstract class CallListBaseViewModel(application: Application) : ViewModel(){
    protected var repository: PhoneBookRepository = PhoneBookRepository(application)
    val phoneBookLiveData = MutableLiveData<DataResponse<List<Call>>>()

    init {
        phoneBookLiveData.value = DataResponse.DataIdle()
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(phoneBookLiveData) {
        phoneBookLiveData.value!!.loadingStatus == LoadingStatus.Error
    }

    val isLoading: LiveData<Boolean> = Transformations.map(phoneBookLiveData) {
        phoneBookLiveData.value!!.loadingStatus == LoadingStatus.Loading
    }

    abstract fun getPhoneBook()
    abstract fun retry()
}