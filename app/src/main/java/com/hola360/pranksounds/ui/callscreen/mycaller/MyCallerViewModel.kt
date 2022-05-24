package com.hola360.pranksounds.ui.callscreen.mycaller

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

class MyCallerViewModel(private val application: Application) : CallListBaseViewModel(application){
    override fun getPhoneBook() {
        if (phoneBookLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            phoneBookLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)

            viewModelScope.launch {
                try {
                    val listFromLocal =
                            repository.getPhoneBookFromLocal()!!

                    if (listFromLocal.isNullOrEmpty()) {
                        phoneBookLiveData.value = DataResponse.DataError()
                    }
                    else {
                        phoneBookLiveData.value = DataResponse.DataSuccess(listFromLocal)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun retry() {
        getPhoneBook()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MyCallerViewModel::class.java)){
                return MyCallerViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}