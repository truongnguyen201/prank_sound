package com.hola360.pranksounds.ui.callscreen.trendcaller

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.ui.callscreen.base.CallListBaseViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

class TrendCallerViewModel(private val application: Application): CallListBaseViewModel(application) {
    override fun getPhoneBook() {
        if (phoneBookLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            phoneBookLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)

            viewModelScope.launch {
                try {
                    val listPhoneBook =
                            repository.getPhoneBook("call_screen")
                                ?.data
                                ?.dataApps
                                ?.phoneBook ?: mutableListOf()

                    if (listPhoneBook.isNullOrEmpty()) {
                        phoneBookLiveData.value = DataResponse.DataError()
                    }
                    else {
                        phoneBookLiveData.value = DataResponse.DataSuccess(listPhoneBook)
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
            if(modelClass.isAssignableFrom(TrendCallerViewModel::class.java)){
                return TrendCallerViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }

}