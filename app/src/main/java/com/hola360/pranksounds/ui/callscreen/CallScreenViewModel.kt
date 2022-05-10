package com.hola360.pranksounds.ui.callscreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hola360.pranksounds.data.api.response.DataResponse
import com.hola360.pranksounds.data.api.response.LoadingStatus
import com.hola360.pranksounds.data.model.Call
import com.hola360.pranksounds.data.repository.PhoneBookRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class CallScreenViewModel(app: Application) : ViewModel() {
    private val repository = PhoneBookRepository(app)
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

    fun getPhoneBook() {
        if (phoneBookLiveData.value!!.loadingStatus != LoadingStatus.Loading) {
            phoneBookLiveData.value = DataResponse.DataLoading(LoadingStatus.Loading)

            viewModelScope.launch {
                try {
                    val listFromApi: Deferred<MutableList<Call>> =
                        async {

                            repository.getPhoneBook("call_screen")
                                ?.data
                                ?.dataApps
                                ?.phoneBook ?: mutableListOf()
                        }

                    val listFromLocal: Deferred<List<Call>> =
                        async {
                            repository.getPhoneBookFromLocal()!!
                        }

                    val listPhoneBook = listFromApi.await()
                    val listPhoneBookFromLocal = listFromLocal.await()
                    if (listPhoneBook.isNullOrEmpty()) {
                        phoneBookLiveData.value = DataResponse.DataError()
                    }
                    else {
                        if (!listPhoneBookFromLocal.isNullOrEmpty()) {
                            listPhoneBook.addAll(listPhoneBookFromLocal)
                        }
                        phoneBookLiveData.value = DataResponse.DataSuccess(listPhoneBook)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    fun retry() {
        getPhoneBook()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CallScreenViewModel::class.java)){
                return CallScreenViewModel(app) as T
            }

            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}